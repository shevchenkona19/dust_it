package dustit.clientapp.mvp.presenters.activities;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.model.entities.CategoryIdEntity;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IResultActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IResultActivityView;
import dustit.clientapp.utils.L;
import rx.Subscriber;

/**
 * Created by Никита on 09.11.2017.
 */

public class ResultActivityPresenter extends BasePresenter<IResultActivityView> implements IResultActivityPresenter {
    @Inject
    DataManager dataManager;

    public ResultActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void toMemes(String categoryIds) {
        addSubscription(dataManager.postPersonalCategories(new CategoryIdEntity(categoryIds))
                .subscribe(new Subscriber<ResponseEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onFinishedResultActivity();
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print(e.getMessage());
                        getView().onFailedToSendCategories();
                    }

                    @Override
                    public void onNext(ResponseEntity responseEntity) {
                        if (responseEntity.getResponse() != 200) {
                            getView().onFailedToSendCategories();
                        }
                    }
                }));
    }

    @Override
    public void loadCategories() {
        final List<Category> list = new ArrayList<>();
        addSubscription(dataManager.getCategories()
                .subscribe(new Subscriber<Category>() {
                    @Override
                    public void onCompleted() {
                        getView().onCategoriesLoaded(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print(e.getMessage());
                        getView().onErrorInLoadingCategories();
                    }

                    @Override
                    public void onNext(Category category) {
                        list.add(category);
                    }
                }));
    }
}
