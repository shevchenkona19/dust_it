package dustit.clientapp.mvp.ui.interfaces;

import java.util.List;

import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.model.entities.FavoriteEntity;

/**
 * Created by shevc on 07.10.2017.
 * Let's GO!
 */

public interface IFeedActivityView extends IActivityView {
    void onError();

    void onUsernameArrived(String s);

    void onCategoriesArrived(List<Category> categoryList);

    void onCategoriesFailedToLoad();

}
