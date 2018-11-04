package dustit.clientapp.mvp.presenters.fragments;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.MemUpperEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.ICategoriesFragmentPresenter;
import dustit.clientapp.mvp.ui.interfaces.ICategoriesFragmentView;
import rx.Subscriber;

public class CategoriesFragmentPresenter extends BasePresenter<ICategoriesFragmentView> implements ICategoriesFragmentPresenter {
    @Inject
    DataManager dataManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    public CategoriesFragmentPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void loadBase(String categoryId) {
        AtomicReference<MemUpperEntity> atomicReference = new AtomicReference<>();
        addSubscription(dataManager.getCategoriesFeed(categoryId, 6, 0)
                .subscribe(new Subscriber<MemUpperEntity>() {
                    @Override
                    public void onCompleted() {
                        if (atomicReference.get() != null) {
                            MemUpperEntity mem = atomicReference.get();
                            getView().onBaseUpdated(mem.getMemEntities());
                            if (mem.isAchievementUpdate()) {
                                getView().onAchievementUpdate(mem.getAchievementEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onErrorInLoading();
                    }

                    @Override
                    public void onNext(MemUpperEntity memUpperEntity) {
                        atomicReference.set(memUpperEntity);
                    }
                }));
    }

    @Override
    public void loadWithOffset(String categoryId, int offset) {
        AtomicReference<MemUpperEntity> atomicReference = new AtomicReference<>();
        addSubscription(dataManager.getCategoriesFeed(categoryId, 6, offset)
                .subscribe(new Subscriber<MemUpperEntity>() {
                    @Override
                    public void onCompleted() {
                        if (atomicReference.get() != null) {
                            MemUpperEntity mem = atomicReference.get();
                            getView().onPartialUpdate(mem.getMemEntities());
                            if (mem.isAchievementUpdate()) {
                                getView().onAchievementUpdate(mem.getAchievementEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onErrorInLoading();
                    }

                    @Override
                    public void onNext(MemUpperEntity memUpperEntity) {
                        atomicReference.set(memUpperEntity);
                    }
                }));
    }

    public boolean isRegistered() {
        return userSettingsDataManager.isRegistered();
    }
}
