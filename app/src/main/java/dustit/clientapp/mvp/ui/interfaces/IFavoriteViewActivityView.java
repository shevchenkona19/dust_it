package dustit.clientapp.mvp.ui.interfaces;


public interface IFavoriteViewActivityView extends IActivityView{
    void onRemovedFromFavorites();
    void onErrorRemovingFromFavorites();
    void onDownloaded(String pathToImage);

    boolean checkPermission();

    void getPermissions();

    void onDownloadFailed();

    void onError();

    void onAddedToFavourites();
}
