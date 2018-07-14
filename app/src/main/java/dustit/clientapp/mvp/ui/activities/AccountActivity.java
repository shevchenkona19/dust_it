package dustit.clientapp.mvp.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wooplr.spotlight.SpotlightView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.presenters.activities.AccountActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IAccountActivityView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.bus.FavouritesBus;
import dustit.clientapp.utils.managers.ThemeManager;


public class AccountActivity extends AppCompatActivity implements IAccountActivityView {
    private static final int PICK_IMAGE = 222;
    private static final int CROPPED_IMAGE = 223;
    private static final int PERMISSION_DIALOG = 1010;
    @BindView(R.id.tbAccountSettingsToolbar)
    Toolbar tbAccount;
    @BindView(R.id.sdvAccountUserIcon)
    SimpleDraweeView sdvIcon;
    @BindView(R.id.tvAccountUsername)
    TextView tvUsername;
    @BindView(R.id.btnAccountEdit)
    Button btnEdit;
    @BindView(R.id.btnAccountSettings)
    Button btnSettings;
    @BindView(R.id.cpbAccountLoadingPhoto)
    CircularProgressView cpbPhotoLoading;
    @BindView(R.id.clAccountLayout)
    ConstraintLayout clAccount;
    @BindView(R.id.clAccountLoadingLayout)
    ConstraintLayout clAccountLoading;
    @BindView(R.id.pbAccountLoading)
    ProgressBar pbLoading;
    @BindView(R.id.tvAccountFailedToLoad)
    TextView tvFailedToLoad;
    @BindView(R.id.btnAccountReload)
    Button btnReload;
    @BindView(R.id.tvAccountFavoritesCounter)
    TextView tvFavoritesCounter;
    @BindView(R.id.ivAccountToFavorites)
    ImageView ivToFavorites;
    @BindView(R.id.cvAccountSettingsCard)
    RelativeLayout cvAccountCard;
    @BindView(R.id.btnAccountWriteUs)
    Button btnWriteUs;

    private final AccountActivityPresenter mPresenter = new AccountActivityPresenter();

    private String myUsername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportRequestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
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
        super.onCreate(savedInstanceState);
        App.get().getAppComponent().inject(this);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        sdvIcon.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mPresenter.bind(this);
        setSupportActionBar(tbAccount);
        mPresenter.getUsername();
        mPresenter.getFavorites();
        btnReload.setOnClickListener(view -> {
            tvFailedToLoad.setVisibility(View.GONE);
            btnReload.setVisibility(View.GONE);
            pbLoading.setVisibility(View.VISIBLE);
            mPresenter.getUsername();
            mPresenter.getFavorites();
        });
        sdvIcon.setLegacyVisibilityHandlingEnabled(true);
        btnEdit.setOnClickListener(view -> {
            final Intent intent = new Intent(AccountActivity.this, PersonalSettingsActivity.class);
            startActivity(intent);
        });
        btnSettings.setOnClickListener(view -> {
            final Intent i = new Intent(AccountActivity.this, SettingsActivity.class);
            startActivity(i);
        });
        sdvIcon.setOnClickListener(view -> {
            final AlertDialog dialog = new AlertDialog.Builder(AccountActivity.this)
                    .setTitle(getString(R.string.change_profile_pic_title))
                    .setMessage(getString(R.string.change_profile_pic_question))
                    .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                        final int permCheckRead = ContextCompat.checkSelfPermission(AccountActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE);
                        final int permCheckWrite = ContextCompat.checkSelfPermission(AccountActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (permCheckRead != PackageManager.PERMISSION_GRANTED
                                && permCheckWrite != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(AccountActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSION_DIALOG);
                        } else {
                            final Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            getIntent.setType("image/*");
                            final Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            pickIntent.setType("image/*");
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
        ivToFavorites.setOnClickListener(view -> {
            final Intent intent = new Intent(this, FavoritesActivity.class);
            final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                    cvAccountCard,
                    ViewCompat.getTransitionName(cvAccountCard));
            startActivity(intent, options.toBundle());
        });
        tvFavoritesCounter.setOnClickListener(view -> startActivity(new Intent(AccountActivity.this, FavoritesActivity.class)));
        tbAccount.setNavigationOnClickListener(v -> unRevealActivity());
        FavouritesBus.getInstance().setAdditionalConsumer(new FavouritesBus.IConsumer() {
            @Override
            public void consumeRemoved(String id) {
                try {
                    int current = Integer.parseInt(tvFavoritesCounter.getText().toString()) - 1;
                    tvFavoritesCounter.setText(current + "");
                } catch (Exception e) {
                    //shit happens...
                }
            }

            @Override
            public void consumeAdded(String id) {
                try {
                    int current = Integer.parseInt(tvFavoritesCounter.getText().toString()) + 1;
                    tvFavoritesCounter.setText(current + "");
                } catch (Exception e) {
                    //shit
                }
            }
        });
        btnWriteUs.setOnClickListener((view -> {
            Intent intent = new Intent(this, UserFeedbackActivity.class);
            startActivity(intent);
        }));
        new SpotlightView.Builder(this)
                .introAnimationDuration(400)
                .enableRevealAnimation(true)
                .performClick(true)
                .fadeinTextDuration(400)
                .headingTvColor(Color.parseColor("#f98098"))
                .headingTvSize(32)
                .headingTvText(getString(R.string.all_favs_title))
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText(getString(R.string.all_favs_description))
                .maskColor(Color.parseColor("#dc000000"))
//                .target((tabs.getChildAt(0) as ViewGroup).getChildAt(0))
                .target(ivToFavorites)
                .lineAnimDuration(400)
                .lineAndArcColor(Color.parseColor("#ffb06a"))
                .dismissOnTouch(false)
                .dismissOnBackPress(false)
                .enableDismissAfterShown(false)
                .usageId(IConstants.ISpotlight.ACCOUNT_ALL_FAVS)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void unRevealActivity() {
        supportFinishAfterTransition();
    }

    @Override
    public void onBackPressed() {
        unRevealActivity();
    }

    @Override
    protected void onDestroy() {
        sdvIcon.setLayerType(View.LAYER_TYPE_NONE, null);
        mPresenter.unbind();
        FavouritesBus.destroy();
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
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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
        tvUsername.setText(myUsername);
        clAccountLoading.setVisibility(View.GONE);
        clAccount.setVisibility(View.VISIBLE);
        sdvIcon.setImageURI(Uri.parse(IConstants.BASE_URL + "/feed/userPhoto?targetUsername=" + myUsername));
    }

    @Override
    public void onUsernameFailedToLoad() {
        clAccount.setVisibility(View.GONE);
        clAccountLoading.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
        tvFailedToLoad.setVisibility(View.VISIBLE);
        btnReload.setVisibility(View.VISIBLE);
    }

    @Override
    public void onErrorLoadingFavorites() {
        clAccount.setVisibility(View.GONE);
        clAccountLoading.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
        tvFailedToLoad.setVisibility(View.VISIBLE);
        btnReload.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateFavorites(int i) {
        tvFavoritesCounter.setText(String.valueOf(i));
    }

    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(this);
    }
}


