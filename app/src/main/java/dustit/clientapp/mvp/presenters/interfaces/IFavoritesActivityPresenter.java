package dustit.clientapp.mvp.presenters.interfaces;

/**
 * Created by Никита on 05.12.2017.
 */

public interface IFavoritesActivityPresenter {
    void loadFavorites();
    void removeFromFavorites(String id);
}
