package dustit.clientapp.mvp.presenters.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.AchievementsEntity;
import dustit.clientapp.mvp.model.entities.FavoriteEntity;
import dustit.clientapp.mvp.model.entities.FavoritesUpperEntity;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.PhotoBody;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.model.entities.UsernameEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.INewAccountActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.INewAccountActivityView;
import dustit.clientapp.utils.L;
import rx.Subscriber;

public class NewAccountActivityPresenter extends BasePresenter<INewAccountActivityView> implements INewAccountActivityPresenter {
    @Inject
    DataManager dataManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    public NewAccountActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void uploadImage(String path) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        final PhotoBody photoBody = new PhotoBody();
        String extension = path.substring(path.lastIndexOf("."));
        final Bitmap bm = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        photoBody.setPhoto(encodedImage);
        photoBody.setExt(extension);
        addSubscription(dataManager.postPhoto(photoBody)
                .subscribe(new Subscriber<ResponseEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onUploadFinished();
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print(e.getMessage());
                        getView().onUploadFailed();
                    }

                    @Override
                    public void onNext(ResponseEntity responseEntity) {
                        if (responseEntity.getResponse() != 200) {
                            getView().onUploadFailed();
                        }
                    }
                }));
    }

    @Override
    public void getUsername(String id) {
        final StringBuilder builder = new StringBuilder();
        addSubscription(dataManager.getUsername(id)
                .subscribe(new Subscriber<UsernameEntity>() {
                    @Override
                    public void onCompleted() {
                        String username = builder.toString();
                        getView().onUsernameArrived(username);
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print(e.getMessage());
                        getView().onUsernameFailedToLoad();
                    }

                    @Override
                    public void onNext(UsernameEntity usernameEntity) {
                        builder.append(usernameEntity.getUsername());
                    }
                }));
    }

    public boolean isRegistered() {
        return userSettingsDataManager.isRegistered();
    }

    public String getToken() {
        return dataManager.getToken();
    }

    @Override
    public void getAchievements(String userId) {
        AtomicReference<AchievementsEntity> achievements = new AtomicReference<>();
        addSubscription(dataManager.getAchievements(userId)
        .subscribe(new Subscriber<AchievementsEntity>() {
            @Override
            public void onCompleted() {
                getView().onAchievementsLoaded(achievements.get());
            }

            @Override
            public void onError(Throwable e) {
                L.print("Error: " + e.getMessage());
                getView().onFailedToLoadAchievements();
            }

            @Override
            public void onNext(AchievementsEntity achievementsEntity) {
                achievements.set(achievementsEntity);
            }
        }));
    }

    public String loadMyId() {
        return dataManager.loadId();
    }
}
