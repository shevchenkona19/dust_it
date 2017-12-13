package dustit.clientapp.mvp.presenters.activities;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IFeedActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IFeedActivityView;

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
    public void postLike(String id) {
        dataManager.postLike(id);
    }
}
