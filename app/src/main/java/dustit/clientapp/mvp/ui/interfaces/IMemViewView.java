package dustit.clientapp.mvp.ui.interfaces;

import java.util.List;

import dustit.clientapp.mvp.model.entities.CommentEntity;

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

    void onErrorSendingQuarry();

    void onQuarrySendedSuccessfully(String id);
}
