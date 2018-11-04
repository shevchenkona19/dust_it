package dustit.clientapp.mvp.presenters.fragments;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.MemUpperEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IFeedFragmentPresenter;
import dustit.clientapp.mvp.ui.interfaces.IFeedFragmentView;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.L;
import rx.Subscriber;

/**
 * Created by shevc on 05.10.2017.
 * Let's GO!
 */

public class FeedFragmentPresenter extends BasePresenter<IFeedFragmentView> implements IFeedFragmentPresenter {
    @Inject
    DataManager dataManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    public FeedFragmentPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void loadBase() {
        AtomicReference<MemUpperEntity> atomicReference = new AtomicReference<>();
        addSubscription(dataManager.getFeed(6, 0)
                .subscribe(new Subscriber<MemUpperEntity>() {
                    @Override
                    public void onCompleted() {
                        MemUpperEntity mem = atomicReference.get();
                        if (mem != null) {
                            String message = mem.getMessage();
                            if (message.equals(IConstants.ErrorCodes.NO_CATEGORIES)) {
                                getView().onNoCategories();
                            } else {
                                getView().onBaseUpdated(mem.getMemEntities());
                                if (mem.isAchievementUpdate()) {
                                    getView().onAchievementUpdate(mem.getAchievementEntity());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print("Error in loading base: " + e.getMessage());
                        getView().onErrorInLoading();
                    }

                    @Override
                    public void onNext(MemUpperEntity memEntity) {
                        atomicReference.set(memEntity);
                    }
                }));
    }

    @Override
    public void loadWithOffset(int offset) {
        AtomicReference<MemUpperEntity> atomicReference = new AtomicReference<>();
        addSubscription(dataManager.getFeed(5, offset)
                .subscribe(new Subscriber<MemUpperEntity>() {
                    @Override
                    public void onCompleted() {
                        MemUpperEntity mem = atomicReference.get();
                        if (mem != null) {
                            getView().onPartialUpdate(mem.getMemEntities());
                            if (mem.isAchievementUpdate()) {
                                getView().onAchievementUpdate(mem.getAchievementEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print("Error in loading partial: " + e.getMessage());
                        getView().onErrorInLoading();
                    }

                    @Override
                    public void onNext(MemUpperEntity memEntity) {
                        atomicReference.set(memEntity);
                    }
                }));
    }

    public boolean isRegistered() {
        return userSettingsDataManager.isRegistered();
    }
}
