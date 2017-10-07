package dustit.clientapp.mvp.ui.activities;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.presenters.activities.FeedActivityPresenter;
import dustit.clientapp.mvp.ui.adapters.FeedViewPagerAdapter;
import dustit.clientapp.mvp.ui.fragments.FeedFragment;
import dustit.clientapp.mvp.ui.fragments.HotFragment;
import dustit.clientapp.mvp.ui.fragments.MemViewFragment;
import dustit.clientapp.mvp.ui.interfaces.IFeedActivityView;

public class FeedActivity extends AppCompatActivity implements FeedFragment.IFeedFragmentInteractionListener,
        HotFragment.IHotFragmentInteractionListener,
        MemViewFragment.IMemViewFragmentInteractionListener,
        IFeedActivityView {
    @BindView(R.id.tlFeedTabs)
    TabLayout tlFeedTabs;
    @BindView(R.id.sdvUserIcon)
    SimpleDraweeView sdvUserIcon;
    @BindView(R.id.vpFeedPager)
    ViewPager vpFeed;
    @BindView(R.id.rvFeedContainer)
    RelativeLayout rvContainer;

    private FeedViewPagerAdapter adapter;
    private FeedActivityPresenter presenter;

    private boolean isFeed = false;
    private boolean isHot = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        presenter = new FeedActivityPresenter();
        presenter.bind(this);
        ButterKnife.bind(this);
        tlFeedTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpFeed.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        adapter = new FeedViewPagerAdapter(getSupportFragmentManager());
        vpFeed.setAdapter(adapter);
        vpFeed.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tlFeedTabs));
        sdvUserIcon.setImageURI(Uri.parse("http://www.uni-regensburg.de/Fakultaeten/phil_Fak_II/Psychologie/Psy_II/beautycheck/english/durchschnittsgesichter/m(01-32)_gr.jpg"));
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }

    @Override
    public void onFeedMemSelected(MemEntity memEntity) {
        //show mem
        isFeed = true;
        rvContainer.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.rvFeedContainer, MemViewFragment.newInstance(memEntity))
                .commit();
    }

    @Override
    public void closeFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rvFeedContainer, null)
                .commit();
        rvContainer.setVisibility(View.GONE);
    }

    @Override
    public void onLikePressed(String id) {
        presenter.postLike(id);
        if (isFeed) {

        } else if (isHot) {

        }
    }
}
