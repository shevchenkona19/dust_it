package dustit.clientapp.mvp.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.presenters.activities.AccountActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IAccountActivityView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;
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
    @BindView(R.id.clAccountSettingsContainer)
    ConstraintLayout clContainer;
    @BindView(R.id.cvAccountSettingsCard)
    CardView cvAccountCard;
    private final AccountActivityPresenter mPresenter = new AccountActivityPresenter();

    private String themeId = "";

    @Inject
    ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        mPresenter.bind(this);
        App.get().getAppComponent().inject(this);
        setSupportActionBar(tbAccount);
        mPresenter.getUsername();
        mPresenter.getFavorites();
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvFailedToLoad.setVisibility(View.GONE);
                btnReload.setVisibility(View.GONE);
                pbLoading.setVisibility(View.VISIBLE);
                mPresenter.getUsername();
                mPresenter.getFavorites();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, PersonalSettingsActivity.class);
                startActivity(intent);
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AccountActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });
        sdvIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = new AlertDialog.Builder(AccountActivity.this)
                        .setTitle(getString(R.string.change_profile_pic_title))
                        .setMessage(getString(R.string.change_profile_pic_question))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int permCheckRead = ContextCompat.checkSelfPermission(AccountActivity.this,
                                        Manifest.permission.READ_EXTERNAL_STORAGE);
                                int permCheckWrite = ContextCompat.checkSelfPermission(AccountActivity.this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                if (permCheckRead != PackageManager.PERMISSION_GRANTED
                                        && permCheckWrite != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(AccountActivity.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            PERMISSION_DIALOG);
                                } else {
                                    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                    getIntent.setType("image/*");
                                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    pickIntent.setType("image/*");
                                    Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.choose_photo));
                                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
                                    startActivityForResult(chooserIntent, PICK_IMAGE);
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));
                    }
                });

                dialog.show();
            }
        });
        ivToFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountActivity.this, FavoritesActivity.class));
            }
        });
        tvFavoritesCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountActivity.this, FavoritesActivity.class));
            }
        });
        tbAccount.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setColors();
        themeId = themeManager.subscribeToThemeChanges(new ThemeManager.IThemable() {
            @Override
            public void notifyThemeChanged(ThemeManager.Theme t) {
                setColors();
            }
        });
    }

    private void setColors() {
        tbAccount.setBackgroundResource(themeManager.getPrimaryColor());
        tvUsername.setTextColor(getColorFromResources(themeManager.getMainTextMainAppColor()));
        btnEdit.setTextColor(getColorFromResources(themeManager.getMainTextMainAppColor()));
        btnReload.setTextColor(getColorFromResources(themeManager.getOnCardAccentColor()));
        btnSettings.setTextColor(getColorFromResources(themeManager.getMainTextMainAppColor()));
        pbLoading.getIndeterminateDrawable().setColorFilter(themeManager.getPrimaryColor(), PorterDuff.Mode.MULTIPLY);
        tvFailedToLoad.setTextColor(getColorFromResources(themeManager.getSecondaryTextMainAppColor()));
        btnReload.setTextColor(getColorFromResources(themeManager.getMainTextMainAppColor()));
        tvFavoritesCounter.setTextColor(getColorFromResources(themeManager.getSecondaryTextMainAppColor()));
        clContainer.setBackgroundResource(themeManager.getBackgroundMainColor());
        cvAccountCard.setCardBackgroundColor(getColorFromResources(themeManager.getCardBackgroundColor()));
        ivToFavorites.setColorFilter(getColorFromResources(themeManager.getOnCardAccentColor()), PorterDuff.Mode.SRC_ATOP);
        btnEdit.getCompoundDrawables()[2].setColorFilter(getColorFromResources(themeManager.getOnCardAccentColor()), PorterDuff.Mode.SRC_ATOP);
        btnSettings.getCompoundDrawables()[2].setColorFilter(getColorFromResources(themeManager.getOnCardAccentColor()), PorterDuff.Mode.SRC_ATOP);
        tbAccount.getNavigationIcon().setColorFilter(getColorFromResources(themeManager.getAccentColor()), PorterDuff.Mode.SRC_ATOP);
        tbAccount.setTitleTextColor(getColorFromResources(themeManager.getMainTextToolbarColor()));
    }

    private int getColorFromResources(int c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getColor(c);
        } else {
            return getResources().getColor(c);
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.unbind();
        themeManager.unsubscribe(themeId);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_DIALOG: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        getIntent.setType("image/*");
                        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickIntent.setType("image/*");
                        Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.choose_photo));
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
            Uri imageSource = data.getData();
            try {
                File image = createImageFile();
                Uri destinationUri = Uri.fromFile(image);
                UCrop.Options options = new UCrop.Options();
                options.setToolbarColor(getResources().getColor(R.color.colorPrimaryDefault));
                options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkDefault));
                options.setActiveWidgetColor(getResources().getColor(R.color.colorPrimaryDefault));
                options.setToolbarTitle(getString(R.string.crop_photo));
                UCrop.of(imageSource, destinationUri)
                        .withOptions(options)
                        .start(this, CROPPED_IMAGE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CROPPED_IMAGE) {
            if (resultCode == UCrop.RESULT_ERROR) {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                try {
                    throw UCrop.getError(data);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                return;
            }
            Uri croppedImage = UCrop.getOutput(data);
            File file = new File(croppedImage.getPath());
            mPresenter.uploadImage(file);
            sdvIcon.setVisibility(View.GONE);
            cpbPhotoLoading.setVisibility(View.VISIBLE);

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        sdvIcon.setImageURI(IConstants.BASE_URL + "getUserPhoto?token=" + mPresenter.getToken());
    }

    @Override
    public void onUploadFailed() {
        cpbPhotoLoading.setVisibility(View.GONE);
        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        sdvIcon.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUsernameArrived(String username) {
        tvUsername.setText(username);
        clAccountLoading.setVisibility(View.GONE);
        clAccount.setVisibility(View.VISIBLE);
        sdvIcon.setImageURI(Uri.parse(IConstants.BASE_URL + "/feed/getUserPhoto?targetUsername=" + username));
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


