package dustit.clientapp.mvp.presenters.activities;

import android.util.Log;

import java.io.File;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.FavoritesUpperEntity;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.model.entities.UsernameEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IAccountActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IAccountActivityView;
import dustit.clientapp.utils.ProgressRequestBody;
import rx.Subscriber;

/**
 * Created by Никита on 11.11.2017.
 */

public class AccountActivityPresenter extends BasePresenter<IAccountActivityView> implements IAccountActivityPresenter, ProgressRequestBody.UploadCallbacks {
    @Inject
    DataManager dataManager;

    public AccountActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }
    @Override
    public void uploadImage(File file) {
        final ProgressRequestBody requestBody = new ProgressRequestBody(file, this);
        addSubscription(dataManager.postPhoto(requestBody, file.getName())
                .subscribe(new Subscriber<ResponseEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onUploadFinished();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("MY", e.getMessage());
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
                            Log.d("MY", e.getMessage());
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
       final int[] size = new int[1];
        addSubscription(dataManager.getAllFavorites()
        .subscribe(new Subscriber<FavoritesUpperEntity>() {
            @Override
            public void onCompleted() {
                getView().updateFavorites(size[0]);
            }

            @Override
            public void onError(Throwable e) {
                getView().onErrorLoadingFavorites();
            }

            @Override
            public void onNext(FavoritesUpperEntity favoritesUpperEntity) {
                size[0] = favoritesUpperEntity.getList().size();
            }
        }));
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
