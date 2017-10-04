package dustit.clientapp.mvp.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import dustit.clientapp.mvp.ui.fragments.MemTestFragment;

/**
 * Created by shevc on 30.09.2017.
 * Let's GO!
 */

public class TestViewPagerAdapter extends FragmentStatePagerAdapter {

    private int count;

    public TestViewPagerAdapter(FragmentManager fm, int count) {
        super(fm);
        this.count = count;
    }

    @Override
    public Fragment getItem(int position) {
        return MemTestFragment.newInstance(position ,"");
    }

    @Override
    public int getCount() {
        return count;
    }
}
