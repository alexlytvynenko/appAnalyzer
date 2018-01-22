package com.alexlytvynenko.appanalyzer.internal.ui.list.viewHolder

/**
 * Created by alex_litvinenko on 24.10.17.
 */
internal const val VH_LOG = 0
internal const val VH_REQUEST = 1

internal interface ItemViewHolder {

    fun getType(): Int
}

internal inline fun <reified T> ItemViewHolder.getData() = this as T