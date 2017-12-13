package dustit.clientapp.mvp.ui.interfaces;

/**
 * Created by Никита on 11.11.2017.
 */

public interface IAccountActivityView extends IActivityView {
    void updateUploadingProgress(int percents);
    void onUploadFinished();
    void onUploadFailed();
    void onUsernameArrived(String username);
    void onUsernameFailedToLoad();

    void onErrorLoadingFavorites();


    void updateFavorites(int i);
}
