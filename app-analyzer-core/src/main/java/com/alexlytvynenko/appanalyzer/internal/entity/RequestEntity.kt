package com.alexlytvynenko.appanalyzer.internal.entity

import android.annotation.SuppressLint
import android.os.Parcelable
import com.alexlytvynenko.appanalyzer.internal.toDateTimeFormat
import com.alexlytvynenko.appanalyzer.internal.ui.list.viewHolder.ItemViewHolder
import com.alexlytvynenko.appanalyzer.internal.ui.list.viewHolder.VH_REQUEST
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by alex_litvinenko on 11.10.17.
 */
@SuppressLint("ParcelCreator")
@Parcelize
internal data class RequestEntity(val method: String, val id: Long, val startDateInMS: Long,
                                  val url: String, val requestHeaders: Map<String, String>,
                                  val requestBody: String, var status: String, var duration: String = "",
                                  var responseHeaders: Map<String, String> = hashMapOf(), var responseBody: String = "") : Parcelable, ItemViewHolder {

    internal fun detailsCount() = 9

    internal fun requestHeaderString(): String {
        val requestHeaderString = StringBuilder()
        for ((key, value) in requestHeaders) {
            if (requestHeaderString.isNotEmpty()) requestHeaderString.append("\n")
            requestHeaderString.append("$key: $value")
        }
        if (requestHeaderString.isEmpty()) requestHeaderString.append("No request headers")
        return requestHeaderString.toString()
    }

    internal fun responseHeaderString(): String {
        val responseHeaderString = StringBuilder()
        for ((key, value) in responseHeaders) {
            if (responseHeaderString.isNotEmpty()) responseHeaderString.append("\n")
            responseHeaderString.append("$key: $value")
        }
        if (responseHeaderString.isEmpty()) responseHeaderString.append("No response headers")
        return responseHeaderString.toString()
    }

    internal fun toShareData() =
            "URL $url\n" +
                    "Method $method\n" +
                    "Status $status\n" +
                    "Duration $duration\n" +
                    "Started at ${Date(startDateInMS).toDateTimeFormat()}\n" +
                    "Request Headers:\n${requestHeaderString()}\n" +
                    "Request Body:\n$requestBody\n" +
                    "Response Headers:\n${responseHeaderString()}\n" +
                    "Response Body:\n$responseBody\n" +
                    "------------------------------------\n\n"

    internal companion object {
        val TABLE_NAME = "requests"
        val COLUMN_METHOD = "method"
        val COLUMN_ID = "id"
        val COLUMN_START_DATE = "start_date"
        val COLUMN_URL = "url"
        val COLUMN_REQUEST_HEADERS = "request_headers"
        val COLUMN_REQUEST_BODY = "request_body"
        val COLUMN_STATUS = "status"
        val COLUMN_DURATION = "duration"
        val COLUMN_RESPONSE_HEADERS = "response_headers"
        val COLUMN_RESPONSE_BODY = "response_body"

        internal val dateComparator = Comparator<RequestEntity> { r0, r1 ->
            r1.startDateInMS.compareTo(r0.startDateInMS)
        }
    }

    override fun getType() = VH_REQUEST
}
