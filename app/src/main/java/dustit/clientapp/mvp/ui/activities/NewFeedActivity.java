package dustit.clientapp.mvp.ui.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.transition.Transition;
import android.support.transition.TransitionInflater;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.presenters.activities.FeedActivityPresenter;
import dustit.clientapp.mvp.ui.adapters.FeedViewPagerAdapter;
import dustit.clientapp.mvp.ui.base.BaseFeedFragment;
import dustit.clientapp.mvp.ui.fragments.CategoriesFragment;
import dustit.clientapp.mvp.ui.fragments.MemViewFragment;
import dustit.clientapp.mvp.ui.interfaces.IFeedActivityView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.FeedPageTransformer;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.L;
import dustit.clientapp.utils.bus.FavouritesBus;
import dustit.clientapp.utils.managers.NotifyManager;
import dustit.clientapp.utils.managers.ThemeManager;

public class NewFeedActivity extends AppCompatActivity implements CategoriesFragment.ICategoriesFragmentInteractionListener, IFeedActivityView, MemViewFragment.IMemViewRatingInteractionListener, BaseFeedFragment.IBaseFragmentInteraction {
    @BindView(R.id.vpFeedPager)
    ViewPager vpFeed;
    @BindView(R.id.bnvFeedNavigation)
    BottomNavigationView bnvFeed;
    @BindView(R.id.sdvUserIcon)
    SimpleDraweeView sdvUserIcon;
    @BindView(R.id.tbFeedActivity)
    View toolbar;
    @BindView(R.id.clMainLayout)
    ViewGroup clLayout;
    @BindView(R.id.spCategoriesChooser)
    Spinner spCategoriesChooser;
    @BindView(R.id.tvActivityFeedAppName)
    View tvAppName;

    private FeedViewPagerAdapter adapter;
    private FeedActivityPresenter presenter = new FeedActivityPresenter();

    private boolean isFirstLaunch = true;
    private ICategoriesSpinnerInteractionListener spinnerInteractionListener;

    private List<Category> categories = new ArrayList<>();

    @Inject
    UserSettingsDataManager userSettingsDataManager;

    public interface ICategoriesSpinnerInteractionListener {
        void onCategoriesArrived();

        void onCategoriesFailed();

        void onCategorySelected(Category category);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get().getAppComponent().inject(this);
        setContentView(R.layout.activity_new_feed);
        presenter.bind(this);
        ButterKnife.bind(this);
        sdvUserIcon.setLegacyVisibilityHandlingEnabled(true);
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                clLayout.setVisibility(View.VISIBLE);
            }
        });
        adapter = new FeedViewPagerAdapter(getSupportFragmentManager(), toolbar.getHeight());
        if (userSettingsDataManager.isRegistered()) presenter.getMyUsername();
        bnvFeed.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.feed:
                    vpFeed.setCurrentItem(0);
                    break;
                case R.id.hot:
                    vpFeed.setCurrentItem(1);
                    break;
                case R.id.categories:
                    Animation fromCenterToLeft = AnimationUtils.loadAnimation(this, R.anim.from_center_to_left);
                    Animation fromRightToCenter = AnimationUtils.loadAnimation(this, R.anim.from_right_to_center);
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
                    vpFeed.setCurrentItem(2);
                    return true;
                default:
                    return false;
            }
            if (tvAppName.getVisibility() != View.VISIBLE) {
                Animation fromCenterToRight = AnimationUtils.loadAnimation(this,
                        R.anim.from_center_to_right);
                Animation fromLeftToCenter = AnimationUtils.loadAnimation(this,
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
            return true;
        });
        sdvUserIcon.setOnClickListener(this::revealAccount);
        if (userSettingsDataManager.isNoRegistration()) {
            sdvUserIcon.setImageURI("android.resource://" + getPackageName() + "/drawable/noimage");
        }
        vpFeed.setPageTransformer(false, new FeedPageTransformer());
        toolbar.setOnClickListener(v -> adapter.scrollToTop(vpFeed.getCurrentItem()));
        presenter.getCategories();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, NotifyManager.class));
        } else {
            startService(new Intent(this, NotifyManager.class));
        }
        showComments();
        if (!userSettingsDataManager.isFcmUpdated()) {
            presenter.updateFcmId();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                int flags = 0;
                window.getDecorView().setSystemUiVisibility(flags);
            } else {
                int flags = window.getDecorView().getSystemUiVisibility();
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.getDecorView().setSystemUiVisibility(flags);
                window.setStatusBarColor(Color.WHITE);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && isFirstLaunch) {
            isFirstLaunch = false;
            if (adapter == null)
                adapter = new FeedViewPagerAdapter(getSupportFragmentManager(), toolbar.getHeight());
            if (vpFeed.getAdapter() == null)
                vpFeed.setAdapter(adapter);
            vpFeed.setOffscreenPageLimit(3);
        }
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        adapter.destroy();
        FavouritesBus.destroy();
        super.onDestroy();
    }

    private void showComments() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            Bundle extras = intent.getExtras();
            if (extras.getBoolean(IConstants.IBundle.SHOW_COMMENTS)) {
                String memId = extras.getString(IConstants.IBundle.MEM_ID);
                String parentComment = extras.getString(IConstants.IBundle.PARENT_COMMENT_ID);
                String newComment = extras.getString(IConstants.IBundle.NEW_COMMENT_ID);
                presenter.loadMemForComments(memId, parentComment, newComment);
            }
        }
    }

    private void revealAccount(View view) {
        Intent intent = new Intent(this, NewAccountActivity.class);
        intent.putExtra(IConstants.IBundle.IS_ME, true);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                view,
                getString(R.string.account_photo_transition));
        startActivity(intent, options.toBundle());
    }

    @Override
    public void notifyOnScrollChanged(int distance) {

    }

    @Override
    public void launchMemView(View holder, MemEntity memEntity, boolean startComments) {
        MemViewFragment fragment = MemViewFragment.newInstance(memEntity, startComments, presenter.loadId());
        showFragment(fragment);
    }

    private void launchMemViewForComments(MemEntity memEntity, boolean startComments, String
            parentComment, String newComment) {
        MemViewFragment fragment = MemViewFragment.newInstance(memEntity, startComments, presenter.loadId(), parentComment, newComment);
        showFragment(fragment);
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.feedContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void notifyFeedScrollIdle(boolean b) {

    }

    @Override
    public boolean isRegistered() {
        return userSettingsDataManager.isRegistered();
    }

    @Override
    public void notifyFeedOnTop() {

    }

    @Override
    public void gotoFragment(byte id) {
        vpFeed.setCurrentItem(id, true);
    }

    @Override
    public void onError(String error) {
        showError(error);
    }

    @Override
    public void onAttachToActivity(ICategoriesSpinnerInteractionListener listener) {
        spinnerInteractionListener = listener;
    }

    @Override
    public void reloadCategories() {
        presenter.getCategories();
    }

    @Override
    public void onDetachFromActivity() {
        spinnerInteractionListener = null;
    }

    @Override
    public void closeMemView() {
        clLayout.setVisibility(View.VISIBLE);
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onError() {
        showError();
    }

    private void showError(String message) {
        Snackbar.make(clLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void showError() {
        showError(getString(R.string.error));
    }

    @Override
    public void onUsernameArrived(String s) {
        sdvUserIcon.setImageURI(IConstants.BASE_URL + "/feed/userPhoto?targetUsername=" + s);
    }

    @Override
    public void onCategoriesArrived(List<Category> categoryList) {
        if (!categoryList.isEmpty()) {
            categories = categoryList;
            List<String> categoryNames = new ArrayList<>();
            for (Category category : categoryList) categoryNames.add(category.getName());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, categoryNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCategoriesChooser.setAdapter(adapter);
            if (spinnerInteractionListener != null)
                spinnerInteractionListener.onCategoriesArrived();
            spCategoriesChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (view != null)
                        if (spinnerInteractionListener != null)
                            spinnerInteractionListener.onCategorySelected(categories.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    @Override
    public void onCategoriesFailedToLoad() {
        if (adapter != null) {
            adapter.setCategoriesLoaded(false);
        }
        if (spinnerInteractionListener != null)
            spinnerInteractionListener.onCategoriesFailed();
        onError();
    }

    @Override
    public void onMemReadyForComments(MemEntity memEntity, String parentComment, String
            newComment) {
        launchMemViewForComments(memEntity, true, parentComment, newComment);
    }

    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(this);
    }
}
