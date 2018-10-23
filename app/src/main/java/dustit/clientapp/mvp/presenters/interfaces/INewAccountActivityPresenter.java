package dustit.clientapp.mvp.presenters.interfaces;

import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.ui.interfaces.INewAccountActivityView;

public interface INewAccountActivityPresenter {
    void uploadImage(String path);
    void getUsername();
    void loadFavorites();
    void removeFromFavorites(String id);
}
