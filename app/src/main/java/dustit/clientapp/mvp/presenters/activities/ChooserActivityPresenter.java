package dustit.clientapp.mvp.presenters.activities;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IChooserActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IChooserActivityView;

/**
 * Created by shevc on 23.10.2017.
 * Let's GO!
 */

public class ChooserActivityPresenter extends BasePresenter<IChooserActivityView> implements IChooserActivityPresenter {
    @Inject
    DataManager dataManager;

    public ChooserActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }
    @Override
    public void checkIfRegistered() {
        if (dataManager.getToken() == null) {
            return;
        }
        if (!dataManager.getToken().equals("")) {
            getView().userAlreadyRegistered();
        }
    }
}
