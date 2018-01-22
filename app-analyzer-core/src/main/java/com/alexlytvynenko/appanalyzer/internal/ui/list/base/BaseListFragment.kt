package com.alexlytvynenko.appanalyzer.internal.ui.list.base

import android.app.Fragment
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.DOWN
import android.support.v7.widget.helper.ItemTouchHelper.UP
import android.text.Html
import android.view.*
import com.alexlytvynenko.appanalyzer.R
import com.alexlytvynenko.appanalyzer.internal.screenHeight
import com.alexlytvynenko.appanalyzer.internal.ui.LayoutController
import com.alexlytvynenko.appanalyzer.internal.ui.list.viewHolder.ItemViewHolder
import kotlinx.android.synthetic.main.fragment_list.*
import android.view.MenuInflater
import com.alexlytvynenko.appanalyzer.internal.consume
import com.alexlytvynenko.appanalyzer.internal.ui.list.RequestListFragment
import org.jetbrains.anko.*
import android.support.v7.widget.SearchView
import android.widget.EditText

/**
 * Created by alex_litvinenko on 24.10.17.
 */
internal abstract class BaseListFragment<Entity : ItemViewHolder> : Fragment() {

    protected var items = arrayListOf<Entity>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_list, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        if (!isFeatureEnabled()) initError()
    }

    override fun onResume() {
        super.onResume()
        if (isFeatureEnabled()) loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.root_menu, menu)
        if (this is RequestListFragment) menu.findItem(R.id.search).isVisible = false
        else {
            val searchView = menu.findItem(R.id.search).actionView as SearchView
            searchView.find<EditText>(android.support.v7.appcompat.R.id.search_src_text).setTextColor(Color.WHITE)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String) = false

                override fun onQueryTextChange(newText: String): Boolean {
                    filterData(newText.trim())
                    return true
                }
            })
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!isFeatureEnabled()) return super.onOptionsItemSelected(item)
        return when (item.itemId) {
            R.id.clear -> {
                consume { deleteAll() }
            }
            R.id.share -> {
                consume { share() }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initList() {
        list.setHasFixedSize(true)
        list.layoutManager = LinearLayoutManager(activity)
        list.adapter = BaseListAdapter<Entity>()
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                val position = viewHolder?.adapterPosition
                delete(position!!)
            }
        })
        itemTouchHelper.attachToRecyclerView(list)
        (list.adapter as BaseListAdapter<*>).onItemClickListener = {
            onItemClicked(it)
        }
    }

    private fun initError() {
        text.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            text.text = Html.fromHtml(getErrorText(), Html.FROM_HTML_MODE_LEGACY)
        } else {
            text.text = Html.fromHtml(getErrorText())
        }
    }

    fun filterData(query: String) {
        doAsync {
            val data = filterItems(query)
            uiThread {
                list?.let {
                    (it.adapter as BaseListAdapter<Entity>).setData(data)
                    checkScroll()
                }
            }
        }
    }

    fun loadData() {
        doAsync {
            val data = loadItems()
            items.clear()
            items.addAll(data)
            uiThread {
                list?.let {
                    (it.adapter as BaseListAdapter<Entity>).setData(items)
                    checkScroll()
                }
            }
        }
    }

    private fun deleteAll() {
        alert("Are you sure?") {
            positiveButton("Yes") {
                (list.adapter as BaseListAdapter<Entity>).setData(arrayListOf())
                doAsync {
                    removeAll()
                    checkScroll()
                }
            }
            negativeButton("No") {}
        }.show()
    }

    private fun delete(position: Int) {
        val adapter = list.adapter as BaseListAdapter<Entity>
        val entity = adapter.entities[position]
        adapter.removeItem(position)
        doAsync {
            removeItem(entity)
            checkScroll()
        }
    }

    private fun checkScroll() {
        list.postDelayed({
            list?.let {
                val canScroll = it.canScrollVertically(DOWN) || it.canScrollVertically(UP)
                val actionBarHeight = (activity as AppCompatActivity).supportActionBar?.height ?: 0
                val overHeight = if (view.height < screenHeight() - actionBarHeight)
                    view.height - it.height < actionBarHeight
                else view.height - actionBarHeight - it.height < actionBarHeight
                if (canScroll || overHeight) (activity as LayoutController).enableScroll()
                else (activity as LayoutController).disableScroll()
            }
        }, 100)
    }

    internal abstract fun isFeatureEnabled(): Boolean
    internal abstract fun getErrorText(): String
    internal abstract fun share()
    internal abstract fun loadItems(): List<Entity>
    internal abstract fun filterItems(query: String): List<Entity>
    internal abstract fun removeItem(entity: Entity)
    internal abstract fun removeAll()
    internal abstract fun onItemClicked(entity: ItemViewHolder)
}