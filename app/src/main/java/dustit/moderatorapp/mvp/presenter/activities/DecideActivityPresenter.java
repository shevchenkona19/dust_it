package dustit.moderatorapp.mvp.presenter.activities;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dustit.moderatorapp.App;
import dustit.moderatorapp.mvp.datamanager.DataManager;
import dustit.moderatorapp.mvp.model.entities.CategoriesIdEntity;
import dustit.moderatorapp.mvp.model.entities.Category;
import dustit.moderatorapp.mvp.model.entities.MemIdEntity;
import dustit.moderatorapp.mvp.model.entities.ResponseCode;
import dustit.moderatorapp.mvp.presenter.base.BasePresenter;
import dustit.moderatorapp.mvp.presenter.interfaces.IDecideActivityPresenter;
import dustit.moderatorapp.mvp.ui.interfaces.IDecideActivityView;
import rx.Subscriber;

public class DecideActivityPresenter extends BasePresenter<IDecideActivityView> implements IDecideActivityPresenter {
    @Inject
    DataManager dataManager;

    public DecideActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void getCategories() {
        final List<Category> categories = new ArrayList<>();
        dataManager.getCategories()
                .subscribe(new Subscriber<Category>() {
                    @Override
                    public void onCompleted() {
                        getView().onCategoriesLoaded(categories);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onErrorInLoadingCategories();
                    }

                    @Override
                    public void onNext(Category category) {
                        categories.add(category);
                    }
                });
    }

    @Override
    public void postMem(String id, CategoriesIdEntity entity) {
        dataManager.postMem(id, entity)
                .subscribe(new Subscriber<ResponseCode>() {
                    @Override
                    public void onCompleted() {
                        getView().onMemPosted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onErrorPostingMem();
                    }

                    @Override
                    public void onNext(ResponseCode responseCode) {
                        if (isError(responseCode.getCode())) {
                            getView().onErrorPostingMem();
                        }
                    }
                });
    }

    @Override
    public void getNewMem() {
        final MemIdEntity[] memEntity = new MemIdEntity[1];
        dataManager.getNewMem()
                .subscribe(new Subscriber<MemIdEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onNewMemArrived(memEntity[0]);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onErrorInGettingNewMem();
                    }

                    @Override
                    public void onNext(MemIdEntity memIdEntity) {
                        memEntity[0] = memIdEntity;
                    }
                });
    }

    @Override
    public void discardMem(String id) {
        dataManager.discardMem(id)
                .subscribe(new Subscriber<ResponseCode>() {
                    @Override
                    public void onCompleted() {
                        getView().onMemDiscarded();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onErrorInDiscardingMem();
                    }

                    @Override
                    public void onNext(ResponseCode responseCode) {
                        if (isError(responseCode.getCode())) {
                            getView().onErrorInDiscardingMem();
                        }
                    }
                });
    }

    public String getToken() {
        return dataManager.getToken();
    }

    private boolean isError(int code) {
        return code != 200;
    }
}
