package com.alexlytvynenko.appanalyzer.internal.ui.list.viewHolder

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import com.alexlytvynenko.appanalyzer.R
import com.alexlytvynenko.appanalyzer.internal.entity.LogEntity
import com.alexlytvynenko.appanalyzer.internal.toDateTimeMSFormat
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item.*
import java.util.*

/**
 * Created by alex_litvinenko on 24.10.17.
 */
internal class LogViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    internal fun bind(data: LogEntity) {
        body.ignoreTrimming = true
        val dateTime = Date(data.startDateInMS).toDateTimeMSFormat()
        val text = SpannableString("$dateTime ${data.processThread} ${data.priority}/${data.tag}: ${data.text}")

        val dateTimeStart = text.indexOf(dateTime)
        val processThread = text.indexOf(data.processThread)
        val priority = text.indexOf("${data.priority}/")
        val tag = text.indexOf(data.tag)
        val msg = text.indexOf(data.text)

        val textColor =
                if (data.priority == "E") R.color.app_analyzer_red_color
                else android.R.color.darker_gray

        text.setSpan(ForegroundColorSpan(ContextCompat.getColor(body.context,
                R.color.app_analyzer_light_blue_color)), dateTimeStart,
                dateTimeStart + dateTime.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        text.setSpan(ForegroundColorSpan(ContextCompat.getColor(body.context,
                R.color.app_analyzer_purple_color)), processThread,
                processThread + data.processThread.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        text.setSpan(ForegroundColorSpan(ContextCompat.getColor(body.context,
                R.color.app_analyzer_green_color)), priority,
                priority + data.priority.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        text.setSpan(ForegroundColorSpan(ContextCompat.getColor(body.context,
                R.color.app_analyzer_light_orange_color)), tag,
                tag + data.tag.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        text.setSpan(ForegroundColorSpan(ContextCompat.getColor(body.context, textColor)), msg,
                msg + data.text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        body.text = text
    }
}