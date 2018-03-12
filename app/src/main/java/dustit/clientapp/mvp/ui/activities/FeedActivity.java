package dustit.clientapp.mvp.ui.activities;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.FavoriteEntity;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.presenters.activities.FeedActivityPresenter;
import dustit.clientapp.mvp.ui.adapters.FeedViewPagerAdapter;
import dustit.clientapp.mvp.ui.base.BaseFeedFragment;
import dustit.clientapp.mvp.ui.fragments.CategoriesFragment;
import dustit.clientapp.mvp.ui.fragments.FeedFragment;
import dustit.clientapp.mvp.ui.fragments.HotFragment;
import dustit.clientapp.mvp.ui.fragments.MemViewActivity;
import dustit.clientapp.mvp.ui.fragments.MemViewFragment;
import dustit.clientapp.mvp.ui.interfaces.IFeedActivityView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.L;
import dustit.clientapp.utils.managers.ThemeManager;

public class FeedActivity extends AppCompatActivity implements FeedFragment.IFeedFragmentInteractionListener,
        HotFragment.IHotFragmentInteractionListener,
        CategoriesFragment.ICategoriesFragmentInteractionListener,
        IFeedActivityView,
        MemViewFragment.IMemViewRatingInteractionListener,
        BaseFeedFragment.IBaseFragmentInteraction {

    private static final String VIEW_FRAGMENT = "view";
    public static final String MEM_ENTITY = "kek";
    private static final int MIN_DISTANCE_THRESHOLD = 15;
    private static final int MIN_FAB_DISTANCE_THRESHOLD = 30;
    private static final float FAB_STEP = 10;
    private static final int FAB_HIDDEN_Y = -200;
    private static int HIDDEN_TOOLBAR_Y;
    private static int SHOWN_TOOLBAR_Y;

    private boolean isFeedScrollIdle = true;

    @BindView(R.id.tlFeedTabs)
    TabLayout tlFeedTabs;
    @BindView(R.id.sdvUserIcon)
    SimpleDraweeView sdvUserIcon;
    @BindView(R.id.vpFeedPager)
    ViewPager vpFeed;
    @BindView(R.id.clMainLayout)
    RelativeLayout clLayout;
    @BindView(R.id.tvActivityFeedAppName)
    TextView tvAppName;
    @BindView(R.id.appBarActivityFeed)
    AppBarLayout appBar;
    @BindView(R.id.fabToolbarCollapsed)
    FloatingActionButton fabColapsed;
    @BindView(R.id.feedContainer)
    ViewGroup container;
    @BindView(R.id.tbFeedActivity)
    android.support.v7.widget.Toolbar toolbar;

    private FeedViewPagerAdapter adapter;
    private FeedActivityPresenter presenter;

    private boolean isFeed = false;
    private boolean isHot = false;
    private boolean isCategories = false;

    private boolean isToolbarCollapsed = false;
    private boolean animIsPlaying = false;
    private boolean canToolbarCollapse = false;

    private float fabScrollYNormalPos;

    private final Rect screenBounds = new Rect();

    private boolean[] cooldownPassed = {true};

    private final Handler handler = new Handler();

    private final List<FavoriteEntity> favoriteEntityList = new ArrayList<>();

    private String themeSubscribeId;
    private final int[] ids = {R.drawable.ic_feed_pressed, R.drawable.ic_hot_pressed, R.drawable.ic_categories_pressed};

    @Inject
    ThemeManager themeManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        presenter = new FeedActivityPresenter();
        presenter.bind(this);
        ButterKnife.bind(this);
        presenter.getMyFavorites();
        App.get().getAppComponent().inject(this);
        vpFeed.setOffscreenPageLimit(3);
        sdvUserIcon.setLegacyVisibilityHandlingEnabled(true);
        presenter.getMyUsername();
        clLayout.getHitRect(screenBounds);
        fabScrollYNormalPos = fabColapsed.getY() + 25;
        vpFeed.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tlFeedTabs));
        tlFeedTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                animateFabIcon(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        vpFeed.setCurrentItem(0, true);
                        break;
                    case 1:
                        vpFeed.setCurrentItem(1, true);
                        break;
                    case 2:
                        vpFeed.setCurrentItem(2, true);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                setToolbarCollapsed(true);
            }
        });
        sdvUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealAccount(v);
            }
        });
        setColors(themeManager.getCurrentTheme());
        themeSubscribeId = themeManager.subscribeToThemeChanges(new ThemeManager.IThemable() {
            @Override
            public void notifyThemeChanged(ThemeManager.Theme t) {
                setColors(t);
            }
        });
        fabColapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToolbarCollapsed(false);
            }
        });
        animateFabIcon(0);
        final LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING);
        layoutTransition.disableTransitionType(LayoutTransition.APPEARING);
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        layoutTransition.setDuration(LayoutTransition.CHANGING, 100);
        clLayout.setLayoutTransition(layoutTransition);
    }

    @Override
    protected void onStart() {
        canToolbarCollapse = userSettingsDataManager.useImmersiveMode();
        super.onStart();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            adapter = new FeedViewPagerAdapter(getSupportFragmentManager(), appBar.getHeight());
            vpFeed.setAdapter(adapter);
            SHOWN_TOOLBAR_Y = 0;
            HIDDEN_TOOLBAR_Y = 0 - toolbar.getHeight();
        }
    }

    private void animateFabIcon(int tabPos) {
        switch (tabPos) {
            case 0:
                fabColapsed.setImageResource(ids[0]);
                break;
            case 1:
                fabColapsed.setImageResource(ids[1]);
                break;
            case 2:
                fabColapsed.setImageResource(ids[2]);
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fabColapsed.setImageTintList(ColorStateList.valueOf(getColorFromResources(themeManager.getAccentColor())));
        }
    }

    private void revealAccount(View view) {
        final Intent intent = new Intent(this, AccountActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this,
                        view,
                        getString(R.string.account_photo_transition));
        startActivity(intent, options.toBundle());
    }

    private void setColors(ThemeManager.Theme t) {
        fabColapsed.setBackgroundTintList(ColorStateList.valueOf(getColorFromResources(themeManager.getPrimaryColor())));
        tvAppName.setTextColor(getColorFromResources(themeManager.getMainTextToolbarColor()));
        appBar.setBackgroundResource(themeManager.getPrimaryColor());
        tlFeedTabs.setSelectedTabIndicatorColor(getColorFromResources(themeManager.getAccentColor()));
        if (tlFeedTabs.getTabAt(0) != null && tlFeedTabs.getTabAt(1) != null &&
                tlFeedTabs.getTabAt(2) != null) {
            tlFeedTabs.getTabAt(0).getIcon().setColorFilter(getColorFromResources(themeManager.getAccentColor()), PorterDuff.Mode.SRC_ATOP);
            tlFeedTabs.getTabAt(1).getIcon().setColorFilter(getColorFromResources(themeManager.getAccentColor()), PorterDuff.Mode.SRC_ATOP);
            tlFeedTabs.getTabAt(2).getIcon().setColorFilter(getColorFromResources(themeManager.getAccentColor()), PorterDuff.Mode.SRC_ATOP);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fabColapsed.setImageTintList(ColorStateList.valueOf(getColorFromResources(themeManager.getAccentColor())));
        }
    }

    private int getColorFromResources(int c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getColor(c);
        } else {
            return getResources().getColor(c);
        }
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        adapter.destroy();
        themeManager.unsubscribe(themeSubscribeId);
        super.onDestroy();
    }

    @Override
    public void onFeedMemSelected(MemEntity memEntity) {
        //show mem
        isFeed = true;
        isHot = false;
        isCategories = false;
        final Intent intent = new Intent(this, MemViewActivity.class);
        intent.putExtra(MEM_ENTITY, memEntity);
        startActivity(intent);
    }

    @Override
    public void onMemSelected(MemEntity memEntity) {
        isFeed = false;
        isHot = true;
        isCategories = false;
        final Intent intent = new Intent(this, MemViewActivity.class);
        intent.putExtra(MEM_ENTITY, memEntity);
        startActivity(intent);
    }

    @Override
    public void onMemCategorySelected(MemEntity memEntity) {
        isFeed = false;
        isHot = false;
        isCategories = true;
        final Intent intent = new Intent(this, MemViewActivity.class);
        intent.putExtra(MEM_ENTITY, memEntity);
        startActivity(intent);
    }

    @Override
    public void setScrollFlags() {
        final AppBarLayout.LayoutParams params =
                (AppBarLayout.LayoutParams) tlFeedTabs.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        tlFeedTabs.setLayoutParams(params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBar.setElevation(0);
        }
    }

    @Override
    public void resetScrollFlags() {
        final AppBarLayout.LayoutParams params =
                (AppBarLayout.LayoutParams) tlFeedTabs.getLayoutParams();
        params.setScrollFlags(0);
        tlFeedTabs.setLayoutParams(params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBar.setElevation(9);
        }
    }

    @Override
    public void passPostLike(String id) {
        if (isFeed) {
            adapter.postLikeFeed(id);
        } else if (isHot) {
            adapter.postLikeHot(id);
        } else if (isCategories) {
            adapter.postLikeCategories(id);
        }
    }

    @Override
    public void passDeleteLike(String id) {
        if (isFeed) {
            adapter.deleteLikeFeed(id);
        } else if (isHot) {
            adapter.deleteLikeHot(id);
        } else if (isCategories) {
            adapter.deleteLikeCategories(id);
        }
    }

    @Override
    public void passPostDislike(String id) {
        if (isFeed) {
            adapter.postDislikeFeed(id);
        } else if (isHot) {
            adapter.postDislikeHot(id);
        } else if (isCategories) {
            adapter.postDislikeCategories(id);
        }
    }

    @Override
    public void passDeleteDislike(String id) {
        if (isFeed) {
            adapter.deleteDislikeFeed(id);
        } else if (isHot) {
            adapter.deleteDislikeHot(id);
        } else if (isCategories) {
            adapter.deleteDislikeCategories(id);
        }
    }


    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(this);
    }

    @Override
    public void onError() {
        Toast.makeText(this, getText(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUsernameArrived(final String s) {
        sdvUserIcon.setImageURI(Uri.parse(IConstants.BASE_URL + "/feed/getUserPhoto?targetUsername=" + s));
    }

    @Override
    public void onFavoritesArrived(final List<FavoriteEntity> list) {
        this.favoriteEntityList.clear();
        this.favoriteEntityList.addAll(list);
        notifyFragments();
    }

    private void notifyFragments() {
        adapter.setFavoritesList(favoriteEntityList);
    }

    @Override
    public void notifyFavoriteAdded(FavoriteEntity favoriteEntity) {
        favoriteEntityList.add(favoriteEntity);
        notifyFragments();
    }

    @Override
    public void notifyOnScrollChanged(int distance) {
        boolean isScrollGoingUp = distance < 0;
        if (canToolbarCollapse) {
            if (!animIsPlaying) {
                if (!isToolbarCollapsed) {
                    if (distance > MIN_DISTANCE_THRESHOLD) {
                        setCooldown();
                        setToolbarCollapsed(true);
                    }
                }
                if (isToolbarCollapsed) {
                    float setY = distance > 0 ? fabColapsed.getY() - FAB_STEP : fabColapsed.getY() + FAB_STEP;
                    if (setY <= FAB_HIDDEN_Y) {
                        if (fabColapsed.getY() == FAB_HIDDEN_Y) return;
                        fabColapsed.setY(FAB_HIDDEN_Y);
                        return;
                    }
                    if (setY <= fabScrollYNormalPos) {
                        fabColapsed.setY(setY);
                    } else {
                        if (fabColapsed.getY() == fabScrollYNormalPos) return;
                        fabColapsed.setY(fabScrollYNormalPos);
                    }
                    if (isScrollGoingUp && fabColapsed.getY() != fabScrollYNormalPos && fabColapsed.getY() != FAB_HIDDEN_Y) {
                        if (isFeedScrollIdle) {
                            if (fabColapsed.getLocalVisibleRect(screenBounds)) {
                                ValueAnimator animator = ValueAnimator.ofFloat(fabColapsed.getY(), FAB_HIDDEN_Y);
                                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        fabColapsed.setY((float) animation.getAnimatedValue());
                                    }
                                });
                                animator.start();
                            }
                        }
                    }
                }
            }
        } else {
            float setY = distance > 0 ? appBar.getY() - FAB_STEP : appBar.getY() + FAB_STEP;
            if (setY <= HIDDEN_TOOLBAR_Y) {
                if (appBar.getY() == HIDDEN_TOOLBAR_Y) return;
                appBar.setY(HIDDEN_TOOLBAR_Y);
                return;
            }
            if (setY <= SHOWN_TOOLBAR_Y) {
                appBar.setY(setY);
            } else {
                if (fabColapsed.getY() == SHOWN_TOOLBAR_Y) return;
                appBar.setY(SHOWN_TOOLBAR_Y);
            }
            if (isScrollGoingUp && appBar.getY() != SHOWN_TOOLBAR_Y && appBar.getY() != HIDDEN_TOOLBAR_Y) {
                if (isFeedScrollIdle) {
                    if (appBar.getLocalVisibleRect(screenBounds)) {
                        ValueAnimator animator = ValueAnimator.ofFloat(appBar.getY(), HIDDEN_TOOLBAR_Y);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                appBar.setY((float) animation.getAnimatedValue());
                            }
                        });
                        animator.start();
                    }
                }
            }
        }
    }

    @Override
    public void launchMemView(View animStart, String transitionName, MemEntity memEntity) {
        final Fragment fragment = MemViewFragment.newInstance(memEntity, transitionName);
        fragment.setSharedElementEnterTransition(new DetailsTransition());
        fragment.setEnterTransition(new Fade());
        fragment.setSharedElementReturnTransition(new DetailsTransition());
        getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(animStart, transitionName)
                .replace(R.id.feedContainer, fragment)
                .addToBackStack(null)
                .commit();
        hideControls();
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                    clLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideControls() {
        clLayout.setVisibility(View.GONE);
    }

    @Override
    public void closeMemView() {
        clLayout.setVisibility(View.VISIBLE);
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void launchMemView(MemEntity memEntity) {
        container.bringToFront();
        final Intent intent = new Intent(this, MemViewActivity.class);
        intent.putExtra(MEM_ENTITY, memEntity);
        startActivity(intent);
    }

    @Override
    public void notifyFeedScrollIdle(boolean b) {
        isFeedScrollIdle = b;
        if (isFeedScrollIdle) {
            if (fabColapsed.getLocalVisibleRect(screenBounds)) {
                ValueAnimator animator = ValueAnimator.ofFloat(fabColapsed.getY(), fabScrollYNormalPos);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        fabColapsed.setY((float) animation.getAnimatedValue());
                    }
                });
                animator.start();
            }
        }
    }

    @Override
    public void notifyFeedOnTop() {
        if (isToolbarCollapsed)
            setToolbarCollapsed(false);
    }

    private void setToolbarCollapsed(boolean collapseToolbar) {
        if (canToolbarCollapse) {
            if (collapseToolbar) {
                unrevealToolbar();
            } else {
                revealToolbar();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void revealToolbar() {
        appBar.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        isToolbarCollapsed = false;
        int x = (int) (fabColapsed.getX() + fabColapsed.getWidth() / 2);
        int y = (int) (fabColapsed.getY() + fabColapsed.getHeight() / 2);
        int endRadius = (int) Math.hypot(appBar.getWidth(), appBar.getHeight());
        Animator revealAnim = ViewAnimationUtils.createCircularReveal(appBar, x, y, 28, endRadius);
        revealAnim.setDuration(500);
        revealAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                animIsPlaying = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animIsPlaying = false;
                fabColapsed.setY(fabScrollYNormalPos);
                appBar.setLayerType(View.LAYER_TYPE_NONE, null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        fabColapsed.setVisibility(View.GONE);
        appBar.setVisibility(View.VISIBLE);
        revealAnim.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void unrevealToolbar() {
        appBar.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        int x = (int) (fabColapsed.getX() + fabColapsed.getWidth() / 2);
        int y = (int) (fabColapsed.getY() + fabColapsed.getHeight() / 2);
        int startRadius = (int) Math.hypot(appBar.getWidth(), appBar.getHeight());
        int endRadius = fabColapsed.getWidth() / 2;
        Animator unrevealAnim = ViewAnimationUtils.createCircularReveal(appBar, x, y, startRadius, endRadius);
        unrevealAnim.setDuration(500);
        unrevealAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                animIsPlaying = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                appBar.setLayerType(View.LAYER_TYPE_NONE, null);
                isToolbarCollapsed = true;
                animIsPlaying = false;
                fabColapsed.setVisibility(View.VISIBLE);
                appBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        unrevealAnim.start();
    }

    private void setCooldown() {
        cooldownPassed[0] = false;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cooldownPassed[0] = true;
            }
        }, 300);
    }

    private class DetailsTransition extends TransitionSet {
        DetailsTransition() {
            setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeBounds());
        }
    }
}
