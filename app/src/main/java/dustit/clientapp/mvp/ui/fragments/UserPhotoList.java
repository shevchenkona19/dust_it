package dustit.clientapp.mvp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dustit.clientapp.R;
import dustit.clientapp.customviews.WrapperLinearLayoutManager;
import dustit.clientapp.mvp.model.entities.NewAchievementEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;
import dustit.clientapp.mvp.model.entities.UploadEntity;
import dustit.clientapp.mvp.presenters.fragments.UserPhotoListFragmentPresenter;
import dustit.clientapp.mvp.ui.adapters.UploadsAdapter;
import dustit.clientapp.mvp.ui.dialog.AchievementUnlockedDialog;
import dustit.clientapp.mvp.ui.dialog.ReportMemeDialog;
import dustit.clientapp.mvp.ui.interfaces.IBaseFeedFragment;
import dustit.clientapp.mvp.ui.interfaces.IUserPhotoListFragmentView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.ImageShareUtils;
import dustit.clientapp.utils.L;

public class UserPhotoList extends Fragment implements IUserPhotoListFragmentView, UploadsAdapter.IUploadInteraction, IBaseFeedFragment {

    @BindView(R.id.rvUserPhotos)
    RecyclerView rvPhotos;
    @BindView(R.id.tvEmptyList)
    TextView tvEmpty;
    @BindView(R.id.pbLoading)
    ProgressBar pbLoading;

    private Unbinder unbinder;
    private UserPhotoListFragmentPresenter presenter = new UserPhotoListFragmentPresenter();
    private UploadsAdapter adapter;

    private OnFragmentInteractionListener mListener;
    private IConstants.ViewMode viewMode;

    public UserPhotoList() {
        // Required empty public constructor
    }

    public static UserPhotoList newInstance(int userId, IConstants.ViewMode viewMode) {
        UserPhotoList fragment = new UserPhotoList();
        Bundle args = new Bundle();
        args.putInt(IConstants.IBundle.USER_ID, userId);
        args.putString(IConstants.IBundle.VIEW_MODE, viewMode.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) setArguments(savedInstanceState);
        if (getArguments() != null) {
            presenter.setUserId(getArguments().getInt(IConstants.IBundle.USER_ID, 0));
            viewMode = IConstants.ViewMode.valueOf(getArguments().getString(IConstants.IBundle.VIEW_MODE));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_photo_list, container, false);
        unbinder = ButterKnife.bind(this, v);
        presenter.bind(this);
        adapter = new UploadsAdapter(getContext(), this, rvPhotos);
        adapter.setHasStableIds(true);
        adapter.changeViewMode(viewMode);
        switch (viewMode) {
            case LIST:
                rvPhotos.setLayoutManager(new WrapperLinearLayoutManager(getContext()));
                break;
            case GRID:
                rvPhotos.setLayoutManager(new GridLayoutManager(getContext(), 3));
                break;
        }
        rvPhotos.setAdapter(adapter);
        rvPhotos.setHasFixedSize(true);
        presenter.bindToView(this);
        presenter.loadBaseUploads();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        pbLoading.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        presenter.unbind();
        super.onDestroyView();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onUploadsBaseArrived(List<UploadEntity> list) {
        if (list.size() == 0) {
            showEmpty();
            return;
        }
        finishLoading();
        adapter.updateWhole(list);
    }

    private void showEmpty() {
        L.print("set gone showEmpty");
        pbLoading.setVisibility(View.GONE);
        rvPhotos.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUploadsPartialArrived(List<UploadEntity> list) {
        if (list.size() == 0) {
            adapter.onMemesEnded();
            return;
        }
        adapter.updateAtEnding(list);
    }

    @Override
    public void onUploadsFailed() {
        finishLoading();
        showMessage(getString(R.string.error));
        adapter.onFailedToLoad();
    }

    @Override
    public void startLoading() {
        rvPhotos.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        L.print("set visible startLoading");
        pbLoading.setVisibility(View.VISIBLE);
    }

    private void finishLoading() {
        rvPhotos.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        L.print("set gone finishLoading");
        pbLoading.setVisibility(View.GONE);
    }

    @Override
    public void reloadFeedBase() {
        presenter.loadBaseUploads();
    }

    @Override
    public void onUploadSelected(View animStart, UploadEntity upload) {
        mListener.onUploadSelected(animStart, upload);
    }

    @Override
    public boolean isRegistered() {
        return presenter.isRegistered();
    }

    @Override
    public void onNotRegistered() {
        if (getContext() != null)
            AlertBuilder.showNotRegisteredPrompt(getContext());
    }

    @Override
    public void postLike(UploadEntity upload) {
        presenter.postLike(upload);
    }

    @Override
    public void deleteLike(UploadEntity upload) {
        presenter.deleteLike(upload);
    }

    @Override
    public void postDislike(UploadEntity upload) {
        presenter.postDislike(upload);
    }

    @Override
    public void deleteDislike(UploadEntity upload) {
        presenter.deleteDislike(upload);
    }

    @Override
    public void onCommentsSelected(View animStart, UploadEntity upload) {
        mListener.onCommentsSelected(animStart, upload);
    }

    @Override
    public void loadMore(int offset) {
        presenter.loadMore(offset);
    }

    @Override
    public void addToFavourites(UploadEntity uploadEntity) {
        presenter.addToFavourites(uploadEntity);
    }

    @Override
    public void removeFromFavourites(UploadEntity uploadEntity) {
        presenter.removeFromFavourites(uploadEntity);
    }

    @Override
    public void shareMem(UploadEntity upload) {
        ImageShareUtils.shareImage(IConstants.BASE_URL + "/feed/imgs?id=" + upload.getImageId(), getContext());
    }

    @Override
    public void reportMeme(UploadEntity upload) {
        if (getContext() == null) return;
        new ReportMemeDialog(getContext(), upload.toMemEntity());
    }

    private void showMessage(String error) {
        mListener.showSnackbar(error);
    }

    @Override
    public void onChangedMemFeedback(RefreshedMem refreshedMem) {
        L.print("Refresh mem");
        adapter.refreshMem(refreshedMem);
    }

    @Override
    public void onErrorInFeedback(RestoreMemEntity restoreMemEntity) {
        adapter.restoreMem(restoreMemEntity);
    }

    @Override
    public void onAchievementUpdate(NewAchievementEntity achievementEntity) {
        if (getContext() != null) {
            if (presenter.isRegistered())
                new AchievementUnlockedDialog(getContext(), achievementEntity.isFinalLevel()).bind(achievementEntity).show();
        }
    }

    public void refreshUploads() {
        if (presenter != null) {
            presenter.loadBaseUploads();
        }
    }

    public interface OnFragmentInteractionListener {
        void onUploadSelected(View animStart, UploadEntity upload);

        void onCommentsSelected(View animStart, UploadEntity upload);

        void showSnackbar(String message);
    }
}
