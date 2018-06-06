package dustit.clientapp.mvp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.Unbinder
import dustit.clientapp.R
import dustit.clientapp.customviews.WrapperLinearLayoutManager
import dustit.clientapp.mvp.model.entities.MemEntity
import dustit.clientapp.mvp.presenters.fragments.FeedFragmentPresenter
import dustit.clientapp.mvp.ui.adapters.FeedRecyclerViewAdapter
import dustit.clientapp.mvp.ui.base.BaseFeedFragment
import dustit.clientapp.mvp.ui.interfaces.IFeedFragmentView
import dustit.clientapp.utils.AlertBuilder
import kotlinx.android.synthetic.main.fragment_feed.view.*


class FeedFragment : BaseFeedFragment(), IFeedFragmentView, FeedRecyclerViewAdapter.IFeedInteractionListener {
    private var appBarHeight: Int = 0
    internal var rvFeed: RecyclerView? = null
    internal var srlRefresh: SwipeRefreshLayout? = null
    private var unbinder: Unbinder? = null
    private var presenter: FeedFragmentPresenter? = null
    private var scrollListener: RecyclerView.OnScrollListener? = null
    private var linearLayoutManager: WrapperLinearLayoutManager? = null

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        if (args != null) {
            appBarHeight = args.getInt(HEIGHT_APPBAR)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        bindWithBase(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_feed, container, false)
        rvFeed = v.rvFeed
        srlRefresh = v.srlFeedRefresh
        unbinder = ButterKnife.bind(this, v)
        linearLayoutManager = WrapperLinearLayoutManager(context)
        rvFeed!!.layoutManager = linearLayoutManager
        adapter = context?.let { FeedRecyclerViewAdapter(context, this, appBarHeight) }
        rvFeed!!.adapter = adapter
        presenter = FeedFragmentPresenter()
        presenter!!.bind(this)
        srlRefresh!!.setProgressViewOffset(false, appBarHeight, appBarHeight + 100)
        presenter!!.loadBase()
        srlRefresh!!.setOnRefreshListener {
            srlRefresh!!.isRefreshing = true
            presenter!!.loadBase()
        }
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    notifyFeedScrollIdle(true)
                    if (rvFeed!!.getChildAt(0) != null)
                        if (rvFeed!!.getChildAt(0).top == appBarHeight && linearLayoutManager!!.findFirstVisibleItemPosition() == 0) {
                            if (!srlRefresh!!.isRefreshing)
                                notifyFeedOnTop()
                        }
                } else {
                    notifyFeedScrollIdle(false)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                notifyFeedScrollChanged(dy)
            }
        }
        rvFeed!!.addOnScrollListener(scrollListener)
        subscribeToFeedbackChanges()
        return v
    }

    override fun onDestroyView() {
        unsubscribeFromFeedbackChanges()
        rvFeed!!.removeOnScrollListener(scrollListener)
        unbinder!!.unbind()
        presenter!!.unbind()
        super.onDestroyView()
    }

    override fun onBaseUpdated(list: List<MemEntity>) {
        srlRefresh!!.isRefreshing = false
        adapter!!.updateWhole(list)
    }

    override fun onPartialUpdate(list: List<MemEntity>) {
        if (list.isEmpty()) {
            adapter!!.onMemesEnded()
            return
        }
        adapter!!.updateAtEnding(list)
    }

    override fun onErrorInLoading() {
        srlRefresh!!.isRefreshing = false
        adapter!!.onFailedToLoad()
    }

    override fun reloadFeedBase() {
        presenter!!.loadBase()
    }
    override fun showErrorToast() {
        Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT).show()
    }

    override fun onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(context)
    }

    override fun loadMore(offset: Int) {
        presenter!!.loadWithOffset(offset)
    }

    companion object {
        const val HEIGHT_APPBAR = "HEIGHT"
        fun newInstance(appBarHeight: Int): FeedFragment {
            val args = Bundle()
            args.putInt(HEIGHT_APPBAR, appBarHeight)
            val fragment = FeedFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
