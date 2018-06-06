package dustit.clientapp.mvp.presenters.activities;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.model.entities.FavoriteEntity;
import dustit.clientapp.mvp.model.entities.FavoritesUpperEntity;
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

    public FeedActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void getMyUsername() {
        if (dataManager.isUsernameCached()) {
            getView().onUsernameArrived(dataManager.getCachedUsername());
        } else {
            final Container<String> container = new Container<>();
            addSubscription(dataManager.getMyUsername().subscribe(new Subscriber<UsernameEntity>() {
                @Override
                public void onCompleted() {
                    getView().onUsernameArrived(container.get());
                }

                @Override
                public void onError(Throwable e) {
                    L.print("GEtmyusername err " + e.getMessage());
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
            }

            @Override
            public void onNext(Category category) {
                categories.add(category);
            }
        }));
    }
}
