package dustit.clientapp.mvp.presenters.fragments;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.PersonalCategory;
import dustit.clientapp.mvp.model.entities.PostSelectedCategoriesUpperEntity;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.model.entities.SelectedCategoriesEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IChangeCategoriesFragmentPresenter;
import dustit.clientapp.mvp.ui.interfaces.IChangeCategoriesFragmentView;
import dustit.clientapp.utils.L;
import rx.Subscriber;

/**
 * Created by Никита on 23.12.2017.
 */

public class ChangeCategoriesFragmentPresenter extends BasePresenter<IChangeCategoriesFragmentView> implements IChangeCategoriesFragmentPresenter {
    @Inject
    DataManager dataManager;

    public ChangeCategoriesFragmentPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void getCategories() {
        final List<PersonalCategory> list =new ArrayList<>();
        addSubscription(dataManager.getPersonalCategories().subscribe(new Subscriber<PersonalCategory>() {
            @Override
            public void onCompleted() {
                getView().updateCategories(list);
            }

            @Override
            public void onError(Throwable e) {
                getView().onErrorInCategoriesChanging();
            }

            @Override
            public void onNext(PersonalCategory personalCategory) {
                list.add(personalCategory);
            }
        }));
    }

    @Override
    public void sendCategories(String[] ids) {
        final PostSelectedCategoriesUpperEntity entity = new PostSelectedCategoriesUpperEntity(ids);
        addSubscription(dataManager.postPersonalCategories(entity).subscribe(new Subscriber<ResponseEntity>() {
            @Override
            public void onCompleted() {
                getView().onCategoriesChanged();
            }

            @Override
            public void onError(Throwable e) {
                L.print(e.getMessage());
                getView().onErrorInCategoriesChanging();
            }

            @Override
            public void onNext(ResponseEntity responseEntity) {
                if (responseEntity.getResponse() !=200) {
                    getView().onErrorInCategoriesChanging();
                }
            }
        }));
    }
}
