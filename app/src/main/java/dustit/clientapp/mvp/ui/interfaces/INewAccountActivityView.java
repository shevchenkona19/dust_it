package dustit.clientapp.mvp.ui.interfaces;

import java.util.List;

import dustit.clientapp.mvp.model.entities.AchievementsEntity;
import dustit.clientapp.mvp.model.entities.FavoriteEntity;
import dustit.clientapp.mvp.model.entities.MemEntity;

public interface INewAccountActivityView extends IActivityView {
    void updateUploadingProgress(int percents);
    void onUploadFinished();
    void onUploadFailed();
    void onUsernameArrived(String username);
    void onUsernameFailedToLoad();

    void onFavoritesArrived(List<MemEntity> list);
    void onFailedToLoadFavorites();
    void removedFromFavorites(String id);
    void onFailedToRemoveFromFavorites(String id);

    void onAchievementsLoaded(AchievementsEntity achievementsEntity);

    void onFailedToLoadAchievements();

    void showEmpty();

}
