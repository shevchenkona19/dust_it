package dustit.clientapp.mvp.presenters.fragments;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IFeedFragmentPresenter;
import dustit.clientapp.mvp.ui.interfaces.IFeedFragmentView;
import dustit.clientapp.utils.FavoritesUtils;
import dustit.clientapp.utils.L;
import dustit.clientapp.utils.containers.Container;
import rx.Subscriber;

/**
 * Created by shevc on 05.10.2017.
 * Let's GO!
 */

public class FeedFragmentPresenter extends BasePresenter<IFeedFragmentView> implements IFeedFragmentPresenter {
    @Inject
    DataManager dataManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    public FeedFragmentPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void loadBase() {
        final List<MemEntity> list = new ArrayList<>();
        /*list.add(new MemEntity("0","https://www.picmonkey.com/_/static/images/index/picmonkey_twitter_02.24fd38f81e59.jpg", "","20","30", true, false, false));
        getView().onBaseUpdated(list);*/
        addSubscription(dataManager.getFeed(6, 0)
                .subscribe(new Subscriber<MemEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onBaseUpdated(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print("Error in loading base: " + e.getMessage());
                        getView().onErrorInLoading();
                    }

                    @Override
                    public void onNext(MemEntity memEntity) {
                        list.add(memEntity);
                    }
                }));
    }

    @Override
    public void loadWithOffset(int offset) {
        final List<MemEntity> list = new ArrayList<>();
        addSubscription(dataManager.getFeed(5, offset)
                .subscribe(new Subscriber<MemEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onPartialUpdate(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print("Error in loading partial: " + e.getMessage());
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


    public void removeFromFavorites(final String id) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        final Container<Integer> containerMessage = new Container<>();
        addSubscription(dataManager.removeFromFavorites(id)
                .subscribe(new Subscriber<ResponseEntity>() {
                    @Override
                    public void onCompleted() {
                        if (containerMessage.get() != 200) {
                            getView().onErrorInRemovingFromFavorites(id);
                        } else {
                            getView().onRemovedFromFavorites(id);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print(e.getMessage());
                        getView().onErrorInRemovingFromFavorites(id);
                    }

                    @Override
                    public void onNext(ResponseEntity responseEntity) {
                        containerMessage.put(responseEntity.getResponse());
                    }
                }));
    }
}
