package dustit.clientapp.mvp.ui.interfaces;

import java.util.List;

import dustit.clientapp.mvp.model.entities.FavoriteEntity;

public interface IFavoriteActivityView extends IActivityView {
    void onFavoritesArrived(List<FavoriteEntity> list);
    void onFailedToLoadFavorites();
    void removedFromFavorites(String id);
    void onFailedToRemoveFromFavorites(String id);

    void showEmpty();

}
