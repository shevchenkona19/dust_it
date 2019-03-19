package dustit.clientapp.mvp.presenters.fragments;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.ICategoriesStepFragmentPresenter;
import dustit.clientapp.mvp.ui.interfaces.ICategoriesStepFragment;
import rx.Subscriber;

public class CategoriesStepFragmentPresenter extends BasePresenter<ICategoriesStepFragment> implements ICategoriesStepFragmentPresenter {

    @Inject
    DataManager dataManager;

    public CategoriesStepFragmentPresenter() {
        App.get().getAppComponent().inject(this);
    }


    @Override
    public void loadCategories() {
        List<Category> list = new ArrayList<>();

        addSubscription(dataManager.getCategories().subscribe(new Subscriber<Category>() {
            @Override
            public void onCompleted() {
                getView().onCategoriesArrived(list);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                getView().onCategoriesFailedToLoad();
            }

            @Override
            public void onNext(Category category) {
                list.add(category);
            }
        }));
    }
}
