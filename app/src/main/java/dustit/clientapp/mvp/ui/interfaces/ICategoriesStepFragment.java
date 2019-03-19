package dustit.clientapp.mvp.ui.interfaces;

import java.util.List;

import dustit.clientapp.mvp.model.entities.Category;

public interface ICategoriesStepFragment extends IFragmentView {
    void onCategoriesArrived(List<Category> categories);
    void onCategoriesFailedToLoad();
}
