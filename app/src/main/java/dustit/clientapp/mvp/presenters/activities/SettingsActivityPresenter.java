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
        dataManager.saveToken("");
        dataManager.setFcmId("");
        userSettingsDataManager.setRegistered(false);
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
            case LIGHT:
                return 0;
            case NIGHT:
                return 1;
            default:
                return -1;
        }
    }
}
