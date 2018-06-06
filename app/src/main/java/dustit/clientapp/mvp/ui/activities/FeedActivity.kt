package dustit.clientapp.mvp.ui.activities

import android.animation.AnimatorSet
import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import dustit.clientapp.App
import dustit.clientapp.R
import dustit.clientapp.mvp.datamanager.FeedbackManager
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager
import dustit.clientapp.mvp.model.entities.Category
import dustit.clientapp.mvp.model.entities.MemEntity
import dustit.clientapp.mvp.presenters.activities.FeedActivityPresenter
import dustit.clientapp.mvp.ui.adapters.FeedViewPagerAdapter
import dustit.clientapp.mvp.ui.base.BaseFeedFragment
import dustit.clientapp.mvp.ui.fragments.CategoriesFragment
import dustit.clientapp.mvp.ui.fragments.MemViewFragment
import dustit.clientapp.mvp.ui.interfaces.IFeedActivityView
import dustit.clientapp.utils.AlertBuilder
import dustit.clientapp.utils.IConstants
import dustit.clientapp.utils.managers.ThemeManager
import kotlinx.android.synthetic.main.activity_feed.*
import java.util.*
import javax.inject.Inject

class FeedActivity : AppCompatActivity(), CategoriesFragment.ICategoriesFragmentInteractionListener, IFeedActivityView, MemViewFragment.IMemViewRatingInteractionListener, BaseFeedFragment.IBaseFragmentInteraction {

    private var isFeedScrollIdle = true
    internal lateinit var vpFeed: ViewPager
    private lateinit var clLayout: RelativeLayout
    internal lateinit var tvAppName: TextView
    private lateinit var appBar: ViewGroup
    private lateinit var fabColapsed: FloatingActionButton
    internal lateinit var container: ViewGroup
    internal lateinit var toolbar: android.support.v7.widget.Toolbar
    private lateinit var tabs: android.support.design.widget.TabLayout

    private var adapter: FeedViewPagerAdapter? = null
    private val presenter: FeedActivityPresenter = FeedActivityPresenter()

    private var isFirstLaunch = true
    private var isToolbarCollapsed = false
    private var animIsPlaying = false
    private var canToolbarCollapse = false

    private var fabScrollYNormalPos: Float = 0f
    private val screenBounds = Rect()
    private var cooldownPassed = true
    private val handler = Handler()
    private val ids = intArrayOf(R.drawable.ic_feed_pressed, R.drawable.ic_hot_pressed, R.drawable.ic_categories_pressed)
    private var spinnerInteractionListener: ICategoriesSpinnerInteractionListener? = null

    @Inject
    lateinit var themeManager: ThemeManager
    @Inject
    lateinit var userSettingsDataManager: UserSettingsDataManager
    @Inject
    lateinit var feedbackManager: FeedbackManager

    interface ICategoriesSpinnerInteractionListener {
        fun onCategoriesArrived()
        fun onCategorySelected(category: Category)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.get().appComponent.inject(this)
        setContentView(R.layout.activity_feed)
        presenter.bind(this)
        bindViews()
        sdvUserIcon!!.setLegacyVisibilityHandlingEnabled(true)
        presenter.getMyUsername()
        clLayout.getHitRect(screenBounds)
        fabScrollYNormalPos = fabColapsed.y + 25
        vpFeed.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tlFeedTabs))
        tlFeedTabs!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                animateFabIcon(tab.position)
                when (tab.position) {
                    0 -> vpFeed.setCurrentItem(0, true)
                    1 -> vpFeed.setCurrentItem(1, true)
                    2 -> {
                        val fromCenterToLeft = AnimationUtils.loadAnimation(this@FeedActivity, R.anim.from_center_to_left)
                        val fromRightToCenter = AnimationUtils.loadAnimation(this@FeedActivity, R.anim.from_right_to_center)
                        fromCenterToLeft.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation?) {
                            }

                            override fun onAnimationEnd(animation: Animation) {
                                tvAppName.visibility = View.GONE
                                fromRightToCenter.setAnimationListener(object : Animation.AnimationListener {
                                    override fun onAnimationStart(animation: Animation) {}

                                    override fun onAnimationEnd(animation: Animation) {
                                        spCategoriesChooser.visibility = View.VISIBLE
                                    }

                                    override fun onAnimationRepeat(animation: Animation) {}
                                })
                                spCategoriesChooser.startAnimation(fromRightToCenter)
                            }

                            override fun onAnimationRepeat(animation: Animation) {}
                        })
                        tvAppName.startAnimation(fromCenterToLeft)
                        vpFeed.setCurrentItem(2, true)
                        return
                    }
                }
                if (tvAppName.visibility != View.VISIBLE) {
                    val fromCenterToRight = AnimationUtils.loadAnimation(this@FeedActivity,
                            R.anim.from_center_to_right)
                    val fromLeftToCenter = AnimationUtils.loadAnimation(this@FeedActivity,
                            R.anim.from_left_to_center)
                    fromCenterToRight.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {}

                        override fun onAnimationEnd(animation: Animation) {
                            spCategoriesChooser.visibility = View.GONE
                            fromLeftToCenter.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationStart(animation: Animation) {}
                                override fun onAnimationEnd(animation: Animation) {
                                    tvAppName.visibility = View.VISIBLE
                                }

                                override fun onAnimationRepeat(animation: Animation) {}
                            })
                            tvAppName.startAnimation(fromLeftToCenter)
                        }

                        override fun onAnimationRepeat(animation: Animation) {}
                    })
                    spCategoriesChooser.startAnimation(fromCenterToRight)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {
                setToolbarCollapsed(true)
            }
        })
        sdvUserIcon.setOnClickListener { this.revealAccount(it) }
        fabColapsed.setOnClickListener { setToolbarCollapsed(false) }
        animateFabIcon(0)
        val layoutTransition = LayoutTransition()
        layoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING)
        layoutTransition.disableTransitionType(LayoutTransition.APPEARING)
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        layoutTransition.setDuration(LayoutTransition.CHANGING, 100)
        clLayout.layoutTransition = layoutTransition
        presenter.getCategories()
    }

    private fun bindViews() {
        vpFeed = vpFeedPager
        clLayout = clMainLayout
        tvAppName = tvActivityFeedAppName
        appBar = appBarActivityFeed
        fabColapsed = fabToolbarCollapsed
        container = feedContainer
        toolbar = tbFeedActivity
        tabs = tlFeedTabs
    }

    override fun onStart() {
        canToolbarCollapse = userSettingsDataManager.useImmersiveMode()
        super.onStart()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && isFirstLaunch) {
            isFirstLaunch = false
            adapter = FeedViewPagerAdapter(supportFragmentManager, appBar.height)
            if (vpFeed.adapter == null)
                vpFeed.adapter = adapter
            vpFeed.offscreenPageLimit = 3
            SHOWN_TOOLBAR_Y = 0
            HIDDEN_TOOLBAR_Y = 0 - toolbar.height
        }
    }

    private fun animateFabIcon(tabPos: Int) {
        when (tabPos) {
            0 -> fabColapsed.setImageResource(ids[0])
            1 -> fabColapsed.setImageResource(ids[1])
            2 -> fabColapsed.setImageResource(ids[2])
        }
    }

    private fun revealAccount(view: View) {
        val intent = Intent(this, AccountActivity::class.java)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                view,
                getString(R.string.account_photo_transition))
        startActivity(intent, options.toBundle())
    }

    override fun onDestroy() {
        feedbackManager.destroy()
        presenter.unbind()
        adapter!!.destroy()
        super.onDestroy()
    }

    override fun onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(this)
    }

    override fun onError() {
        Toast.makeText(this, getText(R.string.error), Toast.LENGTH_SHORT).show()
    }

    override fun onUsernameArrived(s: String) {
        sdvUserIcon.setImageURI(Uri.parse(IConstants.BASE_URL + "/feed/userPhoto?targetUsername=" + s))
    }

    override fun onCategoriesArrived(categoryList: List<Category>) {
        val categoryNames = ArrayList<String>()
        for (category in categoryList) categoryNames.add(category.name)
        val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategoriesChooser!!.adapter = adapter
        spCategoriesChooser!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (spinnerInteractionListener != null) {
                    spinnerInteractionListener!!.onCategorySelected(categoryList[position])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        if (spinnerInteractionListener != null)
            spinnerInteractionListener!!.onCategoriesArrived()
    }

    override fun onCategoriesFailedToLoad() {
        adapter!!.setCategoriesLoaded(false)
        onError()
    }

    override fun notifyOnScrollChanged(distance: Int) {
        if (canToolbarCollapse) {
            if (!animIsPlaying) {
                if (!isToolbarCollapsed) {
                    if (distance > MIN_DISTANCE_THRESHOLD) {
                        setCooldown()
                        setToolbarCollapsed(true)
                    }
                }
                if (isToolbarCollapsed) {
                    val setY = if (distance > 0) fabColapsed.y - FAB_STEP else fabColapsed.y + FAB_STEP
                    if (setY <= FAB_HIDDEN_Y) {
                        if (fabColapsed.y == FAB_HIDDEN_Y.toFloat()) return
                        fabColapsed.y = FAB_HIDDEN_Y.toFloat()
                        return
                    }
                    if (setY <= fabScrollYNormalPos) {
                        fabColapsed.y = setY
                    } else {
                        if (fabColapsed.y == fabScrollYNormalPos) return
                        fabColapsed.y = fabScrollYNormalPos
                    }
                }
            }
        } else {
            val setY = if (distance > 0) appBar.y - FAB_STEP else appBar.y + FAB_STEP
            if (setY <= HIDDEN_TOOLBAR_Y) {
                if (appBar.y != HIDDEN_TOOLBAR_Y.toFloat()) {
                    appBar.y = HIDDEN_TOOLBAR_Y.toFloat()
                }
            } else if (setY <= SHOWN_TOOLBAR_Y) {
                appBar.y = setY
            } else {
                if (appBar.y == SHOWN_TOOLBAR_Y.toFloat()) return
                appBar.y = SHOWN_TOOLBAR_Y.toFloat()
            }
        }
    }

    override fun launchMemView(holder: View, memEntity: MemEntity) {
        val v = holder.findViewById<View>(R.id.sdvItemFeed)
        ViewCompat.setTransitionName(v, getString(R.string.mem_feed_transition_name))
        val fragment = MemViewFragment.newInstance(memEntity)
        val transition = TransitionInflater
                .from(this).inflateTransition(R.transition.mem_view_transition)
        fragment.sharedElementEnterTransition = transition
        fragment.sharedElementReturnTransition = transition
        supportFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .addSharedElement(v, getString(R.string.mem_feed_transition_name))
                .replace(R.id.feedContainer, fragment)
                .addToBackStack(null)
                .commit()
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0)
                clLayout.visibility = View.VISIBLE
        }
    }

    override fun closeMemView() {
        clLayout.visibility = View.VISIBLE
        supportFragmentManager.popBackStack()
    }

    override fun notifyFeedScrollIdle(b: Boolean) {
        isFeedScrollIdle = b
        if (canToolbarCollapse) {
            if (isFeedScrollIdle) {
                if (fabColapsed.getLocalVisibleRect(screenBounds) && fabColapsed.y != fabScrollYNormalPos) {
                    val animator = ValueAnimator.ofFloat(fabColapsed.y, fabScrollYNormalPos)
                    animator.addUpdateListener { animation -> fabColapsed.y = animation.animatedValue as Float }
                    animator.start()
                }
            }
        } else {
            if (isFeedScrollIdle) {
                if (appBar.y != SHOWN_TOOLBAR_Y.toFloat() && appBar.y != HIDDEN_TOOLBAR_Y.toFloat()) {
                    val animator = ValueAnimator.ofFloat(appBar.y, SHOWN_TOOLBAR_Y.toFloat())
                    animator.addUpdateListener { animation -> appBar.y = animation.animatedValue as Float }
                    animator.start()
                }
            }
        }
    }

    override fun notifyFeedOnTop() {
        if (canToolbarCollapse) {
            if (isToolbarCollapsed)
                setToolbarCollapsed(false)
        } else {
            if (appBar.y != SHOWN_TOOLBAR_Y.toFloat()) {
                val animator = ValueAnimator.ofFloat(appBar.y, SHOWN_TOOLBAR_Y.toFloat())
                animator.addUpdateListener { animation -> appBar.y = animation.animatedValue as Float }
                animator.start()
            }
        }
    }

    private fun setToolbarCollapsed(collapseToolbar: Boolean) {
        if (canToolbarCollapse) {
            if (collapseToolbar) {
                unrevealToolbar()
            } else {
                revealToolbar()
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun revealToolbar() {
        val fabX = fabColapsed.x + fabColapsed.width / 2
        val fabY = fabColapsed.y + fabColapsed.height / 2
        val tabsX = tabs.x + tabs.width / 2
        val tabsY = tabs.y + tabs.height / 2
        val toX = tabsX - fabX
        val toY = tabsY - fabY
        val animatorSet = AnimatorSet()
        val xAnim = ObjectAnimator.ofFloat(fabColapsed as View, "translationX", toX)
        val yAnim = ObjectAnimator.ofFloat(fabColapsed as View, "translationY", toY)
        animatorSet.play(xAnim).with(yAnim)
        animatorSet.duration = 300
        animatorSet.start()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun unrevealToolbar() {
        val fabX = fabColapsed.x + fabColapsed.width / 2
        val fabY = fabColapsed.y + fabColapsed.height / 2
        val tabsX = tabs.x + tabs.width / 2
        val tabsY = tabs.y + tabs.height / 2
        val toX = tabsX - fabX
        val toY = tabsY - fabY
        val animatorSet = AnimatorSet()
        val xAnim = ObjectAnimator.ofFloat(fabColapsed as View, "translationX", toX)
        val yAnim = ObjectAnimator.ofFloat(fabColapsed as View, "translationY", toY)
        animatorSet.play(xAnim).with(yAnim)
        animatorSet.duration = 300
        animatorSet.start()
    }

    override fun onResume() {
        super.onResume()
        fabColapsed.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
    }

    override fun onAttachToActivity(listener: ICategoriesSpinnerInteractionListener) {
        spinnerInteractionListener = listener
    }

    override fun onDetachFromActivity() {
        spinnerInteractionListener = null
    }

    private fun setCooldown() {
        cooldownPassed = false
        handler.postDelayed({ cooldownPassed = true }, 300)
    }

    companion object {
        private const val MIN_DISTANCE_THRESHOLD = 15
        private const val FAB_STEP = 10f
        private const val FAB_HIDDEN_Y = -200
        private var HIDDEN_TOOLBAR_Y = 0
        private var SHOWN_TOOLBAR_Y = 0
    }
}
