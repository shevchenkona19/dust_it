package dustit.moderatorapp.mvp.presenter.activities;

import javax.inject.Inject;

import dustit.moderatorapp.App;
import dustit.moderatorapp.mvp.datamanager.DataManager;
import dustit.moderatorapp.mvp.presenter.base.BasePresenter;
import dustit.moderatorapp.mvp.presenter.interfaces.IChooserActivityPresenter;
import dustit.moderatorapp.mvp.ui.interfaces.IChooserActivityView;

/**
 * Created by shevc on 17.09.2017.
 * Let's GO!
 */

public class ChooserActivityPresenter extends BasePresenter<IChooserActivityView> implements IChooserActivityPresenter {
    @Inject
    DataManager dataManager;

    public ChooserActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public boolean isFirstTime() {
        return dataManager.isFirstTime();
    }
}
