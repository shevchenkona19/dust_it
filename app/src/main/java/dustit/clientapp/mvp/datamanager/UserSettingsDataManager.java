package dustit.clientapp.mvp.datamanager;

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

    public void setFeedVisited() {
        preferencesRepository.setFeedVisited();
    }

    public boolean isNotificationsEnabled() {
        return preferencesRepository.isNotificationsEnabled();
    }

    public void setNotificationsEnabled(boolean enabled) {
        preferencesRepository.setNotificationsEnabled(enabled);
    }

    public void saveFcmId(String fcmId) {
        preferencesRepository.saveFcmId(fcmId);
    }

    public String getFcm() {
        return preferencesRepository.getFcm();
    }

    public boolean isNoRegistration() {
        return preferencesRepository.isNoRegistration();
    }

    public void setNoRegistration(boolean noRegistration) {
        preferencesRepository.setNoRegistration(noRegistration);
    }

    public void scheduleTokenUpdate() {
        preferencesRepository.scheduleTokenUpdate();
    }

    public void onFcmUpdated() {
        preferencesRepository.onFcmUpdated();
    }

    public boolean shouldFcmUpdate() {
        return preferencesRepository.shouldFcmUpdate();
    }


}
