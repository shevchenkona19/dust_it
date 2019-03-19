package dustit.clientapp.mvp.ui.interfaces;

import dustit.clientapp.mvp.model.entities.AchievementsEntity;

public interface INewAccountActivityView extends IActivityView {
    void onUploadFinished();
    void onUploadFailed();
    void onUsernameArrived(String username);
    void onUsernameFailedToLoad();

    void onAchievementsLoaded(AchievementsEntity achievementsEntity);

    void onFailedToLoadAchievements();
}
