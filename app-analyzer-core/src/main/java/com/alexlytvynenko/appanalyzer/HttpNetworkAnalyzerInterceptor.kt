package com.alexlytvynenko.appanalyzer

import com.alexlytvynenko.appanalyzer.internal.NetworkAnalyzerInternal
import com.alexlytvynenko.appanalyzer.internal.entity.RequestEntity
import java.nio.charset.Charset
import okhttp3.*
import okhttp3.internal.http.HttpHeaders
import okio.Buffer
import java.io.EOFException
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by alex_litvinenko on 10.10.17.
 */
class HttpNetworkAnalyzerInterceptor : Interceptor {
    private val UTF8 = Charset.forName("UTF-8")

    init {
        NetworkAnalyzerInternal.isNetworkInterceptorInited = true
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        if (NetworkAnalyzerInternal.disabledRequests) return chain.proceed(chain.request())

        var responseStatus = "In process"
        val request = chain.request()
        val requestBody = request.body()
        val hasRequestBody = requestBody != null && requestBody.contentLength() > 0
        val requestHeaders = request.headers()
        var i = 0
        val requestHeadersMap = hashMapOf<String, String>()
        while (i < requestHeaders.size()) {
            requestHeadersMap.put(requestHeaders.name(i), requestHeaders.value(i))
            i++
        }

        val requestBodyString: String
        if (!hasRequestBody) {
            requestBodyString = "No request body"
        } else if (bodyEncoded(request.headers())) {
            requestBodyString = "Encoded request body omitted"
        } else {
            val buffer = Buffer()
            requestBody!!.writeTo(buffer)

            var charset = UTF8
            val contentType = requestBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8)
            }

            requestBodyString =
                    if (isPlaintext(buffer)) buffer.readString(charset)
                    else "Binary ${requestBody.contentLength()} -byte body omitted"
        }

        val startNs = System.nanoTime()
        val startMs = System.currentTimeMillis()
        val requestEntity = RequestEntity(method = request.method(), id = startNs,
                startDateInMS = startMs, requestHeaders = requestHeadersMap,
                requestBody = requestBodyString,
                url = request.url().toString(), status = responseStatus)
        NetworkAnalyzerInternal.saveRequestToDatabase(requestEntity)

        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            responseStatus = "FAILED: $e"
            requestEntity.status = responseStatus
            requestEntity.duration = "0 ms"
            requestEntity.responseBody = "No response body"
            NetworkAnalyzerInternal.saveRequestToDatabase(requestEntity, true)
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body()
        val contentLength = responseBody?.contentLength()

        responseStatus = "${response.code()} ${response.message()}"
        requestEntity.duration = "$tookMs ms"
        requestEntity.status = responseStatus

        val responseHeaders = response.headers()
        val responseHeadersMap = hashMapOf<String, String>()
        var j = 0
        while (j < responseHeaders.size()) {
            responseHeadersMap.put(responseHeaders.name(j), responseHeaders.value(j))
            j++
        }
        requestEntity.responseHeaders = responseHeadersMap

        val responseBodyString: String
        if (!HttpHeaders.hasBody(response) || contentLength!! < 0) {
            responseBodyString = "No response body"
        } else if (bodyEncoded(response.headers())) {
            responseBodyString = "Encoded response body omitted"
        } else {
            val source = responseBody?.source()
            source?.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source!!.buffer()

            var charset = UTF8
            val contentType = responseBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8)
            }

            if (!isPlaintext(buffer)) {
                responseBodyString = "Binary ${buffer.size()} -byte body omitted"
                requestEntity.responseBody = responseBodyString
                return response
            }

            responseBodyString =
                    if (contentLength != 0L) buffer.clone().readString(charset)
                    else ""
        }
        requestEntity.responseBody = responseBodyString
        NetworkAnalyzerInternal.saveRequestToDatabase(requestEntity, true)
        return response
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private fun isPlaintext(buffer: Buffer): Boolean {
        try {
            val prefix = Buffer()
            val byteCount = (if (buffer.size() < 64) buffer.size() else 64).toLong()
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            return true
        } catch (e: EOFException) {
            return false // Truncated UTF-8 sequence.
        }

    }

    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }
}