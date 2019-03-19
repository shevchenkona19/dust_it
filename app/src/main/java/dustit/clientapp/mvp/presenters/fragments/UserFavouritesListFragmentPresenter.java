package dustit.clientapp.mvp.presenters.fragments;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.FeedbackManager;
import dustit.clientapp.mvp.model.entities.FavoritesUpperEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IUserFavouritesListFragmentPresenter;
import dustit.clientapp.mvp.ui.interfaces.IUserFavouritesListFragmentView;
import rx.Subscriber;

public class UserFavouritesListFragmentPresenter extends BasePresenter<IUserFavouritesListFragmentView> implements FeedbackManager.IFeedbackInteraction, IUserFavouritesListFragmentPresenter {

    @Inject
    DataManager dataManager;
    @Inject
    FeedbackManager feedbackManager;

    public UserFavouritesListFragmentPresenter() {
        App.get().getAppComponent().inject(this);
        feedbackManager.subscribe(this);
    }

    @Override
    public void loadFavourites(String userId) {
        AtomicReference<FavoritesUpperEntity> atomicReference = new AtomicReference<>();
        addSubscription(dataManager.getAllFavorites(userId).subscribe(new Subscriber<FavoritesUpperEntity>() {
            @Override
            public void onCompleted() {
                getView().onFavouritesLoaded(atomicReference.get().getList());
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                getView().onFavouritesFailed();
            }

            @Override
            public void onNext(FavoritesUpperEntity favoritesUpperEntity) {
                atomicReference.set(favoritesUpperEntity);
            }
        }));
    }

    @Override
    public void changedFeedback(RefreshedMem refreshedMem) {
        getView().onChangedFeedback(refreshedMem);
    }

    @Override
    public void onError(RestoreMemEntity restoreMemEntity) {
        getView().restoreMem(restoreMemEntity);
    }

    @Override
    public void unbind() {
        feedbackManager.unsubscribe(this);
        super.unbind();
    }
}
