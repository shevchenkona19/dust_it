package dustit.clientapp.mvp.presenters.interfaces;

import dustit.clientapp.utils.managers.ThemeManager;

/**
 * Created by shevc on 23.10.2017.
 * Let's GO!
 */

public interface ISettingsActivityPresenter {
    void logout();
    void saveTheme(ThemeManager.Theme t);
    int loadTheme();
}
