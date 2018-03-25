package dustit.clientapp.mvp.presenters.interfaces;

/**
 * Created by Никита on 03.11.2017.
 */

public interface ICategoriesFragmentPresenter {
    void loadBase(String categoryId);
    void loadWithOffset(String categoryId, int offset);
    void postLike(String id);
    void deleteLike(String id);
    void postDislike(String id);
    void deleteDislike(String id);
    void addToFavorites(String id);
}
