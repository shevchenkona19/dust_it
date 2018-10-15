package dustit.clientapp.mvp.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.lang.ref.WeakReference;

import dustit.clientapp.mvp.ui.fragments.CategoriesFragment;
import dustit.clientapp.mvp.ui.fragments.FeedFragment;
import dustit.clientapp.mvp.ui.fragments.HotFragment;
import dustit.clientapp.utils.L;

/**
 * Created by shevc on 04.10.2017
 * Let's GO!
 */

public class FeedViewPagerAdapter extends FragmentPagerAdapter {

    public FeedViewPagerAdapter(FragmentManager fm, int appBarHeight) {
        super(fm);
        appbarHeight = appBarHeight;
    }

    private int appbarHeight;
    private WeakReference<FeedFragment> feedFragment;
    private WeakReference<HotFragment> hotFragment;
    private WeakReference<CategoriesFragment> categoriesFragment;

    private boolean isCategoriesLoaded;

    @Override
    public Fragment getItem(int position) {
        Fragment selectedFragment;
        switch (position) {
            case 0:
                FeedFragment feedFragment = FeedFragment.Companion.newInstance(appbarHeight);
                this.feedFragment = new WeakReference<>(feedFragment);
                selectedFragment = feedFragment;
                break;
            case 1:
                HotFragment hotFragment = HotFragment.newInstance(appbarHeight);
                this.hotFragment = new WeakReference<>(hotFragment);
                selectedFragment = hotFragment;
                break;
            case 2:
                CategoriesFragment categoriesFragment = CategoriesFragment.newInstance(appbarHeight, isCategoriesLoaded);
                this.categoriesFragment = new WeakReference<>(categoriesFragment);
                selectedFragment = categoriesFragment;
                break;
            default:
                selectedFragment = null;
        }
        return selectedFragment;
    }

    public void setCategoriesLoaded(boolean categoriesLoaded) {
        isCategoriesLoaded = categoriesLoaded;
        categoriesFragment.get().onCategoriesLoaded(categoriesLoaded);
    }

    public void scrollToTop(int itemNum) {
        switch (itemNum) {
            case 0:
                if (feedFragment != null)
                    feedFragment.get().scrollToTop();
                break;
            case 1:
                if (hotFragment != null)
                    hotFragment.get().scrollToTop();
                break;
            case 2:
                if (categoriesFragment != null)
                    categoriesFragment.get().scrollToTop();
                break;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public void destroy() {
        if (feedFragment != null) feedFragment.clear();
        if (hotFragment != null) hotFragment.clear();
        if (categoriesFragment != null) categoriesFragment.clear();
    }
}
