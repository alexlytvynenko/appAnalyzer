package com.alexlytvynenko.appanalyzer.internal

import java.util.concurrent.ThreadFactory

/**
 * Created by alex_litvinenko on 09.10.17.
 */
internal class NetworkAnalyzerSingleThreadFactory(private val threadName: String) : ThreadFactory {

    override fun newThread(runnable: Runnable): Thread {
        return Thread(runnable, "NetworkAnalyzer-$threadName")
    }
}