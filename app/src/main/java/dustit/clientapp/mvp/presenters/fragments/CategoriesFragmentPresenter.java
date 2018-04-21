package dustit.clientapp.mvp.presenters.fragments;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.ICategoriesFragmentPresenter;
import dustit.clientapp.mvp.ui.interfaces.ICategoriesFragmentView;
import dustit.clientapp.utils.FavoritesUtils;
import dustit.clientapp.utils.L;
import dustit.clientapp.utils.containers.Container;
import rx.Subscriber;

public class CategoriesFragmentPresenter extends BasePresenter<ICategoriesFragmentView> implements ICategoriesFragmentPresenter {
    @Inject
    DataManager dataManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    public CategoriesFragmentPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void loadBase(String categoryId) {
        final List<MemEntity> list = new ArrayList<>();
        addSubscription(dataManager.getCategoriesFeed(categoryId, 6, 0)
                .subscribe(new Subscriber<MemEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onBaseUpdated(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onErrorInLoading();
                    }

                    @Override
                    public void onNext(MemEntity memEntity) {
                        list.add(memEntity);
                    }
                }));
    }

    @Override
    public void loadWithOffset(String categoryId, int offset) {
        final List<MemEntity> list = new ArrayList<>();
        addSubscription(dataManager.getCategoriesFeed(categoryId, 5, offset)
                .subscribe(new Subscriber<MemEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onPartialUpdate(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onErrorInLoading();
                    }

                    @Override
                    public void onNext(MemEntity memEntity) {
                        list.add(memEntity);
                    }
                }));
    }

    @Override
    public void addToFavorites(String id) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        FavoritesUtils favoritesUtils = new FavoritesUtils(dataManager);
        favoritesUtils.addCallback(new FavoritesUtils.IFavoriteCallback() {
            @Override
            public void onAddedToFavorites(String id) {
                getView().onAddedToFavorites(id);
            }

            @Override
            public void onError(String id) {
                getView().onErrorInAddingToFavorites(id);
            }
        });
        favoritesUtils.addToFavorites(id);
    }

    public void removeFromFavorites(String id) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        final Container<String> containerId = new Container<>();
        final Container<Integer> containerMessage = new Container<>();
        containerId.put(id);
        addSubscription(dataManager.removeFromFavorites(id)
                .subscribe(new Subscriber<ResponseEntity>() {
                    @Override
                    public void onCompleted() {
                        if (containerMessage.get() != 200) {
                            getView().onErrorInRemovingFromFavorites(containerId.get());
                        } else {
                            getView().onRemovedFromFavorites(containerId.get());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print(e.getMessage());
                        getView().onErrorInRemovingFromFavorites(containerId.get());
                    }

                    @Override
                    public void onNext(ResponseEntity responseEntity) {
                        containerMessage.put(responseEntity.getResponse());
                    }
                }));
    }
}
