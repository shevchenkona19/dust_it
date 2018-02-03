package dustit.clientapp.mvp.presenters.activities;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.ISettingsActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.ISettingsActivityView;
import dustit.clientapp.utils.managers.ThemeManager;

/**
 * Created by shevc on 23.10.2017.
 * Let's GO!
 */

public class SettingsActivityPresenter extends BasePresenter<ISettingsActivityView> implements ISettingsActivityPresenter {
    @Inject
    DataManager dataManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

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

    @Override
    public void saveTheme(ThemeManager.Theme t) {
        userSettingsDataManager.saveTheme(t);
    }

    @Override
    public int loadTheme() {
        final ThemeManager.Theme theme = userSettingsDataManager.loadTheme();
        switch (theme) {
            case DEFAULT:
                return 1;
            case LIGHT:
                return 0;
            case DARK:
                return 2;
            default:
                return -1;
        }
    }
}
