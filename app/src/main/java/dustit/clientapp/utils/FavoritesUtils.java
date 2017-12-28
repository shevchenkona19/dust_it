package dustit.clientapp.utils;

import android.util.Log;

import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import rx.Subscriber;
import rx.Subscription;

public class FavoritesUtils {
    public interface IFavoriteCallback {
        void onAddedToFavorites(String id);

        void onError(String id);
    }

    private IFavoriteCallback callback;
    private DataManager dataManager;
    private Subscription subscription;

    public FavoritesUtils(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void addCallback(IFavoriteCallback callback) {
        this.callback = callback;
    }

    public void addToFavorites(final String id) {
        if (callback != null) {
            subscription = dataManager.addToFavorites(id)
                    .subscribe(new Subscriber<ResponseEntity>() {
                        @Override
                        public void onCompleted() {
                            callback.onAddedToFavorites(id);
                            destroy();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("MY", e.getMessage());
                            callback.onError(id);
                        }

                        @Override
                        public void onNext(ResponseEntity responseEntity) {
                            if (responseEntity.getResponse() != 200) {
                                callback.onError(id);
                            }
                        }
                    });
        } else {
            destroy();
        }
    }

    private void destroy() {
        dataManager = null;
        callback = null;
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
    }
}