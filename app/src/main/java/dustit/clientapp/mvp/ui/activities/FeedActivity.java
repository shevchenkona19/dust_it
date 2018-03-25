package dustit.clientapp.mvp.ui.activities;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.transitionseverywhere.ArcMotion;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.TransitionManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.model.entities.FavoriteEntity;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.presenters.activities.FeedActivityPresenter;
import dustit.clientapp.mvp.ui.adapters.FeedViewPagerAdapter;
import dustit.clientapp.mvp.ui.base.BaseFeedFragment;
import dustit.clientapp.mvp.ui.fragments.CategoriesFragment;
import dustit.clientapp.mvp.ui.fragments.HotFragment;
import dustit.clientapp.mvp.ui.fragments.MemViewActivity;
import dustit.clientapp.mvp.ui.fragments.MemViewFragment;
import dustit.clientapp.mvp.ui.interfaces.IFeedActivityView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.managers.ThemeManager;

public class FeedActivity extends AppCompatActivity implements
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
    @BindView(R.id.spCategoriesChooser)
    Spinner spCategoriesChooser;

    private FeedViewPagerAdapter adapter;
    private FeedActivityPresenter presenter;

    private boolean isFeed = false;
    private boolean isHot = false;
    private boolean isCategories = false;

    private boolean isFirstLaunch = true;

    private boolean isToolbarCollapsed = false;
    private boolean animIsPlaying = false;
    private boolean canToolbarCollapse = false;

    private float fabScrollYNormalPos;

    private final Rect screenBounds = new Rect();

    private boolean[] cooldownPassed = {true};

    private final Handler handler = new Handler();

    private final List<FavoriteEntity> favoriteEntityList = new ArrayList<>();

    private final int[] ids = {R.drawable.ic_feed_pressed, R.drawable.ic_hot_pressed, R.drawable.ic_categories_pressed};

    public interface ICategoriesSpinnerInteractionListener {
        void onCategoriesArrived();

        void onCategorySelected(Category category);
    }

    private ICategoriesSpinnerInteractionListener spinnerInteractionListener;

    @Inject
    ThemeManager themeManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get().getAppComponent().inject(this);
        setContentView(R.layout.activity_feed);
        presenter = new FeedActivityPresenter();
        presenter.bind(this);
        ButterKnife.bind(this);
        presenter.getMyFavorites();
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
                        final Animation fromCenterToLeft = AnimationUtils.loadAnimation(FeedActivity.this, R.anim.from_center_to_left);
                        final Animation fromRightToCenter = AnimationUtils.loadAnimation(FeedActivity.this, R.anim.from_right_to_center);
                        fromCenterToLeft.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                tvAppName.setVisibility(View.GONE);
                                fromRightToCenter.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        spCategoriesChooser.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                spCategoriesChooser.startAnimation(fromRightToCenter);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        tvAppName.startAnimation(fromCenterToLeft);
                        vpFeed.setCurrentItem(2, true);
                        return;
                }
                if (tvAppName.getVisibility() != View.VISIBLE) {
                    final Animation fromCenterToRight = AnimationUtils.loadAnimation(FeedActivity.this,
                            R.anim.from_center_to_right);
                    final Animation fromLeftToCenter = AnimationUtils.loadAnimation(FeedActivity.this,
                            R.anim.from_left_to_center);
                    fromCenterToRight.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            spCategoriesChooser.setVisibility(View.GONE);
                            fromLeftToCenter.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    tvAppName.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            tvAppName.startAnimation(fromLeftToCenter);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    spCategoriesChooser.startAnimation(fromCenterToRight);
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
        presenter.getCategories();
    }

    @Override
    protected void onStart() {
        canToolbarCollapse = userSettingsDataManager.useImmersiveMode();
        super.onStart();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && isFirstLaunch) {
            isFirstLaunch = false;
            adapter = new FeedViewPagerAdapter(getSupportFragmentManager(), appBar.getHeight());
            if (vpFeed.getAdapter() == null)
                vpFeed.setAdapter(adapter);
            vpFeed.setOffscreenPageLimit(3);
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
    }

    private void revealAccount(View view) {
        final Intent intent = new Intent(this, AccountActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this,
                        view,
                        getString(R.string.account_photo_transition));
        startActivity(intent, options.toBundle());
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        adapter.destroy();
        super.onDestroy();
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

    @Override
    public void onCategoriesArrived(final List<Category> categoryList) {
        adapter.setCategoriesLoaded(true);
        final List<String> categoryNames = new ArrayList<>();
        for (Category category : categoryList) categoryNames.add(category.getName());
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoriesChooser.setAdapter(adapter);
        spCategoriesChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerInteractionListener != null) {
                    spinnerInteractionListener.onCategorySelected(categoryList.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (spinnerInteractionListener != null)
            spinnerInteractionListener.onCategoriesArrived();
    }

    @Override
    public void onCategoriesFailedToLoad() {
        adapter.setCategoriesLoaded(false);
        onError();
    }

    private void notifyFragments() {
        if (adapter != null)
            adapter.setFavoritesList(favoriteEntityList);
    }

    @Override
    public void notifyFavoriteAdded(FavoriteEntity favoriteEntity) {
        favoriteEntityList.add(favoriteEntity);
        notifyFragments();
    }

    @Override
    public void notifyOnScrollChanged(int distance) {
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
                }
            }
        } else {
            float setY = distance > 0 ? appBar.getY() - FAB_STEP : appBar.getY() + FAB_STEP;
            if (setY <= HIDDEN_TOOLBAR_Y) {
                if (appBar.getY() != HIDDEN_TOOLBAR_Y) {
                    appBar.setY(HIDDEN_TOOLBAR_Y);
                }
            } else if (setY <= SHOWN_TOOLBAR_Y) {
                appBar.setY(setY);
            } else {
                if (appBar.getY() == SHOWN_TOOLBAR_Y) return;
                appBar.setY(SHOWN_TOOLBAR_Y);
            }
        }
    }

    @Override
    public void launchMemView(View holder, MemEntity memEntity) {
        final View v = holder.findViewById(R.id.sdvItemFeed);
        ViewCompat.setTransitionName(v, getString(R.string.mem_feed_transition_name));
        final Fragment fragment = MemViewFragment.newInstance(memEntity);
        Transition transition = TransitionInflater
                .from(this).inflateTransition(R.transition.mem_view_transition);
        fragment.setSharedElementEnterTransition(transition);
        fragment.setSharedElementReturnTransition(transition);
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addSharedElement(v, getString(R.string.mem_feed_transition_name))
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
        final Intent intent = new Intent(this, MemViewActivity.class);
        intent.putExtra(MEM_ENTITY, memEntity);
        startActivity(intent);
    }

    @Override
    public void notifyFeedScrollIdle(boolean b) {
        isFeedScrollIdle = b;
        if (canToolbarCollapse) {
            if (isFeedScrollIdle) {
                if (fabColapsed.getLocalVisibleRect(screenBounds) && fabColapsed.getY() != fabScrollYNormalPos) {
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
        } else {
            if (isFeedScrollIdle) {
                if (appBar.getY() != SHOWN_TOOLBAR_Y && appBar.getY() != HIDDEN_TOOLBAR_Y) {
                    ValueAnimator animator = ValueAnimator.ofFloat(appBar.getY(), SHOWN_TOOLBAR_Y);
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

    @Override
    public void notifyFeedOnTop() {
        if (canToolbarCollapse) {
            if (isToolbarCollapsed)
                setToolbarCollapsed(false);
        } else {
            if (appBar.getY() != SHOWN_TOOLBAR_Y) {
                ValueAnimator animator = ValueAnimator.ofFloat(appBar.getY(), SHOWN_TOOLBAR_Y);
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
        isToolbarCollapsed = false;
        final int x = (int) (fabColapsed.getX() + fabColapsed.getWidth() / 2);
        final int y = (int) (fabColapsed.getY() + fabColapsed.getHeight() / 2);
        appBar.setLayerType(View.LAYER_TYPE_HARDWARE, null);
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

    @Override
    protected void onResume() {
        super.onResume();
        fabColapsed.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
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

    @Override
    public void onAttachToActivity(ICategoriesSpinnerInteractionListener listener) {
        spinnerInteractionListener = listener;
    }

    @Override
    public void onDetachFromActivity() {
        spinnerInteractionListener = null;
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
}
