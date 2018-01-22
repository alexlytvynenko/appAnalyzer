package com.alexlytvynenko.appanalyzer.internal.entity

import com.alexlytvynenko.appanalyzer.internal.NetworkAnalyzerInternal
import org.jetbrains.anko.db.RowParser

/**
 * Created by alex_litvinenko on 11.10.17.
 */
internal class RequestEntityParser : RowParser<RequestEntity> {
    override fun parseRow(columns: Array<Any?>): RequestEntity {
        return RequestEntity(method = columns[0] as String,
                id = (columns[1] as Double).toLong(), startDateInMS = (columns[2] as Double).toLong(),
                url = columns[3] as String,
                requestHeaders = NetworkAnalyzerInternal.convertFromBytes(columns[4] as ByteArray) as HashMap<String, String>,
                requestBody = columns[5] as String, status = columns[6] as String,
                duration = columns[7] as String,
                responseHeaders = NetworkAnalyzerInternal.convertFromBytes(columns[8] as ByteArray) as HashMap<String, String>,
                responseBody = columns[9] as String)
    }
}