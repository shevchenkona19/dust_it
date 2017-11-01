package dustit.moderatorapp.mvp.ui.interfaces;

import java.util.List;

import dustit.moderatorapp.mvp.model.entities.Category;
import dustit.moderatorapp.mvp.model.entities.MemIdEntity;

/**
 * Created by Никита on 01.11.2017.
 */

public interface IDecideActivityView extends IActivityView {
    void onCategoriesLoaded(List<Category> categories);

    void onErrorInLoadingCategories();

    void onMemPosted();

    void onErrorPostingMem();

    void onNewMemArrived(MemIdEntity memIdEntity);

    void onErrorInGettingNewMem();

    void onMemDiscarded();

    void onErrorInDiscardingMem();
}
