package com.example.appanalyzer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.alexlytvynenko.appanalyzer.HttpNetworkAnalyzerInterceptor
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException

/**
 * Created by alex_litvinenko on 09.10.17.
 */
class MainActivity : AppCompatActivity() {

    private val ENDPOINT = "https://api.github.com/repos/square/okhttp/contributors"

    private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpNetworkAnalyzerInterceptor())
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.e("MainActivity", "OnCreate")
        sendRequest.setOnClickListener {
            Log.d("MainActivity", "Click")
            sendExampleRequest()
        }
    }

    private fun sendExampleRequest() {
        // Create request for remote resource.
        val request = Request.Builder()
                .url(ENDPOINT)
                .build()
        // Execute the request and retrieve the response.
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                runOnUiThread { status.text = "REQUEST IS FAILED !!!" }
            }

            override fun onResponse(call: Call?, response: Response) {
                runOnUiThread {
                    if (!response.isSuccessful) status.text = "Request is failed!"
                    else status.text = "Request is successful! Open ${getString(R.string.app_analyzer_display_activity_label)} for more information"
                }
            }
        })
    }
}