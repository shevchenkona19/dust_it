package dustit.clientapp.mvp.presenters.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.NewResponseEntity;
import dustit.clientapp.mvp.model.entities.PhotoBody;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.model.entities.UploadBody;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IPhotoUploadActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IPhotoUploadActivityView;
import rx.Subscriber;

public class UploadActivityPresenter extends BasePresenter<IPhotoUploadActivityView> implements IPhotoUploadActivityPresenter {

    @Inject
    DataManager dataManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    public UploadActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void uploadPhoto(Uri upload, String categories) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        String path = upload.getPath();
        UploadBody body = new UploadBody();
        final Bitmap bm = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        body.setPhoto(encodedImage);
        body.setCategories(categories);
        AtomicReference<NewResponseEntity> res = new AtomicReference<>();
        addSubscription(dataManager.uploadMeme(body).subscribe(new Subscriber<NewResponseEntity>() {
            @Override
            public void onCompleted() {
                if (res.get().isSuccess()) {
                    getView().onPhotoUploaded();
                    return;
                }
                getView().onErrorPhotoUploading();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                getView().onErrorPhotoUploading();
            }

            @Override
            public void onNext(NewResponseEntity responseEntity) {
                res.set(responseEntity);
            }
        }));
    }
}
