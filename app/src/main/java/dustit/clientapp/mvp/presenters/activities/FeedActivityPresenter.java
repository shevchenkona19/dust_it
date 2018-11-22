package dustit.clientapp.mvp.presenters.activities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.model.entities.FavoriteEntity;
import dustit.clientapp.mvp.model.entities.FavoritesUpperEntity;
import dustit.clientapp.mvp.model.entities.MemEntity;
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
        if (dataManager.isUsernameCached()) {
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
    public void loadMemForComments(@Nullable String memId, @Nullable String parentComment, @Nullable String newComment) {
        if (memId != null && parentComment != null && newComment != null) {
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
        } else {
            L.print("NULL FOR LOAD MEM");
        }
    }

    @NotNull
    public String loadId() {
        return dataManager.loadId();
    }
}
