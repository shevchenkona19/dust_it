package dustit.clientapp.mvp.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.presenters.activities.FeedActivityPresenter;
import dustit.clientapp.mvp.ui.adapters.FeedViewPagerAdapter;
import dustit.clientapp.mvp.ui.fragments.CategoriesFragment;
import dustit.clientapp.mvp.ui.fragments.FeedFragment;
import dustit.clientapp.mvp.ui.fragments.HotFragment;
import dustit.clientapp.mvp.ui.interfaces.IFeedActivityView;

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

    private FeedViewPagerAdapter adapter;
    private FeedActivityPresenter presenter;

    private boolean isFeed = false;
    private boolean isHot = false;
    private boolean isCategories = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        presenter = new FeedActivityPresenter();
        presenter.bind(this);
        ButterKnife.bind(this);
        adapter = new FeedViewPagerAdapter(getSupportFragmentManager());
        vpFeed.setOffscreenPageLimit(3);
        vpFeed.setAdapter(adapter);
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
        sdvUserIcon.setImageURI(Uri.parse("http://www.uni-regensburg.de/Fakultaeten/phil_Fak_II/Psychologie/Psy_II/beautycheck/english/durchschnittsgesichter/m(01-32)_gr.jpg"));
        sdvUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FeedActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        adapter.destroy();
        super.onDestroy();
    }

    @Override
    public void onFeedMemSelected(MemEntity memEntity) {
        //show mem
        isFeed = true;
        Intent intent = new Intent(this, MemViewActivity.class);
        intent.putExtra(MEM_ENTITY, memEntity);
        startActivity(intent);
        MemViewActivity.bind(this);
    }

    @Override
    public void onMemSelected(MemEntity memEntity) {

    }

    @Override
    public void onMemCategorySelected(MemEntity memEntity) {

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
}
