package dustit.clientapp.mvp.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import dustit.clientapp.mvp.model.entities.FavoriteEntity;
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
    private final List<FavoriteEntity> list = new ArrayList<>();

    private boolean isCategoriesLoaded;

    @Override
    public Fragment getItem(int position) {
        L.print("getItem");
        Fragment selectedFragment;
        switch (position) {
            case 0:
                FeedFragment feedFragment = FeedFragment.newInstance(appbarHeight);
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

    public void setFavoritesList(List<FavoriteEntity> favoriteEntityList) {
        list.addAll(favoriteEntityList);
        if (feedFragment != null) feedFragment.get().setFavoritesList(list);
        if (hotFragment != null) hotFragment.get().setFavoritesList(list);
        if (categoriesFragment != null) categoriesFragment.get().setFavoritesList(list);
    }
}
