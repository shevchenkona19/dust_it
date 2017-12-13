package dustit.clientapp.mvp.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.lang.ref.WeakReference;

import dustit.clientapp.mvp.ui.fragments.CategoriesFragment;
import dustit.clientapp.mvp.ui.fragments.FeedFragment;
import dustit.clientapp.mvp.ui.fragments.HotFragment;

/**
 * Created by shevc on 04.10.2017
 * Let's GO!
 */

public class FeedViewPagerAdapter extends FragmentPagerAdapter {

    public FeedViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private WeakReference<FeedFragment> feedFragment;
    private WeakReference<HotFragment> hotFragment;
    private WeakReference<CategoriesFragment> categoriesFragment;

    @Override
    public Fragment getItem(int position) {
        Fragment selectedFragment;
        switch (position) {
            case 0:
                FeedFragment feedFragment = FeedFragment.newInstance();
                this.feedFragment = new WeakReference<>(feedFragment);
                selectedFragment = feedFragment;
                break;
            case 1:
                HotFragment hotFragment = HotFragment.newInstance();
                this.hotFragment = new WeakReference<>(hotFragment);
                selectedFragment = hotFragment;
                break;
            case 2:
                CategoriesFragment categoriesFragment = CategoriesFragment.newInstance();
                this.categoriesFragment = new WeakReference<>(categoriesFragment);
                selectedFragment = categoriesFragment;
                break;
            default:
                selectedFragment = null;
        }
        return selectedFragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    public void postLikeFeed(String id) {
        feedFragment.get().passPostLike(id);
    }

    public void postLikeHot(String id) {
        hotFragment.get().passPostLike(id);
    }

    public void deleteLikeFeed(String id) {
        feedFragment.get().passDeleteLike(id);
    }

    public void deleteLikeHot(String id) {
        hotFragment.get().passDeleteLike(id);
    }

    public void postDislikeFeed(String id) {
        feedFragment.get().passPostDislike(id);
    }

    public void postDislikeHot(String id) {
        hotFragment.get().passPostDislike(id);
    }

    public void deleteDislikeFeed(String id) {
        feedFragment.get().passDeleteDislike(id);
    }

    public void deleteDislikeHot(String id) {
        hotFragment.get().passDeleteDislike(id);
    }

    public void postLikeCategories(String id) {
        categoriesFragment.get().passPostLike(id);
    }

    public void deleteLikeCategories(String id) {
        categoriesFragment.get().passDeleteLike(id);
    }

    public void postDislikeCategories(String id) {
        categoriesFragment.get().passPostDislike(id);
    }

    public void deleteDislikeCategories(String id) {
        categoriesFragment.get().passDeleteDislike(id);
    }

    public void destroy() {
        if (feedFragment != null) feedFragment.clear();
        if (hotFragment != null) hotFragment.clear();
        if (categoriesFragment != null) categoriesFragment.clear();
    }
}
