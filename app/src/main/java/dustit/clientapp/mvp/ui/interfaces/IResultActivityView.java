package dustit.clientapp.mvp.ui.interfaces;

import java.util.List;

import dustit.clientapp.mvp.model.entities.Category;

/**
 * Created by Никита on 09.11.2017.
 */

public interface IResultActivityView extends IActivityView {
    void onCategoriesLoaded(List<Category> list);
    void onErrorInLoadingCategories();
    void onFailedToSendCategories();
    void onFinishedResultActivity();
}
