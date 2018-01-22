package com.alexlytvynenko.appanalyzer.internal.entity

import android.annotation.SuppressLint
import android.os.Parcelable
import com.alexlytvynenko.appanalyzer.internal.toDateTimeMSFormat
import com.alexlytvynenko.appanalyzer.internal.ui.list.viewHolder.ItemViewHolder
import com.alexlytvynenko.appanalyzer.internal.ui.list.viewHolder.VH_LOG
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by alex_litvinenko on 24.10.17.
 */
@SuppressLint("ParcelCreator")
@Parcelize
internal data class LogEntity(val id: Long, val startDateInMS: Long, val processThread: String,
                              val priority: String, val tag: String, val text: String) : ItemViewHolder, Parcelable {

    companion object {
        val LOG_TABLE_NAME = "logs"
        val EXCEPTION_TABLE_NAME = "exceptions"
        val COLUMN_ID = "id"
        val COLUMN_START_DATE = "start_date"
        val COLUMN_PROCESS_THREAD = "process_thread"
        val COLUMN_PRIORITY = "priority"
        val COLUMN_TAG = "tag"
        val COLUMN_TEXT = "text"

        internal val dateComparator = Comparator<LogEntity> { e0, e1 ->
            e1.startDateInMS.compareTo(e0.startDateInMS)
        }
    }

    override fun getType() = VH_LOG

    internal fun toShareData() =
            "${Date(startDateInMS).toDateTimeMSFormat()} $processThread $priority/$tag: $text\n"
}