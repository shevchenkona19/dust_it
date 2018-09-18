package dustit.clientapp.mvp.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.presenters.activities.FavoriteViewActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IFavoriteViewActivityView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.ImageShareUtils;
import dustit.clientapp.utils.bus.FavouritesBus;
import dustit.clientapp.utils.managers.ThemeManager;
import me.relex.photodraweeview.PhotoDraweeView;

public class FavoriteViewActivity extends AppCompatActivity implements IFavoriteViewActivityView {
    public static final String ID_KEY = "idket";
    private static final int PERMISSION_DIALOG = 1010;

    @BindView(R.id.tivFavoriteViewImage)
    PhotoDraweeView tivImage;
    @BindView(R.id.ivFavoriteViewDeleteFromFavorite)
    ImageView ivDelete;
    @BindView(R.id.ivFavoriteViewShare)
    ImageView ivShare;
    @BindView(R.id.ivFavoriteViewDownload)
    ImageView ivDownload;
    @BindView(R.id.toolbar2)
    Toolbar toolbar;

    @Inject
    ThemeManager themeManager;

    private final FavoriteViewActivityPresenter mPresenter = new FavoriteViewActivityPresenter();
    private String mFavoriteId;
    private String imageUrl;

    private boolean isAdded = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get().getAppComponent().inject(this);
        setContentView(R.layout.activity_favorite_view);
        ButterKnife.bind(this);
        mPresenter.bind(this);
        mFavoriteId = getIntent().getStringExtra(FavoriteViewActivity.ID_KEY);
        imageUrl = IConstants.BASE_URL + "/feed/imgs?id=" + mFavoriteId;
        final DraweeController ctrl = Fresco.newDraweeControllerBuilder().setUri(imageUrl)
                .setTapToRetryEnabled(true)
                .setOldController(tivImage.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        if (imageInfo == null || mFavoriteId == null) {
                            return;
                        }
                        tivImage.update(imageInfo.getWidth(), imageInfo.getHeight());
                    }
                })
                .build();
        final GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setProgressBarImage(new ProgressBarDrawable())
                .build();
        tivImage.setController(ctrl);
        tivImage.setHierarchy(hierarchy);
        initClicks();
        initSlidr();
    }

    private void initClicks() {
        ivDelete.setOnClickListener(view -> setIsAdded(mFavoriteId));
        ivDownload.setOnClickListener(view -> mPresenter.downloadImage(mFavoriteId));
        toolbar.setNavigationOnClickListener(view -> finish());
        ivShare.setOnClickListener(view -> ImageShareUtils.shareImage(imageUrl, FavoriteViewActivity.this));
    }

    private void setIsAdded(String mFavoriteId) {
        if (isAdded) {
            mPresenter.removeFromFavorites(mFavoriteId);
        } else {
            mPresenter.addToFavourites(mFavoriteId);
        }
    }

    private void initSlidr() {
        final SlidrConfig config = new SlidrConfig.Builder()
                .primaryColor(getResources().getColor(R.color.colorPrimaryDefault))
                .secondaryColor(getResources().getColor(R.color.colorPrimaryDarkDefault))
                .position(SlidrPosition.VERTICAL)
                .sensitivity(0.5f)
                .scrimColor(Color.BLACK)
                .scrimStartAlpha(0.6f)
                .scrimEndAlpha(0f)
                .velocityThreshold(2400)
                .distanceThreshold(0.25f)
                .edge(true)
                .edgeSize(0.18f)
                .build();
        Slidr.attach(this, config);
    }

    @Override
    public void onRemovedFromFavorites() {
        Toast.makeText(this, getString(R.string.deleted_from_favorites), Toast.LENGTH_SHORT).show();
        ivDelete.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_to_favourites));
        isAdded = false;
        FavouritesBus.getInstance().removed(mFavoriteId);
    }

    @Override
    public void onErrorRemovingFromFavorites() {
        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloaded(String pathToImage) {
        Toast.makeText(this, getString(R.string.downloaded_to) + pathToImage, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean checkPermission() {
        int permCheckRead = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int permCheckWrite = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permCheckRead == PackageManager.PERMISSION_GRANTED
                || permCheckWrite == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void getPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_DIALOG);
    }

    @Override
    public void onDownloadFailed() {
        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError() {
        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAddedToFavourites() {
        ivDelete.setImageDrawable(getResources().getDrawable(R.drawable.ic_saved));
        Toast.makeText(this, getString(R.string.added_to_favourites), Toast.LENGTH_SHORT).show();
        isAdded = true;
        FavouritesBus.getInstance().added(mFavoriteId);
    }

    @Override
    protected void onDestroy() {
        mPresenter.unbind();
        super.onDestroy();
    }

    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(this);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_DIALOG: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        mPresenter.downloadImage(mFavoriteId);
                    }
                } else {
                    onError();
                }
            }
        }
    }
}
