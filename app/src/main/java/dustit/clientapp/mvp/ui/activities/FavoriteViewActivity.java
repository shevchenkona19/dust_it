package dustit.clientapp.mvp.ui.activities;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.customviews.TouchImageView;
import dustit.clientapp.mvp.presenters.activities.FavoriteViewActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IFavoriteViewActivityView;
import dustit.clientapp.utils.IConstants;

public class FavoriteViewActivity extends AppCompatActivity implements IFavoriteViewActivityView {
    public static final String ID_KEY = "idket";

    @BindView(R.id.tivFavoriteViewImage)
    TouchImageView tivImage;
    @BindView(R.id.ivFavoriteBack)
    ImageView ivBack;
    @BindView(R.id.ivFavoriteViewDeleteFromFavorite)
    ImageView ivDelete;
    @BindView(R.id.ivFavoriteViewShare)
    ImageView ivShare;
    @BindView(R.id.ivFavoriteViewDownload)
    ImageView ivDownload;

    private final FavoriteViewActivityPresenter mPresenter = new FavoriteViewActivityPresenter();
    private String mFavoriteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_view);
        ButterKnife.bind(this);
        mPresenter.bind(this);
        mFavoriteId = getIntent().getStringExtra(FavoriteViewActivity.ID_KEY);
        Picasso.with(this)
                .load(Uri.parse(IConstants.BASE_URL + "/client/getFavorite?token=" + mPresenter.getToken() + "&id=" + mFavoriteId))
                .noFade()
                .noPlaceholder()
                .into(tivImage);
        initClicks();
        initSlidr();
    }

    private void initClicks() {
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.removeFromFavorites(mFavoriteId);
            }
        });
        ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.downloadImage(mFavoriteId);
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FavoriteViewActivity.this, "NE RABOTAET", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initSlidr() {
        SlidrConfig config = new SlidrConfig.Builder()
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
}
