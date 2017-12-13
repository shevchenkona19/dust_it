package dustit.clientapp.mvp.presenters.activities;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.FavoritesUpperEntity;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IFavoritesActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IFavoriteActivityView;
import rx.Subscriber;

public class FavoritesActivityPresenter extends BasePresenter<IFavoriteActivityView> implements IFavoritesActivityPresenter {
    @Inject
    DataManager dataManager;

    public FavoritesActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }
    @Override
    public void loadFavorites() {
        List<FavoritesUpperEntity.FavoriteEntity> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(new FavoritesUpperEntity.FavoriteEntity("http://www.gettyimages.com/gi-resources/images/VR/GettyImages-500977426.jpg"));
        }
        getView().onFavoritesArrived(list);
        /*final FavoritesUpperEntity[] favoritesEntity = new FavoritesUpperEntity[1];
        addSubscription(dataManager.getAllFavorites()
                .subscribe(new Subscriber<FavoritesUpperEntity>() {
                    @Override
                    public void onCompleted() {
                        if (favoritesEntity[0].getList().size() == 0) {
                            getView().showEmpty();
                        } else {
                            getView().onFavoritesArrived(favoritesEntity[0].getList());

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("MY", e.getMessage());
                        getView().onFailedToLoadFavorites();
                    }

                    @Override
                    public void onNext(FavoritesUpperEntity favoritesUpperEntity) {
                        favoritesEntity[0] = favoritesUpperEntity;
                    }
                }));*/
    }

    @Override
    public void removeFromFavorites(final String id) {
        addSubscription(dataManager.removeFromFavorites(id)
                .subscribe(new Subscriber<ResponseEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().removedFromFavorites(id);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("MY", e.getMessage());
                        getView().onFailedToRemoveFromFavorites(id);
                    }

                    @Override
                    public void onNext(ResponseEntity responseEntity) {
                        if (responseEntity.getResponse() != 200) {
                            getView().onFailedToRemoveFromFavorites(id);
                        }
                    }
                }));
    }

    public String getToken() {
        return dataManager.getToken();
    }
}
