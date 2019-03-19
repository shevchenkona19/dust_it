package dustit.clientapp.mvp.ui.interfaces;

import java.util.List;

import dustit.clientapp.mvp.model.entities.CommentEntity;
import dustit.clientapp.mvp.model.entities.NewAchievementEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;

/**
 * Created by Никита on 11.11.2017.
 */

public interface IMemViewView extends IActivityView {
    void onBaseUpdated(List<CommentEntity> list);

    void onPartialUpdate(List<CommentEntity> list);

    void onErrorInLoading();

    void onStartLoading();

    void onCommentSentSuccessfully();

    void onCommentSendFail();

    void onError();

    void onAchievementUpdate(NewAchievementEntity achievementEntity);

    void onAnswerSentSuccessfully();

    void onCommentsToCommentIdLoaded(List<CommentEntity> list);

    boolean checkPermission();

    void onDownloadFailed();

    void onDownloaded(String res);

    void getPermissions();

    void changedFeedback(RefreshedMem refreshedMem);

    void onError(RestoreMemEntity restoreMemEntity);

    void onIsFavourite(boolean favourite);

}
