package com.alexlytvynenko.appanalyzer.internal.ui.details

import android.app.Fragment
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import com.alexlytvynenko.appanalyzer.R
import com.alexlytvynenko.appanalyzer.internal.NetworkAnalyzerInternal
import com.alexlytvynenko.appanalyzer.internal.entity.RequestEntity
import com.alexlytvynenko.appanalyzer.internal.screenHeight
import com.alexlytvynenko.appanalyzer.internal.ui.LayoutController
import kotlinx.android.synthetic.main.fragment_list.*
import com.alexlytvynenko.appanalyzer.internal.consume

/**
 * Created by alex_litvinenko on 22.10.17.
 */
class RequestDetailsFragment : Fragment() {

    private var request: RequestEntity? = null

    companion object {
        val ARG_REQUEST = "arg_request"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_list, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            request = arguments.get(ARG_REQUEST) as RequestEntity
        }
        initList()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.root_menu, menu)
        menu?.findItem(R.id.search)?.isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.share -> {
                consume { share() }
            }
            R.id.clear -> {
                consume { request?.let { deleteRequest(it) } }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun share() {
        request?.let {
            val file = NetworkAnalyzerInternal.saveToFile(activity, it.toShareData())
            NetworkAnalyzerInternal.shareFile(activity, file, "Request")
        }
    }

    private fun initList() {
        list.setHasFixedSize(true)
        list.layoutManager = LinearLayoutManager(activity)
        request?.let {
            list.adapter = RequestDetailsAdapter(it)
            (list.adapter as RequestDetailsAdapter).onClickListener = { checkScroll() }
            checkScroll()
        }
    }

    private fun checkScroll() {
        list.postDelayed({
            val canScroll = list.canScrollVertically(ItemTouchHelper.DOWN) || list.canScrollVertically(ItemTouchHelper.UP)
            val actionBarHeight = (activity as AppCompatActivity).supportActionBar?.height ?: 0
            val overHeight = if (view.height < screenHeight() - actionBarHeight)
                view.height - list.height < actionBarHeight
            else view.height - actionBarHeight - list.height < actionBarHeight
            if (canScroll || overHeight) (activity as LayoutController).enableScroll()
            else (activity as LayoutController).disableScroll()
        }, 100)
    }

    private fun deleteRequest(requestEntity: RequestEntity) {
        NetworkAnalyzerInternal.deleteRequestFromDatabase(requestEntity)
        activity.onBackPressed()
    }
}