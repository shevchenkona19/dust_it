package dustit.clientapp.mvp.presenters.interfaces;

public interface IChangeCategoriesFragmentPresenter {
    void getCategories();
    void sendCategories(String[] ids);
}
