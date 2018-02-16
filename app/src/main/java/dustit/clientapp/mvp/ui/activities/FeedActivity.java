package dustit.clientapp.mvp.ui.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.presenters.activities.FeedActivityPresenter;
import dustit.clientapp.mvp.ui.adapters.FeedViewPagerAdapter;
import dustit.clientapp.mvp.ui.fragments.CategoriesFragment;
import dustit.clientapp.mvp.ui.fragments.FeedFragment;
import dustit.clientapp.mvp.ui.fragments.HotFragment;
import dustit.clientapp.mvp.ui.interfaces.IFeedActivityView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.managers.ThemeManager;

public class FeedActivity extends AppCompatActivity implements FeedFragment.IFeedFragmentInteractionListener,
        HotFragment.IHotFragmentInteractionListener,
        CategoriesFragment.ICategoriesFragmentInteractionListener,
        IFeedActivityView,
        MemViewActivity.IMemViewRatingInteractionListener {

    private static final String VIEW_FRAGMENT = "view";
    public static final String MEM_ENTITY = "kek";

    @BindView(R.id.tlFeedTabs)
    TabLayout tlFeedTabs;
    @BindView(R.id.sdvUserIcon)
    SimpleDraweeView sdvUserIcon;
    @BindView(R.id.vpFeedPager)
    ViewPager vpFeed;
    @BindView(R.id.rvFeedContainer)
    RelativeLayout rvContainer;
    @BindView(R.id.clMainLayout)
    CoordinatorLayout clLayout;
    @BindView(R.id.rlActivityFeedMainContainer)
    RelativeLayout rlMainContainer;
    @BindView(R.id.tvActivityFeedAppName)
    TextView tvAppName;
    @BindView(R.id.appBarActivityFeed)
    AppBarLayout appBar;

    private FeedViewPagerAdapter adapter;
    private FeedActivityPresenter presenter;

    private boolean isFeed = false;
    private boolean isHot = false;
    private boolean isCategories = false;

    private String themeSubscribeId;

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
        App.get().getAppComponent().inject(this);
        adapter = new FeedViewPagerAdapter(getSupportFragmentManager());
        vpFeed.setOffscreenPageLimit(3);
        vpFeed.setAdapter(adapter);
        presenter.getMyUsername();
        vpFeed.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tlFeedTabs));
        tlFeedTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
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

            }
        });
        sdvUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FeedActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });
        setColors();
        themeSubscribeId = themeManager.subscribeToThemeChanges(new ThemeManager.IThemable() {
            @Override
            public void notifyThemeChanged(ThemeManager.Theme t) {
                setColors();
            }
        });
    }

    private void setColors() {
        rlMainContainer.setBackgroundResource(themeManager.getBackgroundMainColor());
        tvAppName.setTextColor(getColorFromResources(themeManager.getMainTextToolbarColor()));
        appBar.setBackgroundResource(themeManager.getPrimaryColor());
        tlFeedTabs.setSelectedTabIndicatorColor(getColorFromResources(themeManager.getAccentColor()));
        if (tlFeedTabs.getTabAt(0) != null && tlFeedTabs.getTabAt(1) != null &&
                tlFeedTabs.getTabAt(2) != null) {
            tlFeedTabs.getTabAt(0).getIcon().setColorFilter(getColorFromResources(themeManager.getAccentColor()), PorterDuff.Mode.SRC_ATOP);
            tlFeedTabs.getTabAt(1).getIcon().setColorFilter(getColorFromResources(themeManager.getAccentColor()), PorterDuff.Mode.SRC_ATOP);
            tlFeedTabs.getTabAt(2).getIcon().setColorFilter(getColorFromResources(themeManager.getAccentColor()), PorterDuff.Mode.SRC_ATOP);

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
        Intent intent = new Intent(this, MemViewActivity.class);
        intent.putExtra(MEM_ENTITY, memEntity);
        startActivity(intent);
        MemViewActivity.bind(this);
    }

    @Override
    public void onMemSelected(MemEntity memEntity) {
        isFeed = false;
        isHot = true;
        isCategories = false;
        Intent intent = new Intent(this, MemViewActivity.class);
        intent.putExtra(MEM_ENTITY, memEntity);
        startActivity(intent);
        MemViewActivity.bind(this);
    }

    @Override
    public void onMemCategorySelected(MemEntity memEntity) {
        isFeed = false;
        isHot = false;
        isCategories = true;
        Intent intent = new Intent(this, MemViewActivity.class);
        intent.putExtra(MEM_ENTITY, memEntity);
        startActivity(intent);
        MemViewActivity.bind(this);
    }

    @Override
    public void setScrollFlags() {
        AppBarLayout.LayoutParams params =
                (AppBarLayout.LayoutParams) tlFeedTabs.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
    }

    @Override
    public void resetScrollFlags() {
        AppBarLayout.LayoutParams params =
                (AppBarLayout.LayoutParams) tlFeedTabs.getLayoutParams();
        params.setScrollFlags(0);
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
    public void onUsernameArrived(String s) {
        sdvUserIcon.setImageURI(Uri.parse(IConstants.BASE_URL + "/feed/getUserPhoto?targetUsername=" + s));
    }
}
