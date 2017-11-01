package dustit.moderatorapp.mvp.presenter.interfaces;

import dustit.moderatorapp.mvp.model.entities.CategoriesIdEntity;

/**
 * Created by Никита on 01.11.2017.
 */

public interface IDecideActivityPresenter {
    void getCategories();

    void postMem(String id, CategoriesIdEntity entity);

    void getNewMem();

    void discardMem(String id);
}
