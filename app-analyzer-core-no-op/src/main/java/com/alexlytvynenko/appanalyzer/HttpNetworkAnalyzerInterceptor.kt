package com.alexlytvynenko.appanalyzer

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by alex_lytvynenko on 05.01.2018.
 */
class HttpNetworkAnalyzerInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}