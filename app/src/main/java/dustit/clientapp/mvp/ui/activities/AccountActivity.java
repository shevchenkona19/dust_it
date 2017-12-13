package dustit.clientapp.mvp.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.presenters.activities.AccountActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IAccountActivityView;
import dustit.clientapp.utils.IConstants;

public class AccountActivity extends AppCompatActivity implements IAccountActivityView {


    private static final int PICK_IMAGE = 222;
    private static final int CROPPED_IMAGE = 223;
    private static final int PERMISSION_DIALOG = 1010;
    @BindView(R.id.tbAccount)
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
    private final AccountActivityPresenter mPresenter = new AccountActivityPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        mPresenter.bind(this);
        setSupportActionBar(tbAccount);
        mPresenter.getUsername();
        mPresenter.getFavorites();
        pbLoading.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        sdvIcon.setImageURI(Uri.parse("http://www.uni-regensburg.de/Fakultaeten/phil_Fak_II/Psychologie/Psy_II/beautycheck/english/durchschnittsgesichter/m(01-32)_gr.jpg"));
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
                        .setTitle("Поменять фотографию профиля")
                        .setMessage("Вы хотите поменять фотографию своего профиля?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int permCheckRead = ContextCompat.checkSelfPermission(AccountActivity.this,
                                        Manifest.permission.READ_EXTERNAL_STORAGE);
                                int permCheckWrite = ContextCompat.checkSelfPermission(AccountActivity.this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                Log.d("MY", "Perm checked");
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
                                    Intent chooserIntent = Intent.createChooser(getIntent, "Выбирите фото:");
                                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
                                    startActivityForResult(chooserIntent, PICK_IMAGE);
                                }
                            }
                        })
                        .setNegativeButton("Нет", null)
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
    }

    @Override
    protected void onDestroy() {
        mPresenter.unbind();
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
                        Intent chooserIntent = Intent.createChooser(getIntent, "Выбирите фото:");
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
                Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
                return;
            }
            Uri imageSource = data.getData();
            try {
                File image = createImageFile();
                Uri destinationUri = Uri.fromFile(image);
                UCrop.Options options = new UCrop.Options();
                options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                options.setActiveWidgetColor(getResources().getColor(R.color.colorPrimary));
                options.setToolbarTitle("Обрежьте фото");

                UCrop.of(imageSource, destinationUri)
                        .withOptions(options)
                        .start(this, CROPPED_IMAGE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CROPPED_IMAGE) {
            if (resultCode == UCrop.RESULT_ERROR) {
                Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, "Errr...", Toast.LENGTH_SHORT).show();
        sdvIcon.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUsernameArrived(String username) {
        tvUsername.setText(username);
        clAccountLoading.setVisibility(View.GONE);
        clAccount.setVisibility(View.VISIBLE);
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
}


