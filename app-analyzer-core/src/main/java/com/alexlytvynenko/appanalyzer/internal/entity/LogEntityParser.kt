package com.alexlytvynenko.appanalyzer.internal.entity

import org.jetbrains.anko.db.RowParser

/**
 * Created by alex_litvinenko on 25.10.17.
 */
internal class LogEntityParser : RowParser<LogEntity> {
    override fun parseRow(columns: Array<Any?>): LogEntity {
        return LogEntity(id = (columns[0] as Double).toLong(), startDateInMS = (columns[1] as Double).toLong(),
                processThread = columns[2] as String, priority = columns[3] as String, tag = columns[4] as String,
                text = columns[5] as String)
    }
}