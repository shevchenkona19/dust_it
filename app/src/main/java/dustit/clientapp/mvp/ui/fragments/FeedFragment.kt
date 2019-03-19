package dustit.clientapp.mvp.ui.fragments

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.Unbinder
import dustit.clientapp.R
import dustit.clientapp.customviews.WrapperLinearLayoutManager
import dustit.clientapp.mvp.model.entities.MemEntity
import dustit.clientapp.mvp.model.entities.NewAchievementEntity
import dustit.clientapp.mvp.presenters.fragments.FeedFragmentPresenter
import dustit.clientapp.mvp.ui.activities.AccountActivity
import dustit.clientapp.mvp.ui.activities.PersonalSettingsActivity
import dustit.clientapp.mvp.ui.adapters.FeedRecyclerViewAdapter
import dustit.clientapp.mvp.ui.base.BaseFeedFragment
import dustit.clientapp.mvp.ui.dialog.AchievementUnlockedDialog
import dustit.clientapp.mvp.ui.interfaces.IFeedFragmentView
import dustit.clientapp.utils.AlertBuilder
import dustit.clientapp.utils.IConstants
import kotlinx.android.synthetic.main.fragment_feed.view.*

class FeedFragment : BaseFeedFragment(), IFeedFragmentView, FeedRecyclerViewAdapter.IFeedInteractionListener {
    private var appBarHeight: Int = 0
    private var rvFeed: RecyclerView? = null
    private var srlRefresh: SwipeRefreshLayout? = null
    private var unbinder: Unbinder? = null
    private var presenter: FeedFragmentPresenter? = null
    private var scrollListener: RecyclerView.OnScrollListener? = null
    private var linearLayoutManager: WrapperLinearLayoutManager? = null
    private var rlEmptyCategories: ViewGroup? = null
    private var changingCategories = false
    private var myId: String? = ""

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        if (args != null) {
            appBarHeight = args.getInt(HEIGHT_APPBAR)
            myId = args.getString(IConstants.IBundle.MY_ID)
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
        bindFeedback(this)
        linearLayoutManager = WrapperLinearLayoutManager(context)
        rvFeed!!.layoutManager = linearLayoutManager
        adapter = context?.let { FeedRecyclerViewAdapter(context, this, appBarHeight, rvFeed) }
        adapter.setHasStableIds(true)
        rvFeed!!.adapter = adapter
        rlEmptyCategories = v.hotEmpty
        setFeedPool(rvFeed!!.recycledViewPool)
        presenter = FeedFragmentPresenter()
        presenter!!.bind(this)
        v.btnEmptyHot.setOnClickListener {
            changingCategories = true
            val intent = Intent(context, PersonalSettingsActivity::class.java)
            startActivity(intent)
        }
        srlRefresh!!.setProgressViewOffset(false, appBarHeight - 100, appBarHeight + 100)
        srlRefresh!!.setOnRefreshListener {
            srlRefresh!!.isRefreshing = true
            presenter!!.loadBase()
        }
        (rvFeed!!.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        subscribeToFeedbackChanges()
        presenter!!.loadBase()
        return v
    }

    fun scrollToTop() {
        rvFeed?.scrollToPosition(0)
    }

    override fun isRegistered(): Boolean {
        return isUserRegistered
    }

    override fun onDestroyView() {
        unsubscribeFromFeedbackChanges()
        rvFeed!!.removeOnScrollListener(scrollListener)
        unbinder!!.unbind()
        presenter!!.unbind()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        if (changingCategories) {
            changingCategories = false
            presenter!!.loadBase()
            srlRefresh!!.visibility = View.VISIBLE
            rlEmptyCategories!!.visibility = View.INVISIBLE
        }
    }

    override fun onBaseUpdated(list: List<MemEntity>) {
        srlRefresh!!.isRefreshing = false
        adapter!!.updateWhole(list)
        rvFeed!!.scheduleLayoutAnimation()
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

    override fun onNoCategories() {
        srlRefresh!!.visibility = View.INVISIBLE
        rlEmptyCategories!!.visibility = View.VISIBLE
    }

    override fun onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(context)
    }

    override fun loadMore(offset: Int) {
        presenter!!.loadWithOffset(offset)
    }

    override fun gotoHot() {
        gotoFragment(1)
    }

    override fun onAchievementUpdate(achievementEntity: NewAchievementEntity) {
        if (context != null) {
            if (presenter!!.isRegistered)
            AchievementUnlockedDialog(context, achievementEntity.isFinalLevel).bind(achievementEntity).show()
        }
    }

    override fun gotoAccount(mem: MemEntity?) {
        if (mem != null) {
            val intent = Intent(context, AccountActivity::class.java)
            intent.putExtra(IConstants.IBundle.IS_ME, myId.equals(mem.userId))
            intent.putExtra(IConstants.IBundle.USER_ID, mem.userId)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
        }
    }

    companion object {
        const val HEIGHT_APPBAR = "HEIGHT"
        fun newInstance(appBarHeight: Int, myId: String): FeedFragment {
            val args = Bundle()
            args.putInt(HEIGHT_APPBAR, appBarHeight)
            args.putString(IConstants.IBundle.MY_ID, myId)
            val fragment = FeedFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
