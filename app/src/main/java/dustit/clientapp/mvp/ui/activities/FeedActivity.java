package dustit.clientapp.mvp.ui.activities;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.ui.adapters.FeedViewPagerAdapter;
import dustit.clientapp.mvp.ui.fragments.FeedFragment;
import dustit.clientapp.mvp.ui.fragments.HotFragment;

public class FeedActivity extends AppCompatActivity implements FeedFragment.IFeedFragmentInteractionListener, HotFragment.IHotFragmentInteractionListener{
    @BindView(R.id.tlFeedTabs)
    TabLayout tlFeedTabs;
    @BindView(R.id.sdvUserIcon)
    SimpleDraweeView sdvUserIcon;
    @BindView(R.id.vpFeedPager)
    ViewPager vpFeed;

    private FeedViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
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
}
