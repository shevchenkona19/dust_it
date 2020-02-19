package dustit.clientapp.mvp.ui.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.lang.ref.WeakReference;

import dustit.clientapp.mvp.ui.fragments.ReferralFragment;
import dustit.clientapp.mvp.ui.fragments.UserFavouritesList;
import dustit.clientapp.mvp.ui.fragments.UserPhotoList;
import dustit.clientapp.utils.IConstants;

public class AccountViewPagerAdapter extends FragmentPagerAdapter {

    private int userId;
    private boolean isMe;

    private WeakReference<UserPhotoList> uploadsList;
    private WeakReference<UserPhotoList> uploadsGrid;
    private WeakReference<UserFavouritesList> favouritesList;
    private WeakReference<ReferralFragment> referralFragment;

    public AccountViewPagerAdapter(FragmentManager fm, int userId, boolean isMe) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.userId = userId;
        this.isMe = isMe;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            default:
            case 0:
                UserPhotoList frag = UserPhotoList.newInstance(userId, IConstants.ViewMode.LIST);
                uploadsList = new WeakReference<>(frag);
                return frag;
            case 1:
                UserPhotoList frag1 = UserPhotoList.newInstance(userId, IConstants.ViewMode.GRID);
                uploadsGrid = new WeakReference<>(frag1);
                return frag1;
            case 2:
                UserFavouritesList favouritesListFrag = UserFavouritesList.newInstance(userId, isMe);
                favouritesList = new WeakReference<>(favouritesListFrag);
                return favouritesListFrag;
            case 3:
                ReferralFragment fragmentReferral = ReferralFragment.newInstance();
                referralFragment = new WeakReference<>(fragmentReferral);
                return fragmentReferral;
        }
    }

    @Override
    public int getCount() {
        if (isMe) return 4;
        else return 3;
    }

    public void refreshUploads() {
        if (uploadsList != null) {
            uploadsList.get().refreshUploads();
        }
        if (uploadsGrid != null) {
            uploadsGrid.get().refreshUploads();
        }
    }
}
