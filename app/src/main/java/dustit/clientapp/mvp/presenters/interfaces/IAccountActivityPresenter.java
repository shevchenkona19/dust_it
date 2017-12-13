package dustit.clientapp.mvp.presenters.interfaces;

import java.io.File;

/**
 * Created by Никита on 11.11.2017.
 */

public interface IAccountActivityPresenter {
    void uploadImage(File file);
    void getUsername();
    void getFavorites();
}
