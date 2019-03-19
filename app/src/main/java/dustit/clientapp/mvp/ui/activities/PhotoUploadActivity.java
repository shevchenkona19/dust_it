package dustit.clientapp.mvp.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.presenters.activities.UploadActivityPresenter;
import dustit.clientapp.mvp.ui.adapters.UploadStepsAdapter;
import dustit.clientapp.mvp.ui.fragments.CategoriesStepFragment;
import dustit.clientapp.mvp.ui.interfaces.IPhotoUploadActivityView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;

public class PhotoUploadActivity extends AppCompatActivity implements IPhotoUploadActivityView, StepperLayout.StepperListener, CategoriesStepFragment.ICategoriesStepFragmentInteraction {

    private static final int PERMISSION_DIALOG = 408;
    private static final int PICK_IMAGE = 36;
    private static final int CROP_PHOTO = 84;

    @BindView(R.id.slUploadPhoto)
    StepperLayout slUploadPhoto;

    Uri croppedImage = null;
    private UploadActivityPresenter presenter = new UploadActivityPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload);
        ButterKnife.bind(this);
        presenter.bind(this);
        if (checkPermissions()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_DIALOG);
        } else {
            requestPhoto();
        }
        slUploadPhoto.setListener(this);
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }

    private boolean checkPermissions() {
        final int permCheckRead = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        final int permCheckWrite = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permCheckRead != PackageManager.PERMISSION_GRANTED
                || permCheckWrite != PackageManager.PERMISSION_GRANTED;
    }

    private void requestPhoto() {
        final Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        final Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        final Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.choose_photo));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        startActivityForResult(chooserIntent, PICK_IMAGE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_DIALOG:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestPhoto();
                } else {
                    AlertDialog dialog = AlertBuilder.getUploadPermissionsRequired(this);
                    dialog.setOnCancelListener(dialog1 -> finishWithError());
                    dialog.show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode != RESULT_OK || data == null) {
                finishWithError();
                return;
            }
            Uri sourceUri = data.getData();
            if (sourceUri == null) {
                finishWithError();
                return;
            }
            try {
                File tempImage = createImageFile();
                Uri destinationUri = Uri.fromFile(tempImage);
                UCrop.Options options = new UCrop.Options();
                options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                options.setFreeStyleCropEnabled(true);
                options.setToolbarColor(getResources().getColor(R.color.colorPrimaryDefault));
                options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkDefault));
                options.setActiveWidgetColor(getResources().getColor(R.color.colorPrimaryDefault));
                options.setToolbarTitle(getString(R.string.crop_photo));
                UCrop.of(sourceUri, destinationUri)
                        .withOptions(options)
                        .start(this, CROP_PHOTO);
            } catch (IOException e) {
                e.printStackTrace();
                finishWithError();
            }
        } else if (requestCode == CROP_PHOTO) {
            if (resultCode == UCrop.RESULT_ERROR || data == null) {
                finishWithError();
                return;
            }
            try {
                croppedImage = UCrop.getOutput(data);
            } catch (Exception e) {
                finishWithError();
            }
            UploadStepsAdapter adapter = new UploadStepsAdapter(getSupportFragmentManager(), this, croppedImage);
            slUploadPhoto.setAdapter(adapter);
            slUploadPhoto.proceed();
            //Finish - to categories
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final String imageFileName = "MEM_" + timeStamp + "_";
        final File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    @Override
    public void onCompleted(View completeButton) {

    }

    @Override
    public void onError(VerificationError verificationError) {

    }

    @Override
    public void onStepSelected(int newStepPosition) {
        if (newStepPosition == 1) {
            new Handler().postDelayed(() -> {
                setResult(IConstants.IRequest.RESULT_OK);
                finish();
            }, 3000);
        }
    }

    private void finishWithError() {
        Toast.makeText(this, getText(R.string.error), Toast.LENGTH_SHORT).show();
        setResult(IConstants.IRequest.RESULT_ERROR);
        finish();
    }

    @Override
    public void onReturn() {

    }

    @Override
    public void sendCategories(String checkedCategories) {
        presenter.uploadPhoto(croppedImage, checkedCategories);
    }

    @Override
    public void onPhotoUploaded() {
        slUploadPhoto.proceed();
        slUploadPhoto.setShowBottomNavigation(false);
    }

    @Override
    public void onErrorPhotoUploading() {
        Toast.makeText(this, getText(R.string.error), Toast.LENGTH_SHORT).show();
        finishWithError();
    }

    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(this);
        finishWithError();
    }
}
