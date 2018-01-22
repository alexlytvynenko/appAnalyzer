package com.alexlytvynenko.appanalyzer

import android.app.Application
import android.content.Context
import com.alexlytvynenko.appanalyzer.internal.ui.DisplayNetworkActivity
import com.alexlytvynenko.appanalyzer.internal.NetworkAnalyzerInternal
import org.jetbrains.anko.doAsync

/**
 * Created by alex_litvinenko on 09.10.17.
 */
object NetworkAnalyzer {

    fun disabledLogs(isDisabled: Boolean): NetworkAnalyzer {
        NetworkAnalyzerInternal.disabledLogs = isDisabled
        return this
    }

    fun disabledExceptions(isDisabled: Boolean): NetworkAnalyzer {
        NetworkAnalyzerInternal.disabledExceptions = isDisabled
        return this
    }

    fun disabledRequests(isDisabled: Boolean): NetworkAnalyzer {
        NetworkAnalyzerInternal.disabledRequests = isDisabled
        return this
    }

    fun disabled(isDisabled: Boolean): NetworkAnalyzer {
        NetworkAnalyzerInternal.disabledLogs = isDisabled
        NetworkAnalyzerInternal.disabledExceptions = isDisabled
        NetworkAnalyzerInternal.disabledRequests = isDisabled
        return this
    }

    fun install(application: Application) {
        if (!NetworkAnalyzerInternal.disabledLogs || !NetworkAnalyzerInternal.disabledExceptions
                || !NetworkAnalyzerInternal.disabledRequests) {
            enableDisplayNetworkActivity(application)
            if (!NetworkAnalyzerInternal.disabledLogs || !NetworkAnalyzerInternal.disabledExceptions) {
                doAsync {
                    NetworkAnalyzerInternal.runLogObserver()
                }
            }
        }
    }

    private fun enableDisplayNetworkActivity(context: Context) {
        NetworkAnalyzerInternal.setEnabled(context, DisplayNetworkActivity::class.java, true)
    }
}