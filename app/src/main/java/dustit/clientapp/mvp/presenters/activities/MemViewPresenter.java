package dustit.clientapp.mvp.presenters.activities;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.CommentEntity;
import dustit.clientapp.mvp.model.entities.PostCommentEntity;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IMemViewPresenter;
import dustit.clientapp.mvp.ui.interfaces.IMemViewView;
import dustit.clientapp.utils.L;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Никита on 11.11.2017.
 */

public class MemViewPresenter extends BasePresenter<IMemViewView> implements IMemViewPresenter {
    @Inject
    DataManager dataManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    public MemViewPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void loadCommentsBase(String id) {
        getView().onStartLoading();
        final List<CommentEntity> list = new ArrayList<>();
        addSubscription(dataManager.getComments(id, 6, 0)
                .subscribe(new Subscriber<CommentEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onBaseUpdated(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print(e.getMessage());
                        if (getView() != null) {
                            getView().onErrorInLoading();
                        }
                    }

                    @Override
                    public void onNext(CommentEntity commentEntity) {
                        list.add(commentEntity);
                    }
                }));
    }

    @Override
    public void loadCommentsWithOffset(String id, int offset) {
        getView().onStartLoading();
        final List<CommentEntity> list = new ArrayList<>();
        dataManager.getComments(id, 5, offset)
                .subscribe(new Subscriber<CommentEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onPartialUpdate(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print(e.getMessage());
                        getView().onErrorInLoading();
                    }

                    @Override
                    public void onNext(CommentEntity commentEntity) {
                        list.add(commentEntity);
                    }
                });
    }

    @Override
    public void postComment(String id, String text) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        PostCommentEntity commentEntity = new PostCommentEntity(text);
        dataManager.postComment(id, commentEntity)
                .subscribe(new Subscriber<ResponseEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onCommentSentSuccessfully();
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print(e.getMessage());
                        getView().onCommentSendFail();
                    }

                    @Override
                    public void onNext(ResponseEntity responseEntity) {
                        if (responseEntity.getResponse() != 200) {
                            getView().onCommentSendFail();
                        }
                    }
                });
    }

    @Override
    public void postLike(final String id) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        processLikeDislike(dataManager.postLike(id), id);
    }

    @Override
    public void deleteLike(final String id) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        processLikeDislike(dataManager.deleteLike(id), id);

    }

    @Override
    public void postDislike(String id) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        processLikeDislike(dataManager.postDislike(id), id);
    }

    @Override
    public void deleteDislike(String id) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        processLikeDislike(dataManager.deleteDislike(id), id);
    }

    @Override
    public void addToFavourites(String id) {
        addSubscription(dataManager.addToFavorites(id).subscribe(new Subscriber<ResponseEntity>() {
            @Override
            public void onCompleted() {
                getView().onAddedToFavourites();
            }

            @Override
            public void onError(Throwable e) {
                L.print(e.getMessage());
                getView().onError();
            }

            @Override
            public void onNext(ResponseEntity responseEntity) {
                if (isNotSuccess(responseEntity.getResponse())) {
                    getView().onError();
                }
            }
        }));
    }

    @Override
    public void removeFromFavourites(String id) {
        addSubscription(dataManager.removeFromFavorites(id).subscribe(new Subscriber<ResponseEntity>() {
            @Override
            public void onCompleted() {
                getView().onRemovedFromFavourites();
            }

            @Override
            public void onError(Throwable e) {
                L.print(e.getMessage());
                getView().onError();
            }

            @Override
            public void onNext(ResponseEntity responseEntity) {
                if (isNotSuccess(responseEntity.getResponse())) {
                    getView().onError();
                }
            }
        }));
    }

    private boolean isNotSuccess(int code) {
        return code != 200;
    }

    private void processLikeDislike(Observable<ResponseEntity> subscriber, final String id) {
        addSubscription(subscriber.subscribe(new Subscriber<ResponseEntity>() {
            @Override
            public void onCompleted() {
                getView().onQuarrySendedSuccessfully(id);
            }

            @Override
            public void onError(Throwable e) {
                getView().onErrorSendingQuarry();
            }

            @Override
            public void onNext(ResponseEntity responseEntity) {
                if (isNotSuccess(responseEntity.getResponse())) {
                    getView().onErrorSendingQuarry();
                }
            }
        }));
    }
}
