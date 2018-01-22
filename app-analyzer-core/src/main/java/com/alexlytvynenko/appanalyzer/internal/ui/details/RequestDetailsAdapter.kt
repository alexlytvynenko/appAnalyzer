package com.alexlytvynenko.appanalyzer.internal.ui.details

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import com.alexlytvynenko.appanalyzer.R
import com.alexlytvynenko.appanalyzer.internal.entity.RequestEntity
import com.alexlytvynenko.appanalyzer.internal.inflate
import com.alexlytvynenko.appanalyzer.internal.toDateTimeFormat
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item.*
import java.util.*

/**
 * Created by alex_litvinenko on 22.10.17.
 */
internal class RequestDetailsAdapter(private val request: RequestEntity) : RecyclerView.Adapter<RequestDetailsAdapter.RequestDetailsViewHolder>() {

    internal var onClickListener: () -> Unit = {}

    override fun onBindViewHolder(holder: RequestDetailsViewHolder, position: Int) =
            holder.bind(request, position, onClickListener)

    override fun getItemCount() = request.detailsCount()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RequestDetailsViewHolder(parent.inflate(R.layout.list_item))

    internal class RequestDetailsViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        internal fun bind(request: RequestEntity, position: Int, listener: () -> Unit) {
            val text: SpannableString
            when (position) {
                0 -> {
                    val date = "Start date\n"
                    text = SpannableString(date + Date(request.startDateInMS).toDateTimeFormat())
                    text.setSpan(ForegroundColorSpan(ContextCompat.getColor(body.context,
                            R.color.app_analyzer_light_blue_color)), 0, date.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                }
                1 -> {
                    val method = "Method\n"
                    text = SpannableString(method + request.method)
                    text.setSpan(ForegroundColorSpan(ContextCompat.getColor(body.context,
                            R.color.app_analyzer_light_blue_color)), 0, method.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                2 -> {
                    val url = "URL\n"
                    text = SpannableString(url + request.url)
                    text.setSpan(ForegroundColorSpan(ContextCompat.getColor(body.context,
                            R.color.app_analyzer_light_blue_color)), 0, url.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                3 -> {
                    val requestHeaders = "Request headers\n"
                    text = SpannableString(requestHeaders + request.requestHeaderString())
                    text.setSpan(ForegroundColorSpan(ContextCompat.getColor(body.context,
                            R.color.app_analyzer_light_blue_color)), 0, requestHeaders.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                4 -> {
                    val requestBody = "Request body\n"
                    text = SpannableString(requestBody + request.requestBody)
                    text.setSpan(ForegroundColorSpan(ContextCompat.getColor(body.context,
                            R.color.app_analyzer_light_blue_color)), 0, requestBody.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                5 -> {
                    val status = "Status\n"
                    text = SpannableString(status + request.status)
                    text.setSpan(ForegroundColorSpan(ContextCompat.getColor(body.context,
                            R.color.app_analyzer_light_blue_color)), 0, status.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                6 -> {
                    val duration = "Duration\n"
                    text = SpannableString(duration + request.duration)
                    text.setSpan(ForegroundColorSpan(ContextCompat.getColor(body.context,
                            R.color.app_analyzer_light_blue_color)), 0, duration.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                7 -> {
                    val responseHeaders = "Response headers\n"
                    text = SpannableString(responseHeaders + request.responseHeaderString())
                    text.setSpan(ForegroundColorSpan(ContextCompat.getColor(body.context,
                            R.color.app_analyzer_light_blue_color)), 0, responseHeaders.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                8 -> {
                    val responseBody = "Response body\n"
                    text = SpannableString(responseBody + request.responseBody)
                    text.setSpan(ForegroundColorSpan(ContextCompat.getColor(body.context,
                            R.color.app_analyzer_light_blue_color)), 0, responseBody.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                else -> text = SpannableString("")
            }
            body.text = text
            body.onExpandListener = listener
        }
    }
}