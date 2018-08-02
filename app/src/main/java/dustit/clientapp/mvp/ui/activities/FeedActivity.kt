package dustit.clientapp.mvp.ui.activities

import android.animation.*
import android.annotation.TargetApi
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.wooplr.spotlight.SpotlightConfig
import com.wooplr.spotlight.SpotlightView
import com.wooplr.spotlight.utils.SpotlightSequence
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
import dustit.clientapp.mvp.ui.interfaces.IView
import dustit.clientapp.utils.AlertBuilder
import dustit.clientapp.utils.IConstants
import dustit.clientapp.utils.L
import dustit.clientapp.utils.TimeTracking
import dustit.clientapp.utils.bus.FavouritesBus
import dustit.clientapp.utils.managers.ThemeManager
import kotlinx.android.synthetic.main.activity_feed.*
import java.util.*
import javax.inject.Inject

class FeedActivity : AppCompatActivity(), CategoriesFragment.ICategoriesFragmentInteractionListener, IFeedActivityView, MemViewFragment.IMemViewRatingInteractionListener, BaseFeedFragment.IBaseFragmentInteraction {
    override fun isRegistered(): Boolean {
        return userSettingsDataManager.isRegistered
    }

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

    private var FAB_HIDDEN_Y = 0

    private var fabX = 0
    private var fabY = 0

    private var wasToolbarRevealed = false

    private var fabScrollYNormalPos: Float = 0f
    private val screenBounds = Rect()
    private val ids = intArrayOf(R.drawable.ic_feed_pressed, R.drawable.ic_hot_pressed, R.drawable.ic_explore_white_pressed)
    private var spinnerInteractionListener: ICategoriesSpinnerInteractionListener? = null

    private val deque = ArrayDeque<Int>()
    private var isBackPressed = false

    @Inject
    lateinit var themeManager: ThemeManager
    @Inject
    lateinit var userSettingsDataManager: UserSettingsDataManager
    @Inject
    lateinit var feedbackManager: FeedbackManager

    interface ICategoriesSpinnerInteractionListener {
        fun onCategoriesArrived()
        fun onCategoriesFailed()
        fun onCategorySelected(category: Category)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.get().appComponent.inject(this)
        setContentView(R.layout.activity_feed)
        presenter.bind(this)
        feedbackManager.bind(this)
        bindViews()
        deque.add(0)
        sdvUserIcon!!.setLegacyVisibilityHandlingEnabled(true)
        val point = Point()
        windowManager.defaultDisplay.getSize(point)
        FAB_HIDDEN_Y = point.y + fabColapsed.height + 15
        if (userSettingsDataManager.isRegistered) {
            presenter.getMyUsername()
        }
        clLayout.getHitRect(screenBounds)
        vpFeed.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tlFeedTabs))
        tlFeedTabs!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (!isBackPressed) {
                    if (deque.size + 1 > 3) {
                        deque.removeLast()
                    }
                    deque.add(tab.position)
                } else {
                    isBackPressed = !isBackPressed
                }
                L.print("Deque - $deque")
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
        fabColapsed.setOnClickListener {
            setToolbarCollapsed(!isToolbarCollapsed)
        }
        animateFabIcon(0)
        if (!presenter.isFeedFirstTime) {
            wasToolbarRevealed = true
        }
        if (!userSettingsDataManager.isRegistered) {
            val uri = Uri.parse("android.resource://" + this.packageName + "/drawable/noimage")
            sdvUserIcon.setImageURI(uri)
        }
        val layoutTransition = LayoutTransition()
        layoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING)
        layoutTransition.disableTransitionType(LayoutTransition.APPEARING)
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        layoutTransition.setDuration(LayoutTransition.CHANGING, 100)
        clLayout.layoutTransition = layoutTransition
        presenter.getCategories()
        SpotlightView.Builder(this)
                .introAnimationDuration(400)
                .enableRevealAnimation(true)
                .performClick(true)
                .fadeinTextDuration(400)
                .headingTvColor(Color.parseColor("#f98098"))
                .headingTvSize(32)
                .headingTvText(getString(R.string.hello_fab))
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText(getString(R.string.hello_fab_description))
                .maskColor(Color.parseColor("#dc000000"))
                .target(fabColapsed)
                .lineAnimDuration(400)
                .lineAndArcColor(Color.parseColor("#ffb06a"))
                .dismissOnTouch(false)
                .dismissOnBackPress(false)
                .enableDismissAfterShown(false)
                .usageId(IConstants.ISpotlight.FAB_FEED)
                .show()
    }

    private fun showIntro() {
        val config = SpotlightConfig()
        config.introAnimationDuration = 400
        config.isRevealAnimationEnabled = true
        config.isPerformClick = false
        config.fadingTextDuration = 400
        config.headingTvColor = Color.parseColor("#f98098")
        config.headingTvSize = 32
        config.subHeadingTvColor = Color.parseColor("#ffffff");
        config.subHeadingTvSize = 16
        config.maskColor = Color.parseColor("#dc000000")
        config.lineAnimationDuration = 400
        config.lineAndArcColor = Color.parseColor("#ffb06a")
        val sequence = SpotlightSequence.getInstance(this, config)
                .addSpotlight((tabs.getChildAt(0) as ViewGroup).getChildAt(0), R.string.feed_title, R.string.description_feed_icon, IConstants.ISpotlight.FEED_ICON)
                .addSpotlight((tabs.getChildAt(0) as ViewGroup).getChildAt(1), R.string.hot_title, R.string.hot_description, IConstants.ISpotlight.HOT_ICON)
                .addSpotlight((tabs.getChildAt(0) as ViewGroup).getChildAt(2), R.string.categories_title, R.string.categories_description, IConstants.ISpotlight.CATEGORIES_ICON)
        if (presenter.isRegistered) {
            sequence.addSpotlight(sdvUserIcon, R.string.user_icon_title, R.string.user_icon_description, IConstants.ISpotlight.ACCOUNT_ICON)
        }
        sequence.startSequence()
        wasToolbarRevealed = true
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
        canToolbarCollapse = true
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
            fabScrollYNormalPos = fabColapsed.y
            fabX = fabColapsed.x.toInt()
            fabY = fabColapsed.y.toInt()
        }
//        if (presenter.isFeedFirstTime) {
    }

    private fun animateFabIcon(tabPos: Int) {
        when (tabPos) {
            0 -> fabColapsed.setImageResource(R.drawable.ic_feed)
            1 -> fabColapsed.setImageResource(R.drawable.ic_hot)
            2 -> fabColapsed.setImageResource(R.drawable.ic_categories)
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
        val b = Bundle()
        b.putLong("TIME_TRACK", System.currentTimeMillis() - TimeTracking.getInstance().startDate)
        FirebaseAnalytics.getInstance(this).logEvent("TIME_TRACK", b)
        feedbackManager.destroy()
        presenter.unbind()
        adapter!!.destroy()
        FavouritesBus.destroy()
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
        if (!categoryList.isEmpty()) {
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
    }

    override fun onCategoriesFailedToLoad() {
        adapter?.setCategoriesLoaded(false)
        if (spinnerInteractionListener != null) {
            spinnerInteractionListener!!.onCategoriesFailed()
        }
        onError()
    }

    override fun reloadCategories() {
        presenter.getCategories()
    }

    override fun notifyOnScrollChanged(distance: Int) {
        if (canToolbarCollapse) {
            if (!animIsPlaying) {
                if (!isToolbarCollapsed) {
                    isToolbarCollapsed = true
                    setToolbarCollapsed(true)
                    return
                }
                val isGoingUp = distance < 0
                if (isGoingUp) {
                    if (fabColapsed.y == fabScrollYNormalPos) return
                    val moveY = fabColapsed.y - FAB_STEP
                    if (moveY < fabScrollYNormalPos) {
                        fabColapsed.y = fabScrollYNormalPos
                        return
                    }
                    fabColapsed.y = moveY
                } else {
                    val moveY = fabColapsed.y + FAB_STEP
                    if (moveY.toInt() == FAB_HIDDEN_Y) return
                    if (moveY > FAB_HIDDEN_Y) {
                        fabColapsed.y = FAB_HIDDEN_Y.toFloat()
                        return
                    }
                    fabColapsed.y = moveY
                }
            }
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

    override fun launchMemView(holder: View, memEntity: MemEntity, startComments: Boolean) {
        val fragment = MemViewFragment.newInstance(memEntity, startComments)
        val transition = TransitionInflater
                .from(this).inflateTransition(R.transition.mem_view_transition)
        fragment.sharedElementEnterTransition = transition
        fragment.sharedElementReturnTransition = transition
        supportFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.feedContainer, fragment)
                .addToBackStack(null)
                .commit()
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0)
                clLayout.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if (deque.size > 1) {
            isBackPressed = true
            deque.pollLast()
            vpFeed.setCurrentItem(deque.pollLast(), true)
        } else if (deque.size == 1) {
            isBackPressed = true
            vpFeed.setCurrentItem(deque.pollLast(), true)
        } else {
            super.onBackPressed()
        }
    }

    override fun gotoFragment(id: Byte) {
        vpFeed.setCurrentItem(id.toInt(), true)
    }

    override fun closeMemView() {
        clLayout.visibility = View.VISIBLE
        supportFragmentManager.popBackStack()
    }

    override fun notifyFeedScrollIdle(b: Boolean) {
        isFeedScrollIdle = b
        if (canToolbarCollapse) {
            if (isFeedScrollIdle) {
                val dist = FAB_HIDDEN_Y - fabScrollYNormalPos
                val pos = FAB_HIDDEN_Y - fabColapsed.y
                if (fabColapsed.y != FAB_HIDDEN_Y.toFloat() || fabColapsed.y != fabScrollYNormalPos) {
                    if (pos < dist / 2) {
                        val anim = ValueAnimator.ofFloat(fabColapsed.y, FAB_HIDDEN_Y.toFloat())
                        anim.addUpdateListener { animation -> fabColapsed.y = animation.animatedValue as Float }
                        anim.start()
                    } else if (pos > dist / 2) {
                        val anim = ValueAnimator.ofFloat(fabColapsed.y, fabScrollYNormalPos)
                        anim.addUpdateListener { animation -> fabColapsed.y = animation.animatedValue as Float }
                        anim.start()
                    }
                }
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
            setToolbarCollapsed(false)
            if (appBar.y != SHOWN_TOOLBAR_Y.toFloat()) {
                val animator = ValueAnimator.ofFloat(appBar.y, SHOWN_TOOLBAR_Y.toFloat())
                animator.addUpdateListener { animation -> appBar.y = animation.animatedValue as Float }
                animator.start()
            }
        }
    }

    private fun setToolbarCollapsed(collapseToolbar: Boolean) {
        isToolbarCollapsed = collapseToolbar
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
        animIsPlaying = true
        val tabX = tabs.x + tabs.width / 2
        val tabY = tabs.y + tabs.height / 2
        val toX = tabX - fabColapsed.width / 2
        val toY = tabY - fabColapsed.height / 2
        val translateAnimX = ObjectAnimator.ofFloat(fabColapsed, "x", toX)
        val translateAnimY = ObjectAnimator.ofFloat(fabColapsed, "y", toY)
        val color = ContextCompat.getColor(this, R.color.colorPrimary)
        val changeColor = ObjectAnimator.ofObject(ArgbEvaluator(), color, ContextCompat.getColor(this, R.color.fabSecond))
        changeColor.addUpdateListener { animation -> fabColapsed.backgroundTintList = ColorStateList.valueOf(animation?.animatedValue as Int) }
        val translate = AnimatorSet()
        val fin = AnimatorSet()
        val endRadius = (Math.hypot(tabs.width / 2.toDouble(), tabs.height / 2.toDouble())).toFloat()
        val reveal: Animator = ViewAnimationUtils.createCircularReveal(tabs, tabs.width / 2, tabs.height / 2, fabColapsed.width / 2f, endRadius)
        reveal.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                if (!wasToolbarRevealed) showIntro()
                animIsPlaying = false
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {
                tabs.visibility = View.VISIBLE
                fabColapsed.visibility = View.INVISIBLE
            }
        })
        reveal.duration = 150
        translate.duration = 150
        translate.playTogether(translateAnimX, translateAnimY, changeColor)
        fin.play(translate)
        fin.play(reveal).after(100)
        fin.duration = 300
        fin.start()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun unrevealToolbar() {
        animIsPlaying = true
        val translateAnimX = ObjectAnimator.ofFloat(fabColapsed, "x", fabX.toFloat())
        val translateAnimY = ObjectAnimator.ofFloat(fabColapsed, "y", fabY.toFloat())
        val translate = AnimatorSet()
        val fin = AnimatorSet()
        val endRadius = (Math.hypot(tabs.width / 2.toDouble(), tabs.height / 2.toDouble())).toFloat()
        val color = ContextCompat.getColor(this, R.color.colorPrimary)
        val changeColor = ObjectAnimator.ofObject(ArgbEvaluator(), ContextCompat.getColor(this, R.color.fabSecond), color)
        changeColor.addUpdateListener { animation -> fabColapsed.backgroundTintList = ColorStateList.valueOf(animation?.animatedValue as Int) }
        val unreveal = ViewAnimationUtils.createCircularReveal(tabs, tabs.width / 2, tabs.height / 2, endRadius, (fabColapsed.width / 2).toFloat())
        unreveal.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                tabs.visibility = View.INVISIBLE
                fabColapsed.visibility = View.VISIBLE
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}
        })
        translate.playTogether(translateAnimX, translateAnimY, changeColor)
        unreveal.duration = 150
        translate.duration = 150
        fin.playSequentially(unreveal, translate)
        fin.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                animIsPlaying = false
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}
        })
        fin.start()
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

    companion object {
        private const val FAB_STEP = 10f
        private var HIDDEN_TOOLBAR_Y = 0
        private var SHOWN_TOOLBAR_Y = 0
    }
}
