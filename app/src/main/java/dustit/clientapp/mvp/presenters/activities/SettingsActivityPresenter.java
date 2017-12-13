package dustit.clientapp.mvp.presenters.activities;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.ISettingsActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.ISettingsActivityView;

/**
 * Created by shevc on 23.10.2017.
 * Let's GO!
 */

public class SettingsActivityPresenter extends BasePresenter<ISettingsActivityView> implements ISettingsActivityPresenter {
    @Inject
    DataManager dataManager;

    public SettingsActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void logout() {
        /*addSubscription(dataManager.logout()
                .subscribe(new Subscriber<ResponseEntity>() {
                    @Override
                    public void onCompleted() {
                        dataManager.saveToken("");
                        getView().onSuccessfullyLogout();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onErrorLogout(e.getMessage());
                    }

                    @Override
                    public void onNext(ResponseEntity responseEntity) {
                        if (responseEntity.getResponse()!=200) {
                            getView().onErrorLogout("Error");
                        }
                    }
                }));*/
        dataManager.saveToken("");
        getView().onSuccessfullyLogout();
    }
}
