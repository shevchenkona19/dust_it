package dustit.clientapp.mvp.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import dustit.clientapp.mvp.ui.fragments.FeedFragment;
import dustit.clientapp.mvp.ui.fragments.HotFragment;

/**
 * Created by shevc on 04.10.2017.
 * Let's GO!
 */

public class FeedViewPagerAdapter extends FragmentPagerAdapter {

    public FeedViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment selectedFragment;
        switch (position) {
            case 0:
                selectedFragment = FeedFragment.newInstance();
                break;
            case 1:
                selectedFragment = HotFragment.newInstance();
                break;
            default:
                selectedFragment = null;
        }
        return selectedFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
