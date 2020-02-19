package dustit.clientapp.mvp.presenters.activities;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.UserSearchResponseEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.ISearchActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.ISearchActivityView;
import dustit.clientapp.utils.L;
import rx.Subscriber;

public class SearchActivityPresenter extends BasePresenter<ISearchActivityView> implements ISearchActivityPresenter {

    @Inject
    DataManager dataManager;

    public SearchActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void searchUsers(String query) {
        getView().onStartLoading();
        AtomicReference<UserSearchResponseEntity> response = new AtomicReference<>();
        addSubscription(dataManager.searchUsers(query).subscribe(new Subscriber<UserSearchResponseEntity>() {
            @Override
            public void onCompleted() {
                UserSearchResponseEntity res = response.get();
                if (res != null) {
                    if (res.isSuccess()) {
                        L.print("isSuccess");
                        getView().onSearchResultsArrived(res.getUsers());
                        return;
                    }
                }
                L.print("is not");
                getView().onSearchResultsArrived(new ArrayList<>(0));
            }

            @Override
            public void onError(Throwable e) {
                L.print("Error searching users: " + e.getMessage());
                getView().onError();
            }

            @Override
            public void onNext(UserSearchResponseEntity userSearchResponseEntity) {
                response.set(userSearchResponseEntity);
            }
        }));
    }
}
