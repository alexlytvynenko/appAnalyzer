package com.example.appanalyzer

import android.app.Application
import com.alexlytvynenko.appanalyzer.AppAnalyzer

/**
 * Created by alex_litvinenko on 09.10.17.
 */
class ExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupAppAnalyzer()
    }

    private fun setupAppAnalyzer() {
        AppAnalyzer.install(this)
    }
}