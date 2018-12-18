package dustit.clientapp.mvp.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.Achievement;
import dustit.clientapp.mvp.model.entities.AchievementsEntity;
import dustit.clientapp.mvp.model.entities.FavoriteEntity;
import dustit.clientapp.mvp.presenters.activities.NewAccountActivityPresenter;
import dustit.clientapp.mvp.ui.adapters.AchievementAdapter;
import dustit.clientapp.mvp.ui.adapters.FavoritesRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.dialog.ViewAchievement;
import dustit.clientapp.mvp.ui.interfaces.INewAccountActivityView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.L;
import dustit.clientapp.utils.bus.FavouritesBus;

public class NewAccountActivity extends AppCompatActivity implements INewAccountActivityView, FavoritesRecyclerViewAdapter.IFavoritesCallback {

    private static final int PICK_IMAGE = 222;
    private static final int CROPPED_IMAGE = 223;
    private static final int PERMISSION_DIALOG = 1010;

    @BindView(R.id.tbAccountSettingsToolbar)
    Toolbar tbAccount;
    @BindView(R.id.sdvAccountUserIcon)
    SimpleDraweeView sdvIcon;
    @BindView(R.id.tvAccountUsername)
    TextView tvUsername;
    @BindView(R.id.cpbAccountLoadingPhoto)
    CircularProgressView cpbPhotoLoading;
    @BindView(R.id.clAccountLoadingLayout)
    ConstraintLayout clAccountLoading;
    @BindView(R.id.pbAccountLoading)
    ProgressBar pbLoading;
    @BindView(R.id.tvAccountFailedToLoad)
    TextView tvFailedToLoad;
    @BindView(R.id.btnAccountReload)
    Button btnReload;
    @BindView(R.id.supFavoritesPanel)
    SlidingUpPanelLayout supLayout;
    @BindView(R.id.rvFavoritesList)
    RecyclerView rvFavorites;
    @BindView(R.id.pbFavoritesLoading)
    ProgressBar pbFavsLoading;
    @BindView(R.id.btnFavoritesFailedToLoad)
    Button btnFavsReload;
    @BindView(R.id.tvFavoritesFailedToLoad)
    TextView tvError;
    @BindView(R.id.tvFavoritesEmpty)
    TextView tvEmptyText;
    @BindView(R.id.clFavoritesParent)
    ConstraintLayout clParent;
    @BindView(R.id.btnAccountRegister)
    Button btnRegister;
    @BindView(R.id.svAccountView)
    ScrollView svAccountView;
    @BindView(R.id.achievementsList)
    RecyclerView rvAchievements;
    @BindView(R.id.cvAccountFavoritesCard)
    CardView cvAccountFavoritesCard;

    @Inject
    UserSettingsDataManager userSettingsDataManager;

    private FavoritesRecyclerViewAdapter mAdapter;
    private AchievementAdapter achievementAdapter;
    private final NewAccountActivityPresenter mPresenter = new NewAccountActivityPresenter();
    private SlidingUpPanelLayout.PanelState prevPanelState = SlidingUpPanelLayout.PanelState.COLLAPSED;

    private String myUsername = "";

    private String userId = "";
    private boolean isMe = false;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IConstants.IBundle.IS_ME, isMe);
        outState.putString(IConstants.IBundle.ID, userId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            final Transition fade = new android.transition.Fade();
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);
            final Window window = getWindow();
            window.setEnterTransition(fade);
            window.setReturnTransition(fade);
            window.setExitTransition(fade);
            final TransitionSet transitionSet = DraweeTransition
                    .createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP,
                            ScalingUtils.ScaleType.CENTER_CROP);
            window.setSharedElementEnterTransition(transitionSet);
            window.setSharedElementExitTransition(transitionSet);
        }
        getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                if (isMe) {
                    if (userSettingsDataManager.isRegistered()) {
                        mPresenter.loadFavorites(userId);
                        mPresenter.getAchievements(userId);
                    }
                } else {
                    mPresenter.loadFavorites(userId);
                    mPresenter.getAchievements(userId);
                }
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        super.onCreate(savedInstanceState);
        App.get().getAppComponent().inject(this);
        setContentView(R.layout.account_new);
        ButterKnife.bind(this);
        sdvIcon.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mPresenter.bind(this);
        setSupportActionBar(tbAccount);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) bundle = savedInstanceState;
        if (bundle != null) {
            if (!bundle.getBoolean(IConstants.IBundle.IS_ME)) {
                userId = bundle.getString(IConstants.IBundle.ID);
            } else {
                userId = mPresenter.loadMyId();
                isMe = true;
            }
        }
        btnReload.setOnClickListener(view -> {
            tvFailedToLoad.setVisibility(View.GONE);
            btnReload.setVisibility(View.GONE);
            pbLoading.setVisibility(View.VISIBLE);
            mPresenter.getUsername(userId);
            mPresenter.loadFavorites(userId);
        });
        rvAchievements.setHasFixedSize(true);
        rvFavorites.setHasFixedSize(true);
        sdvIcon.setLegacyVisibilityHandlingEnabled(true);
        if (isMe) {
            if (userSettingsDataManager.isRegistered()) {
                sdvIcon.setOnClickListener(view -> {
                    final AlertDialog dialog = new AlertDialog.Builder(NewAccountActivity.this)
                            .setTitle(getString(R.string.change_profile_pic_title))
                            .setMessage(getString(R.string.change_profile_pic_question))
                            .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                                final int permCheckRead = ContextCompat.checkSelfPermission(NewAccountActivity.this,
                                        Manifest.permission.READ_EXTERNAL_STORAGE);
                                final int permCheckWrite = ContextCompat.checkSelfPermission(NewAccountActivity.this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                if (permCheckRead != PackageManager.PERMISSION_GRANTED
                                        && permCheckWrite != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(NewAccountActivity.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            PERMISSION_DIALOG);
                                } else {
                                    final Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                    getIntent.setType("image/*");
                                    final Intent pickIntent = new Intent(Intent.ACTION_PICK);
                                    pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                    final Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.choose_photo));
                                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
                                    startActivityForResult(chooserIntent, PICK_IMAGE);
                                }
                            })
                            .setNegativeButton(getString(R.string.no), null)
                            .create();
                    dialog.setOnShowListener(dialogInterface -> {
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));
                    });
                    dialog.show();
                });
            } else {
                sdvIcon.setOnClickListener(view -> onNotRegistered());
                rvAchievements.setVisibility(View.GONE);
            }
        }
        tbAccount.setNavigationOnClickListener(v -> unRevealActivity());
        mAdapter = new FavoritesRecyclerViewAdapter(this, this);
        rvFavorites.setAdapter(mAdapter);
        rvFavorites.setLayoutManager(new GridLayoutManager(this, 2));
        achievementAdapter = new AchievementAdapter(this, isMe);
        rvAchievements.setAdapter(achievementAdapter);
        rvAchievements.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));
        pbFavsLoading.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        btnFavsReload.setOnClickListener(view -> {
            mPresenter.loadFavorites(userId);
            hideError();
        });
        if (isMe) {
            if (!userSettingsDataManager.isRegistered()) {
                Uri uri = Uri.parse("android.resource://" + this.getPackageName() + "/drawable/noimage");
                sdvIcon.setImageURI(uri);
                supLayout.setPanelHeight(0);
                clAccountLoading.setVisibility(View.GONE);
                svAccountView.setVisibility(View.VISIBLE);
                btnRegister.setVisibility(View.VISIBLE);
                btnRegister.setOnClickListener((view -> AlertBuilder.showRegisterPrompt(this)));
            }
        }
        if (isMe) {
            if (userSettingsDataManager.isRegistered()) {
                mPresenter.getUsername(userId);
            }
        } else {
            mPresenter.getUsername(userId);
        }
        supLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                float dps = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12 * (1 - slideOffset),
                        getResources().getDisplayMetrics());
                if (dps > 0 && dps < 1) return;
                cvAccountFavoritesCard.setRadius(dps);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                switch (newState) {
                    case EXPANDED:
                        cvAccountFavoritesCard.setRadius(0);
                        prevPanelState = SlidingUpPanelLayout.PanelState.EXPANDED;
                        break;
                    case COLLAPSED:
                        float twelveDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                                getResources().getDisplayMetrics());
                        cvAccountFavoritesCard.setRadius(twelveDp);
                        prevPanelState = SlidingUpPanelLayout.PanelState.COLLAPSED;
                        break;
                    case ANCHORED:
                        switch (prevPanelState) {
                            case EXPANDED:
                                supLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                                break;
                            case COLLAPSED:
                                supLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                                break;
                            case DRAGGING:
                                supLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                        }
                }
            }
        });
        if (isMe) {
            FavouritesBus.getInstance().setMainConsumer(new FavouritesBus.IConsumer() {
                @Override
                public void consumeRemoved(String id) {
                    mPresenter.loadFavorites(userId);
                }

                @Override
                public void consumeAdded(String id) {
                    mPresenter.loadFavorites(userId);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                int flags = 0;
                window.getDecorView().setSystemUiVisibility(flags);
            } else {
                int flags = window.getDecorView().getSystemUiVisibility();
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.getDecorView().setSystemUiVisibility(flags);
                window.setStatusBarColor(Color.WHITE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isMe)
            getMenuInflater().inflate(R.menu.profile_menu, menu);
        return isMe;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toSettings:
                final Intent i = new Intent(this, SettingsActivity.class);
                i.putExtras(getIntent().getExtras() != null ? getIntent().getExtras() : new Bundle());
                startActivity(i);
                return true;
            case R.id.toEditAccount:
                final Intent intent = new Intent(this, PersonalSettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.toWriteUs:
                Intent intent2 = new Intent(this, UserFeedbackActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void unRevealActivity() {
        supportFinishAfterTransition();
    }

    @Override
    public void onBackPressed() {
        if (supLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            supLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return;
        }
        unRevealActivity();
    }

    @Override
    protected void onDestroy() {
        sdvIcon.setLayerType(View.LAYER_TYPE_NONE, null);
        mPresenter.unbind();
        FavouritesBus.destroy();
        mPresenter.unbind();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_DIALOG: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        final Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        getIntent.setType("image/*");
                        final Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickIntent.setType("image/*");
                        final Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.choose_photo));
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
                        startActivityForResult(chooserIntent, PICK_IMAGE);
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                return;
            }
            final Uri imageSource = data.getData();
            try {
                final File image = createImageFile();
                final Uri destinationUri = Uri.fromFile(image);
                UCrop.Options options = new UCrop.Options();
                options.setToolbarColor(getResources().getColor(R.color.colorPrimaryDefault));
                options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkDefault));
                options.setActiveWidgetColor(getResources().getColor(R.color.colorPrimaryDefault));
                options.setToolbarTitle(getString(R.string.crop_photo));
                if (imageSource != null) {
                    UCrop.of(imageSource, destinationUri)
                            .withOptions(options)
                            .start(this, CROPPED_IMAGE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CROPPED_IMAGE) {
            if (resultCode == UCrop.RESULT_ERROR || data == null) {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                try {
                    throw UCrop.getError(data);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                return;
            }
            Uri croppedImage;
            try {
                croppedImage = UCrop.getOutput(data);
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                return;
            }
            if (croppedImage != null) {
                mPresenter.uploadImage(croppedImage.getPath());
                sdvIcon.setVisibility(View.GONE);
                cpbPhotoLoading.setVisibility(View.VISIBLE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final String imageFileName = "JPEG_" + timeStamp + "_";
        final File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    @Override
    public void updateUploadingProgress(int percents) {
        cpbPhotoLoading.setProgress(percents);
    }

    @Override
    public void onUploadFinished() {
        cpbPhotoLoading.setProgress(100);
        cpbPhotoLoading.setVisibility(View.GONE);
        sdvIcon.setVisibility(View.VISIBLE);
        Fresco.getImagePipeline().clearCaches();
        sdvIcon.setImageURI(IConstants.BASE_URL + "/feed/userPhoto?targetUsername=" + myUsername);
    }

    @Override
    public void onUploadFailed() {
        cpbPhotoLoading.setVisibility(View.GONE);
        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        sdvIcon.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUsernameArrived(String username) {
        myUsername = username;
        if (isMe) {
            tvUsername.setText(myUsername);
        } else {
            tvUsername.setVisibility(View.INVISIBLE);
            tbAccount.setTitle(myUsername);
        }
        clAccountLoading.setVisibility(View.GONE);
        svAccountView.setVisibility(View.VISIBLE);
        sdvIcon.setImageURI(Uri.parse(IConstants.BASE_URL + "/feed/userPhoto?targetUsername=" + myUsername));
    }

    @Override
    public void onUsernameFailedToLoad() {
        svAccountView.setVisibility(View.GONE);
        clAccountLoading.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
        tvFailedToLoad.setVisibility(View.VISIBLE);
        btnReload.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFavoritesArrived(List<FavoriteEntity> list) {
        pbFavsLoading.setVisibility(View.GONE);
        rvFavorites.setVisibility(View.VISIBLE);
        tvEmptyText.setVisibility(View.GONE);
        mAdapter.updateAll(list);
        rvFavorites.scheduleLayoutAnimation();
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
        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAchievementsLoaded(AchievementsEntity achievementsEntity) {
        List<Achievement> items = new ArrayList<>();
        items.add(achievementsEntity.getLikes());
        items.add(achievementsEntity.getDislikes());
        items.add(achievementsEntity.getComments());
        items.add(achievementsEntity.getViews());
        items.add(achievementsEntity.getFavourites());
        achievementAdapter.update(achievementsEntity.isFirstHundred(), achievementsEntity.isFirstThousand(), items);
        rvAchievements.scheduleLayoutAnimation();
    }

    @Override
    public void onFailedToLoadAchievements() {
        L.print("failed to load achievements");
    }

    @Override
    public void showEmpty() {
        rvFavorites.setVisibility(View.GONE);
        pbFavsLoading.setVisibility(View.GONE);
        tvEmptyText.setVisibility(View.VISIBLE);
    }

    private void showError() {
        rvFavorites.setVisibility(View.GONE);
        pbFavsLoading.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        btnFavsReload.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
        btnFavsReload.setVisibility(View.GONE);
        rvFavorites.setVisibility(View.GONE);
        pbFavsLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFavoriteChosen(String id) {
        if (supLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
            startActivity(new Intent(this, FavoriteViewActivity.class).putExtra(FavoriteViewActivity.ID_KEY, id).putExtra(IConstants.IBundle.IS_ME, isMe));
        else
            supLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(this);
    }
}
