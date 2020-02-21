package dustit.clientapp.mvp.presenters.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.model.entities.UsernameEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IFeedActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IFeedActivityView;
import dustit.clientapp.utils.L;
import dustit.clientapp.utils.containers.Container;
import rx.Subscriber;

/**
 * Created by shevc on 07.10.2017.
 * Let's GO!
 */

public class FeedActivityPresenter extends BasePresenter<IFeedActivityView> implements IFeedActivityPresenter {
    @Inject
    DataManager dataManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    public FeedActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void getMyUsername() {
        if (dataManager.isUsernameCached() && !dataManager.getCachedUsername().equals("")) {
            getView().onUsernameArrived(dataManager.getCachedUsername());
        } else {
            final Container<String> container = new Container<>();
            addSubscription(dataManager.getUsername(dataManager.loadId()).subscribe(new Subscriber<UsernameEntity>() {
                @Override
                public void onCompleted() {
                    getView().onUsernameArrived(container.get());
                }

                @Override
                public void onError(Throwable e) {
                    L.print("GetMyUsername err " + e.getMessage());
                    getView().onError();
                }

                @Override
                public void onNext(UsernameEntity usernameEntity) {
                    dataManager.cacheUsername(usernameEntity.getUsername());
                    container.put(usernameEntity.getUsername());
                }
            }));
        }
    }

    @Override
    public void getCategories() {
        final List<Category> categories = new ArrayList<>();
        addSubscription(dataManager.getCategories().subscribe(new Subscriber<Category>() {
            @Override
            public void onCompleted() {
                getView().onCategoriesArrived(categories);
            }

            @Override
            public void onError(Throwable e) {
                L.print(e.getMessage());
                getView().onCategoriesFailedToLoad();
            }

            @Override
            public void onNext(Category category) {
                categories.add(category);
            }
        }));
    }

    public boolean isRegistered() {
        return userSettingsDataManager.isRegistered();
    }

    @Override
    public void setFeedVisited() {
        userSettingsDataManager.setFeedVisited();
    }

    @Override
    public boolean isFeedFirstTime() {
        return userSettingsDataManager.isFeedFirstTime();
    }

    @Override
    public void loadMemForComments(int memId, int parentComment, int newComment) {
        L.print("MemId: " + memId);
        L.print("parentComment: " + parentComment);
        L.print("newComment: " + newComment);
        AtomicReference<MemEntity> reference = new AtomicReference<>();
        addSubscription(dataManager.getMemById(memId).subscribe(new Subscriber<MemEntity>() {
            @Override
            public void onCompleted() {
                getView().onMemReadyForComments(reference.get(), parentComment, newComment);
            }

            @Override
            public void onError(Throwable e) {
                L.print("Error for loading mem: " + e.getMessage());
            }

            @Override
            public void onNext(MemEntity memEntity) {
                reference.set(memEntity);
            }
        }));
    }

    public void updateFcmId() {
        String fcmId = userSettingsDataManager.getFcm();
        final boolean[] isError = {false};
        addSubscription(dataManager.setFcmId(fcmId).subscribe(new Subscriber<ResponseEntity>() {
            @Override
            public void onCompleted() {
                if (!isError[0]) {
                    userSettingsDataManager.onFcmUpdated();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseEntity responseEntity) {
                if (responseEntity.getResponse() != 200) {
                    isError[0] = true;
                }
            }
        }));
    }

    public int loadId() {
        return dataManager.loadId();
    }
}
