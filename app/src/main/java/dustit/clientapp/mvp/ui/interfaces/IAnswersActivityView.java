package dustit.clientapp.mvp.ui.interfaces;

import java.util.List;

import dustit.clientapp.mvp.model.entities.CommentEntity;
import dustit.clientapp.mvp.model.entities.NewAchievementEntity;

public interface IAnswersActivityView extends IActivityView {
    void onAnswered();

    void onAnswerFailed();

    void onBaseUpdate(List<CommentEntity> list);

    void onPartialUpdate(List<CommentEntity> list);

    void onFailedToLoadComments();

    void onAchievementUpdate(NewAchievementEntity achievementEntity);

    void onError();

    void onAnswersToIdLoaded(List<CommentEntity> list);
}
