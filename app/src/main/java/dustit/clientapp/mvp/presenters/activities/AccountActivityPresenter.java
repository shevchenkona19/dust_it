package dustit.clientapp.mvp.presenters.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.FavoritesUpperEntity;
import dustit.clientapp.mvp.model.entities.PhotoBody;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.model.entities.UsernameEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IAccountActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IAccountActivityView;
import dustit.clientapp.utils.L;
import dustit.clientapp.utils.ProgressRequestBody;
import dustit.clientapp.utils.containers.Container;
import rx.Subscriber;


public class AccountActivityPresenter extends BasePresenter<IAccountActivityView> implements IAccountActivityPresenter, ProgressRequestBody.UploadCallbacks {
    @Inject
    DataManager dataManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    public AccountActivityPresenter() {
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
    public void getUsername() {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onUsernameArrived("");
            return;
        }
        if (dataManager.isUsernameCached()) {
            getView().onUsernameArrived(dataManager.getCachedUsername());
        } else {
            final StringBuilder builder = new StringBuilder();
            addSubscription(dataManager.getMyUsername()
                    .subscribe(new Subscriber<UsernameEntity>() {
                        @Override
                        public void onCompleted() {
                            String username = builder.toString();
                            dataManager.cacheUsername(username);
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
    }

    @Override
    public void getFavorites() {
        if (!userSettingsDataManager.isRegistered()) {
            getView().updateFavorites(0);
            return;
        }
        final Container<Integer> container = new Container<>();
        addSubscription(dataManager.getAllFavorites()
                .subscribe(new Subscriber<FavoritesUpperEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().updateFavorites(container.get());
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print("Error: " + e.getMessage());
                        getView().onErrorLoadingFavorites();
                    }

                    @Override
                    public void onNext(FavoritesUpperEntity favoritesUpperEntity) {
                        container.put(favoritesUpperEntity.getIds().length);
                    }
                }));
    }

    public boolean isRegistered() {
        return userSettingsDataManager.isRegistered();
    }

    @Override
    public void onProgressUpdate(int percentage) {
        getView().updateUploadingProgress(percentage);
    }

    @Override
    public void onError() {
        getView().onUploadFailed();
    }

    @Override
    public void onFinish() {
        getView().onUploadFinished();
    }

    public String getToken() {
        return dataManager.getToken();
    }
}
