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

}
