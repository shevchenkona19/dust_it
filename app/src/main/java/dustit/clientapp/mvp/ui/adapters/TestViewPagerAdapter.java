package dustit.clientapp.mvp.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import dustit.clientapp.mvp.model.entities.TestMemEntity;
import dustit.clientapp.mvp.ui.fragments.MemTestFragment;

/**
 * Created by shevc on 30.09.2017.
 * Let's GO!
 */

public class TestViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<TestMemEntity> list;
    private String token;

    public TestViewPagerAdapter(FragmentManager fm, String token) {
        super(fm);
        list = new ArrayList<>();
        this.token = token;
    }

    public void updateList(List<TestMemEntity> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return MemTestFragment.newInstance(position , list.get(position), token);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
