package com.example.appanalyzer

import android.app.Application
import com.alexlytvynenko.appanalyzer.NetworkAnalyzer

/**
 * Created by alex_litvinenko on 09.10.17.
 */
class ExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupNetworkAnalyzer()
    }

    private fun setupNetworkAnalyzer() {
        NetworkAnalyzer.install(this)
    }
}