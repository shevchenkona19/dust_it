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
import dustit.clientapp.mvp.presenters.interfaces.IHotFragmentPresenter;
import dustit.clientapp.mvp.ui.interfaces.IHotFragmentView;
import dustit.clientapp.utils.FavoritesUtils;
import dustit.clientapp.utils.L;
import dustit.clientapp.utils.containers.Container;
import rx.Subscriber;

/**
 * Created by shevc on 23.10.2017.
 * Let's GO!
 */

public class HotFragmentPresenter extends BasePresenter<IHotFragmentView> implements IHotFragmentPresenter {

    @Inject
    DataManager dataManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    public HotFragmentPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void loadBase() {
        final List<MemEntity> list = new ArrayList<>();
        addSubscription(dataManager.getHot(6, 0)
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
        addSubscription(dataManager.getHot(5, offset)
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
    public void postLike(final String id) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        final int[] code = {0};
        addSubscription(dataManager.postLike(id).subscribe(new Subscriber<ResponseEntity>() {
            @Override
            public void onCompleted() {
                if (code[0] != 200) {
                    getView().onLikeDeletingError(id);
                }
            }

            @Override
            public void onError(Throwable e) {
                L.print("deleteLike: " + e.getMessage());
                getView().onLikePostError(id);
            }

            @Override
            public void onNext(ResponseEntity responseEntity) {
                code[0] = responseEntity.getResponse();
            }
        }));
    }

    @Override
    public void deleteLike(final String id) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        final int[] code = {0};
        addSubscription(dataManager.deleteLike(id).subscribe(new Subscriber<ResponseEntity>() {
            @Override
            public void onCompleted() {
                if (code[0] != 200) {
                    getView().onLikeDeletingError(id);
                }
            }

            @Override
            public void onError(Throwable e) {
                L.print("deleteLike: " + e.getMessage());
                getView().onLikeDeletingError(id);
            }

            @Override
            public void onNext(ResponseEntity responseEntity) {
                code[0] = responseEntity.getResponse();
            }
        }));
    }

    @Override
    public void postDislike(final String id) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        final int[] code = {0};
        addSubscription(dataManager.postDislike(id).subscribe(new Subscriber<ResponseEntity>() {
            @Override
            public void onCompleted() {
                if (code[0] != 200) {
                    getView().onDislikePostError(id);
                }
            }

            @Override
            public void onError(Throwable e) {
                L.print("deleteLike: " + e.getMessage());
                getView().onLikeDeletingError(id);
            }

            @Override
            public void onNext(ResponseEntity responseEntity) {
                code[0] = responseEntity.getResponse();
            }
        }));
    }

    @Override
    public void deleteDislike(final String id) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        final int[] code = {0};
        addSubscription(dataManager.deleteDislike(id).subscribe(new Subscriber<ResponseEntity>() {
            @Override
            public void onCompleted() {
                if (code[0] != 200) {
                    getView().onDislikePostError(id);
                }
            }

            @Override
            public void onError(Throwable e) {
                L.print("deleteLike: " + e.getMessage());
                getView().onDislikeDeletingError(id);
            }

            @Override
            public void onNext(ResponseEntity responseEntity) {
                code[0] = responseEntity.getResponse();
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

    public void deleteFromFavorites(String id) {
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
