package com.alexlytvynenko.appanalyzer

import android.app.Application
import android.content.Context
import com.alexlytvynenko.appanalyzer.internal.ui.DisplayAnalyzerActivity
import com.alexlytvynenko.appanalyzer.internal.AppAnalyzerInternal
import org.jetbrains.anko.doAsync

/**
 * Created by alex_litvinenko on 09.10.17.
 */
object AppAnalyzer {

    fun disabledLogs(isDisabled: Boolean): AppAnalyzer {
        AppAnalyzerInternal.disabledLogs = isDisabled
        return this
    }

    fun disabledExceptions(isDisabled: Boolean): AppAnalyzer {
        AppAnalyzerInternal.disabledExceptions = isDisabled
        return this
    }

    fun disabledRequests(isDisabled: Boolean): AppAnalyzer {
        AppAnalyzerInternal.disabledRequests = isDisabled
        return this
    }

    fun disabled(isDisabled: Boolean): AppAnalyzer {
        AppAnalyzerInternal.disabledLogs = isDisabled
        AppAnalyzerInternal.disabledExceptions = isDisabled
        AppAnalyzerInternal.disabledRequests = isDisabled
        return this
    }

    fun install(application: Application) {
        if (!AppAnalyzerInternal.disabledLogs || !AppAnalyzerInternal.disabledExceptions
                || !AppAnalyzerInternal.disabledRequests) {
            enableDisplayAnalyzerActivity(application)
            if (!AppAnalyzerInternal.disabledLogs || !AppAnalyzerInternal.disabledExceptions) {
                doAsync {
                    AppAnalyzerInternal.runLogObserver()
                }
            }
        }
    }

    private fun enableDisplayAnalyzerActivity(context: Context) {
        AppAnalyzerInternal.setEnabled(context, DisplayAnalyzerActivity::class.java, true)
    }
}