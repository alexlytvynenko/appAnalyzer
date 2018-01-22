package com.alexlytvynenko.appanalyzer.internal.ui.list.viewHolder

import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import com.alexlytvynenko.appanalyzer.R
import com.alexlytvynenko.appanalyzer.internal.entity.RequestEntity
import com.alexlytvynenko.appanalyzer.internal.toDateTimeFormat
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item.*
import java.util.*

/**
 * Created by alex_litvinenko on 24.10.17.
 */
internal class RequestViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    internal fun bind(request: RequestEntity, position: Int, listener: (item: ItemViewHolder) -> Unit) {
        body.ignoreTrimming = true
        val dateTime = Date(request.startDateInMS).toDateTimeFormat()
        val text = SpannableString("${position + 1}. $dateTime ${request.method} ${request.status} ${request.url}" +
                " ${request.duration}")

        val dateTimeStart = text.indexOf(dateTime)
        val positionStart = text.indexOf((position + 1).toString())
        val methodStart = text.indexOf(request.method)
        val statusStart = text.indexOf(request.status)
        val durationStart = text.indexOf(request.duration)

        val statusColor =
                if (request.status.startsWith("2")) R.color.app_analyzer_green_color
                else R.color.app_analyzer_red_color

        text.setSpan(ForegroundColorSpan(ContextCompat.getColor(body.context,
                R.color.app_analyzer_light_blue_color)), dateTimeStart,
                dateTimeStart + dateTime.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        text.setSpan(StyleSpan(Typeface.BOLD), positionStart,
                positionStart + (position + 1).toString().length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        text.setSpan(ForegroundColorSpan(ContextCompat.getColor(body.context,
                R.color.app_analyzer_light_orange_color)), methodStart,
                methodStart + request.method.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        text.setSpan(ForegroundColorSpan(ContextCompat.getColor(body.context,
                statusColor)), statusStart,
                statusStart + request.status.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        text.setSpan(ForegroundColorSpan(ContextCompat.getColor(body.context,
                R.color.app_analyzer_purple_color)), durationStart,
                durationStart + request.duration.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        body.text = text
        body.setOnClickListener { listener(request) }
    }
}