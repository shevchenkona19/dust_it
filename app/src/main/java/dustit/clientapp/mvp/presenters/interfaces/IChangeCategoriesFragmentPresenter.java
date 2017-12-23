package dustit.clientapp.mvp.presenters.interfaces;

/**
 * Created by Никита on 23.12.2017.
 */

public interface IChangeCategoriesFragmentPresenter {
    void getCategories();
    void sendCategories(String ids);
}
