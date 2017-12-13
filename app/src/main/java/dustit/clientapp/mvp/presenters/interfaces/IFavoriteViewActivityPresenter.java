package dustit.clientapp.mvp.presenters.interfaces;

import java.io.IOException;

/**
 * Created by Никита on 12.12.2017.
 */

public interface IFavoriteViewActivityPresenter {
    void removeFromFavorites(String id);
    void downloadImage(String id) throws IOException;
}
