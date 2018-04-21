package dustit.clientapp.mvp.presenters.interfaces;

/**
 * Created by Никита on 03.11.2017.
 */

public interface ICategoriesFragmentPresenter {
    void loadBase(String categoryId);
    void loadWithOffset(String categoryId, int offset);
    void addToFavorites(String id);
}
