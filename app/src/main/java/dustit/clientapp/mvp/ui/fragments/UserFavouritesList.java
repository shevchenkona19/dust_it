package dustit.clientapp.mvp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.NewAchievementEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;
import dustit.clientapp.mvp.presenters.fragments.UserFavouritesListFragmentPresenter;
import dustit.clientapp.mvp.ui.adapters.FavoritesRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.dialog.AchievementUnlockedDialog;
import dustit.clientapp.mvp.ui.interfaces.IUserFavouritesListFragmentView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;

public class UserFavouritesList extends Fragment implements IUserFavouritesListFragmentView, FavoritesRecyclerViewAdapter.IFavoritesCallback {

    @BindView(R.id.rvUserFavorites)
    RecyclerView rvPhotos;
    @BindView(R.id.tvEmptyList)
    TextView tvEmpty;
    @BindView(R.id.pbLoading)
    ProgressBar pbLoading;

    private Unbinder unbinder;
    private UserFavouritesListFragmentPresenter presenter = new UserFavouritesListFragmentPresenter();
    private FavoritesRecyclerViewAdapter adapter;
    private int userId;

    private boolean isMe;

    private OnFragmentInteractionListener mListener;

    public UserFavouritesList() {
        // Required empty public constructor
    }

    public static UserFavouritesList newInstance(int userId, boolean isMe) {
        UserFavouritesList fragment = new UserFavouritesList();
        Bundle args = new Bundle();
        args.putInt(IConstants.IBundle.USER_ID, userId);
        args.putBoolean(IConstants.IBundle.IS_ME, isMe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args != null) {
            userId = args.getInt(IConstants.IBundle.USER_ID);
            isMe = args.getBoolean(IConstants.IBundle.IS_ME);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) setArguments(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_favourite_list, container, false);
        unbinder = ButterKnife.bind(this, v);
        presenter.bind(this);
        adapter = new FavoritesRecyclerViewAdapter(getContext(), this);
        rvPhotos.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter.setHasStableIds(true);
        adapter.setIsMe(isMe);
        rvPhotos.setAdapter(adapter);
        rvPhotos.setHasFixedSize(true);
        presenter.loadFavourites(userId);
        return v;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        presenter.unbind();
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
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

    private void finishLoading() {
        rvPhotos.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        pbLoading.setVisibility(View.GONE);
    }

    private void showEmpty() {
        rvPhotos.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
    }

    @Override
    public void onNotRegistered() {
        if (getContext() != null)
            AlertBuilder.showNotRegisteredPrompt(getContext());
    }

    private void showError(String error) {
        Toast.makeText(getContext(), error.length() > 0 ? error : getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavouritesLoaded(List<MemEntity> memEntities) {
        if (memEntities.size() == 0) {
            showEmpty();
            return;
        }
        finishLoading();
        adapter.updateAll(memEntities);
    }

    @Override
    public void onFavouritesFailed() {
        showError("");
    }

    @Override
    public void onChangedFeedback(RefreshedMem refreshedMem) {
        adapter.refreshListWithMem(refreshedMem);
    }

    @Override
    public void restoreMem(RestoreMemEntity restoreMemEntity) {
        adapter.restoreMem(restoreMemEntity);
    }

    @Override
    public void onAchievementUpdate(NewAchievementEntity achievementEntity) {
        if (getContext() != null)
            new AchievementUnlockedDialog(getContext(), achievementEntity.isFinalLevel()).bind(achievementEntity).show();
    }

    @Override
    public void onFavoriteChosen(MemEntity mem) {
        mListener.onFavoriteSelected(mem);
    }

    @Override
    public void reload() {
        presenter.loadFavourites(userId);
    }

    public interface OnFragmentInteractionListener {
        void onFavoriteSelected(MemEntity memEntity);
    }
}
