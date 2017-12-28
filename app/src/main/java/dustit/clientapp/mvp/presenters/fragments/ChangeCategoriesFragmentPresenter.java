package dustit.clientapp.mvp.presenters.fragments;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.model.entities.CategoryIdEntity;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
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
        final List<Category> listOfAll = new ArrayList<>();
        final List<Category> listOfChecked = new ArrayList<>();
        addSubscription(dataManager.getCategories()
                .subscribe(new Subscriber<Category>() {
                    @Override
                    public void onCompleted() {
                        addSubscription(dataManager
                                .getPersonalCategories()
                                .subscribe(new Subscriber<CategoryIdEntity>() {
                                    @Override
                                    public void onCompleted() {
                                        getView().updateCategories(listOfChecked);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        L.print(e.getMessage());
                                        getView().onErrorInCategoriesChanging();
                                    }

                                    @Override
                                    public void onNext(CategoryIdEntity entity) {
                                        String ids = entity.getIds();
                                        if (ids.charAt(0) == ' ')
                                            ids = ids.substring(1, ids.length() - 1);
                                        if (ids.charAt(ids.length() - 1) == ' ') {
                                            ids = ids.substring(0, ids.length() - 2);
                                        }
                                        if (ids.contains(" ")) {
                                            String[] separatedIds = ids.split(" ");
                                            for (Category c :
                                                    listOfAll) {
                                                for (String s :
                                                        separatedIds) {
                                                    if (c.getId().equals(s)) {
                                                        c.setChecked(true);
                                                        break;
                                                    }
                                                }
                                            }
                                            listOfChecked.addAll(listOfAll);
                                        } else {
                                            for (Category c : listOfAll) {
                                                if (c.getId().equals(ids)) {
                                                    c.setChecked(true);
                                                    break;
                                                }
                                            }
                                            listOfChecked.addAll(listOfAll);
                                        }
                                    }
                                }));
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print(e.getMessage());
                        getView().onErrorInCategoriesChanging();
                    }

                    @Override
                    public void onNext(Category category) {
                        listOfAll.add(category);
                    }
                }));

    }

    @Override
    public void sendCategories(String ids) {
        addSubscription(dataManager.postPersonalCategories(new CategoryIdEntity(ids)).subscribe(new Subscriber<ResponseEntity>() {
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
