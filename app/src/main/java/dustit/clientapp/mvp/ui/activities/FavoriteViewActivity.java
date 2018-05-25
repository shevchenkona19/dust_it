package dustit.clientapp.mvp.ui.activities;

import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import dustit.clientapp.utils.managers.ThemeManager;
import me.relex.photodraweeview.PhotoDraweeView;

public class FavoriteViewActivity extends AppCompatActivity implements IFavoriteViewActivityView {
    public static final String ID_KEY = "idket";

    @BindView(R.id.tivFavoriteViewImage)
    PhotoDraweeView tivImage;
    @BindView(R.id.ivFavoriteBack)
    ImageView ivBack;
    @BindView(R.id.ivFavoriteViewDeleteFromFavorite)
    ImageView ivDelete;
    @BindView(R.id.ivFavoriteViewShare)
    ImageView ivShare;
    @BindView(R.id.ivFavoriteViewDownload)
    ImageView ivDownload;

    @Inject
    ThemeManager themeManager;

    private final FavoriteViewActivityPresenter mPresenter = new FavoriteViewActivityPresenter();
    private String mFavoriteId;
    private String imageUrl;



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
        ivDelete.setOnClickListener(view -> mPresenter.removeFromFavorites(mFavoriteId));
        ivDownload.setOnClickListener(view -> mPresenter.downloadImage(mFavoriteId));
        ivBack.setOnClickListener(view -> finish());
        ivShare.setOnClickListener(view -> ImageShareUtils.shareImage(imageUrl, FavoriteViewActivity.this));
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
        finish();
    }

    @Override
    public void onErrorRemovingFromFavorites() {
        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloaded(String pathToImage) {
        /*sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                Uri.parse("file://" + Environment.getExternalStorageDirectory())));*/
        Toast.makeText(this, getString(R.string.downloaded_to) + pathToImage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDownloadFailed() {
        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG).show();
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
}
