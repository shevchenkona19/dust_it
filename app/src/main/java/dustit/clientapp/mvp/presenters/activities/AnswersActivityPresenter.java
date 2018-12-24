package dustit.clientapp.mvp.presenters.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.CommentEntity;
import dustit.clientapp.mvp.model.entities.PostCommentEntity;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IAnswersActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IAnswersActivityView;
import dustit.clientapp.utils.L;
import rx.Subscriber;

public class AnswersActivityPresenter extends BasePresenter<IAnswersActivityView> implements IAnswersActivityPresenter {
    @Inject
    DataManager dataManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;
    private String commentId;

    public AnswersActivityPresenter(String commentId) {
        App.get().getAppComponent().inject(this);
        this.commentId = commentId;
    }

    @Override
    public void loadBase(int limit) {
        loadBaseAnswers(limit);
    }

    @Override
    public void loadBase() {
        loadBaseAnswers(6);
    }

    private void loadBaseAnswers(int limit) {
        final List<CommentEntity> commentEntities = new ArrayList<>();
        addSubscription(dataManager.getAnswersForComment(commentId, limit, 0).subscribe(new Subscriber<CommentEntity>() {
            @Override
            public void onCompleted() {
                getView().onBaseUpdate(commentEntities);
            }

            @Override
            public void onError(Throwable e) {
                getView().onFailedToLoadComments();
            }

            @Override
            public void onNext(CommentEntity commentEntity) {
                commentEntities.add(commentEntity);
            }
        }));
    }

    @Override
    public void loadPartial(int offset) {
        final List<CommentEntity> list = new ArrayList<>();
        addSubscription(dataManager.getAnswersForComment(commentId, 5, offset).subscribe(new Subscriber<CommentEntity>() {
            @Override
            public void onCompleted() {
                getView().onPartialUpdate(list);
            }

            @Override
            public void onError(Throwable e) {
                getView().onFailedToLoadComments();
            }

            @Override
            public void onNext(CommentEntity commentEntity) {
                list.add(commentEntity);
            }
        }));
    }

    @Override
    public void postRespond(String userId, String text, String imageId) {
        AtomicReference<ResponseEntity> res = new AtomicReference<>();
        addSubscription(dataManager.postAnswerForComment(imageId, commentId, userId, new PostCommentEntity(text)).subscribe(new Subscriber<ResponseEntity>() {
            @Override
            public void onCompleted() {
                ResponseEntity responseEntity = res.get();
                if (responseEntity != null) {
                    if (responseEntity.getResponse() == 200) {
                        if (responseEntity.isAchievementUpdate()) {
                            getView().onAchievementUpdate(responseEntity.getAchievementEntity());
                        }
                        getView().onAnswered();
                    } else getView().onAnswerFailed();
                }
            }

            @Override
            public void onError(Throwable e) {
                getView().onAnswerFailed();
            }

            @Override
            public void onNext(ResponseEntity responseEntity) {
                res.set(responseEntity);
            }
        }));
    }

    @Override
    public void loadCommentsToId(String newCommentId, String baseCommentId, String imageId) {
        final List<CommentEntity> list = new ArrayList<>();
        addSubscription(dataManager.getAnswersForCommentToId(newCommentId, baseCommentId, imageId).subscribe(new Subscriber<CommentEntity>() {
            @Override
            public void onCompleted() {
                getView().onAnswersToIdLoaded(list);
            }

            @Override
            public void onError(Throwable e) {
                L.print("Error: " + e.getMessage());
                getView().onError();
            }

            @Override
            public void onNext(CommentEntity commentEntity) {
                list.add(commentEntity);
            }
        }));
    }



    public boolean isRegistered() {
        return userSettingsDataManager.isRegistered();
    }
}
