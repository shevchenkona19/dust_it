package dustit.clientapp.mvp.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.FavoritesUpperEntity;
import dustit.clientapp.mvp.presenters.activities.FavoritesActivityPresenter;
import dustit.clientapp.mvp.ui.adapters.FavoritesRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.interfaces.IFavoriteActivityView;

public class FavoritesActivity extends AppCompatActivity implements IFavoriteActivityView, FavoritesRecyclerViewAdapter.IFavoritesCallback {
    @BindView(R.id.tbFavoritesToolbar)
    Toolbar toolbar;
    @BindView(R.id.rvFavoritesList)
    RecyclerView rvFavorites;
    @BindView(R.id.pbFavoritesLoading)
    ProgressBar pbLoading;
    @BindView(R.id.btnFavoritesFailedToLoad)
    Button btnReload;
    @BindView(R.id.tvFavoritesFailedToLoad)
    TextView tvError;
    @BindView(R.id.tvFavoritesEmpty)
    TextView tvEmptyText;
    @BindView(R.id.ivFavoritesEmpty)
    ImageView ivEmptyPic;


    private FavoritesRecyclerViewAdapter mAdapter;
    private final FavoritesActivityPresenter mPresenter = new FavoritesActivityPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        ButterKnife.bind(this);
        mPresenter.bind(this);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorMainText));
        setSupportActionBar(toolbar);
        mAdapter = new FavoritesRecyclerViewAdapter(this,this, mPresenter.getToken());
        rvFavorites.setAdapter(mAdapter);
        rvFavorites.setLayoutManager(new GridLayoutManager(this, 3));
        mPresenter.loadFavorites();
        pbLoading.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.loadFavorites();
                hideError();
            }
        });
    }

    @Override
    public void onFavoritesArrived(List<FavoritesUpperEntity.FavoriteEntity> list) {
        pbLoading.setVisibility(View.GONE);
        rvFavorites.setVisibility(View.VISIBLE);
        mAdapter.updateAll(list);
    }

    @Override
    public void onFailedToLoadFavorites() {
        showError();
    }

    @Override
    public void removedFromFavorites(String id) {
        mAdapter.removeById(id);
    }

    @Override
    public void onFailedToRemoveFromFavorites(String id) {
        Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEmpty() {
        pbLoading.setVisibility(View.GONE);
        tvEmptyText.setVisibility(View.VISIBLE);
        ivEmptyPic.setVisibility(View.VISIBLE);
    }

    private void showError() {
        rvFavorites.setVisibility(View.GONE);
        pbLoading.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        btnReload.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
        btnReload.setVisibility(View.GONE);
        rvFavorites.setVisibility(View.GONE);
        pbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        mPresenter.unbind();
        super.onDestroy();
    }

    @Override
    public void onFavoriteChosen(String id) {
        startActivity(new Intent(this, FavoriteViewActivity.class).putExtra(FavoriteViewActivity.ID_KEY, id));
    }

    @Override
    public void onFavoriteSelected(String id) {

    }
}
