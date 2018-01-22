package com.alexlytvynenko.appanalyzer.internal.ui.list.base

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.alexlytvynenko.appanalyzer.R
import com.alexlytvynenko.appanalyzer.internal.inflate
import com.alexlytvynenko.appanalyzer.internal.ui.list.viewHolder.*

/**
 * Created by alex_litvinenko on 24.10.17.
 */
internal class BaseListAdapter<Entity : ItemViewHolder> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    internal var onItemClickListener: (item: ItemViewHolder) -> Unit = { _ -> }
    internal var entities = arrayListOf<Entity>()
        private set(value) {}

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
            when (getItemViewType(position)) {
                VH_REQUEST -> (holder as RequestViewHolder).bind(entities[position].getData(), position, onItemClickListener)
                else -> (holder as LogViewHolder).bind(entities[position].getData())
            }

    override fun getItemCount() = entities.size

    override fun getItemViewType(position: Int) = entities[position].getType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                VH_REQUEST -> RequestViewHolder(parent.inflate(R.layout.list_item))
                else -> LogViewHolder(parent.inflate(R.layout.list_item))
            }

    internal fun setData(data: List<Entity>) {
        entities.clear()
        entities.addAll(data)
        notifyDataSetChanged()
    }

    internal fun removeItem(position: Int) {
        entities.removeAt(position)
        notifyItemRemoved(position)
    }

}