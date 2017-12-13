package dustit.clientapp.mvp.presenters.interfaces;

/**
 * Created by shevc on 23.10.2017.
 * Let's GO!
 */

public interface IHotFragmentPresenter {
    void loadBase();
    void loadWithOffset(int offset);
    void postLike(String id);
    void deleteLike(String id);
    void postDislike(String id);
    void deleteDislike(String id);
    void addToFavorites(String id);
}
