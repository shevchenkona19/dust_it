package dustit.clientapp.mvp.presenters.fragments;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IBaseFeedFragmentPresenter;
import dustit.clientapp.mvp.ui.interfaces.IBaseFeedFragment;
import rx.Subscriber;

public class BaseFeedFragmentPresenter extends BasePresenter<IBaseFeedFragment> implements IBaseFeedFragmentPresenter {
    @Inject
    DataManager dataManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    public BaseFeedFragmentPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void addToFavourites(String id, int position) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        AtomicReference<ResponseEntity> response = new AtomicReference<>();
        addSubscription(dataManager.addToFavorites(id).subscribe(new Subscriber<ResponseEntity>() {
            @Override
            public void onCompleted() {
                ResponseEntity responseEntity = response.get();
                if (responseEntity != null) {
                    if (responseEntity.getResponse() == 200) {

                        if (getView() != null)
                            getView().onAddedToFavourites(position);
                        return;
                    }
                }
                if (getView() != null)
                    getView().onErrorAddingToFavorites();
            }

            @Override
            public void onError(Throwable e) {
                if (getView() != null)
                    getView().onErrorAddingToFavorites();
            }

            @Override
            public void onNext(ResponseEntity responseEntity) {
                response.set(responseEntity);
            }
        }));
    }

    @Override
    public void removeFromFavourites(String id, int position) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        AtomicReference<ResponseEntity> response = new AtomicReference<>();
        addSubscription(dataManager.removeFromFavorites(id).subscribe(new Subscriber<ResponseEntity>() {
            @Override
            public void onCompleted() {
                ResponseEntity responseEntity = response.get();
                if (responseEntity != null) {
                    if (responseEntity.getResponse() == 200) {
                        if (getView() != null)
                            getView().onRemovedFromFavourites(position);
                        return;
                    }
                }
                if (getView() != null)
                    getView().onErrorRemovingFromFavorites();
            }

            @Override
            public void onError(Throwable e) {
                if (getView() != null)
                    getView().onErrorRemovingFromFavorites();
            }

            @Override
            public void onNext(ResponseEntity responseEntity) {
                response.set(responseEntity);
            }
        }));
    }
}
