package dustit.clientapp.mvp.presenters.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.MemUpperEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IFeedFragmentPresenter;
import dustit.clientapp.mvp.ui.interfaces.IFeedFragmentView;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.L;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

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
        final List<MemEntity> list = new ArrayList<>();
        AtomicReference<String> message = new AtomicReference<>("");
        addSubscription(dataManager.getFeed(6, 0)
                .flatMap((Func1<MemUpperEntity, Observable<MemEntity>>) memUpperEntity -> {
                    if (!memUpperEntity.getMessage().equals("")) {
                        message.set(memUpperEntity.getMessage());
                    }
                    return Observable.from(memUpperEntity.getMemEntities());
                })
                .subscribe(new Subscriber<MemEntity>() {
                    @Override
                    public void onCompleted() {
                        if (message.get().equals(IConstants.ErrorCodes.NO_CATEGORIES)) {
                            getView().onNoCategories();
                        } else {
                            getView().onBaseUpdated(list);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print("Error in loading base: " + e.getMessage());
                        getView().onErrorInLoading();
                    }

                    @Override
                    public void onNext(MemEntity memEntity) {
                        list.add(memEntity);
                    }
                }));
    }

    @Override
    public void loadWithOffset(int offset) {
        final List<MemEntity> list = new ArrayList<>();
        addSubscription(dataManager.getFeed(5, offset)
                .flatMap(memUpperEntity -> Observable.from(memUpperEntity.getMemEntities()))
                .subscribe(new Subscriber<MemEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onPartialUpdate(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print("Error in loading partial: " + e.getMessage());
                        getView().onErrorInLoading();
                    }

                    @Override
                    public void onNext(MemEntity memEntity) {
                        list.add(memEntity);
                    }
                }));
    }
}
