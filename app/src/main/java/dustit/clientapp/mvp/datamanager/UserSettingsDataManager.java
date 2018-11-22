package dustit.clientapp.mvp.datamanager;

import java.util.Arrays;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.model.repositories.SharedPreferencesRepository;
import dustit.clientapp.utils.managers.ThemeManager;

public class UserSettingsDataManager {

    @Inject
    SharedPreferencesRepository preferencesRepository;

    public UserSettingsDataManager() {
        App.get().getAppComponent().inject(this);
    }

    public void saveNewLanguagePref(String lang) {
        preferencesRepository.saveLanguagePref(lang);
    }

    public String loadLanguage() {
        return preferencesRepository.loadLanguage();
    }

    public void saveTheme(ThemeManager.Theme t) {
        preferencesRepository.saveTheme(t);
    }

    public ThemeManager.Theme loadTheme() {
        return preferencesRepository.loadTheme();
    }

    public boolean isRegistered() {
        return preferencesRepository.isRegistered();
    }

    public void setRegistered(boolean registered) {
        preferencesRepository.setRegistered(registered);
    }

    public boolean isFeedFirstTime() {
        return preferencesRepository.isFeedFirstTime();
    }

    public boolean isAccountFirstTime() {
        return preferencesRepository.isAccountFirstTime();
    }

    public void setFeedVisited() {
        preferencesRepository.setFeedVisited();
    }

    public void setAccountVisited() {
        preferencesRepository.setAccountVisited();
    }

    public void setNotificationsEnabled(boolean enabled) {
        preferencesRepository.setNotificationsEnabled(enabled);
    }

    public boolean isNotificationsEnabled() {
        return preferencesRepository.isNotificationsEnabled();
    }

    public boolean enabledAutoStart() {
        return preferencesRepository.enabledAutoStart();
    }

    public void setEnabledAutostart(boolean enabled) {
        preferencesRepository.setEnabledAutostart(enabled);
    }

    public void saveFcmId(String fcmId) {
        preferencesRepository.saveFcmId(fcmId);
    }

    public boolean isFcmUpdated() {
        return preferencesRepository.isFcmUpdate();
    }

    public void setFcmUpdate(boolean update) {
        preferencesRepository.setFcmUpdate(update);
    }

    public String getFcm() {
        return preferencesRepository.getFcm();
    }
}
