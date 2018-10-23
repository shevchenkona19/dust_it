package dustit.clientapp.mvp.ui.interfaces;

import java.util.List;

import dustit.clientapp.mvp.model.entities.FavoriteEntity;

public interface INewAccountActivityView extends IActivityView {
    void updateUploadingProgress(int percents);
    void onUploadFinished();
    void onUploadFailed();
    void onUsernameArrived(String username);
    void onUsernameFailedToLoad();

    void onFavoritesArrived(List<FavoriteEntity> list);
    void onFailedToLoadFavorites();
    void removedFromFavorites(String id);
    void onFailedToRemoveFromFavorites(String id);

    void showEmpty();

}
