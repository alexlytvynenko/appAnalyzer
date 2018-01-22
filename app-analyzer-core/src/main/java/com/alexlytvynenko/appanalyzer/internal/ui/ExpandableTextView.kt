package com.alexlytvynenko.appanalyzer.internal.ui

import android.support.v7.widget.AppCompatTextView
import android.animation.ObjectAnimator
import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.widget.TextView
import com.alexlytvynenko.appanalyzer.R

/**
 * Created by alex_litvinenko on 22.10.17.
 */
internal class ExpandableTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr) {
    private val TRIM_LENGTH = 150
    private val ELLIPSIS = "..."

    private var originalText: CharSequence = ""
    private var trimmedText: CharSequence = ""
    private var bufferType: TextView.BufferType = BufferType.SPANNABLE
    private var expanded: Boolean = false
    var ignoreTrimming: Boolean = false

    init {
        setOnClickListener {
            if (!expanded) {
                expanded = true
                ignoreTrimming = true
                val collapsedLineCount = lineCount
                text = originalText
                maxLines = collapsedLineCount
                val animation = ObjectAnimator.ofInt(this, "maxLines", 1000)
                animation.setDuration(1000).start()
                onExpandListener()
            }
        }
    }

    private fun setText() {
        super.setText(trimmedText, bufferType)
    }

    override fun setText(text: CharSequence, type: TextView.BufferType) {
        originalText = text
        trimmedText = getTrimmedText()
        bufferType = type
        setText()
    }

    private fun getTrimmedText(): CharSequence {
        return if (!ignoreTrimming && originalText.length > TRIM_LENGTH) {
            val more = "More"
            val spanTxt = SpannableStringBuilder(originalText, 0, TRIM_LENGTH).append(ELLIPSIS).append(" ").append(more)
            spanTxt.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.app_analyzer_light_orange_color)),
                    spanTxt.toString().indexOf(more),
                    spanTxt.toString().indexOf(more) + more.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spanTxt
        } else {
            originalText
        }
    }

    fun getOriginalText(): CharSequence {
        return originalText
    }

    internal var onExpandListener: () -> Unit = {}
}