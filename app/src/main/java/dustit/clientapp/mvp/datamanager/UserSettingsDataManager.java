package dustit.clientapp.mvp.datamanager;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.model.repositories.SharedPreferencesRepository;

/**
 * Created by nikita on 27.01.18.
 */

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
}
