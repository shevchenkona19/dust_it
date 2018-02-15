package dustit.clientapp.mvp.ui.interfaces;

import java.util.List;

import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.model.entities.PersonalCategory;

/**
 * Created by Никита on 23.12.2017.
 */

public interface IChangeCategoriesFragmentView extends IFragmentView{
    void onCategoriesChanged();
    void onErrorInCategoriesChanging();

    void updateCategories(List<PersonalCategory> categories);
}
