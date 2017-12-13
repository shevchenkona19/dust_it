package dustit.clientapp.mvp.ui.interfaces;

/**
 * Created by shevc on 23.10.2017.
 * Let's GO!
 */

public interface ISettingsActivityView extends IActivityView {
    void onErrorLogout(String message);

    void onSuccessfullyLogout();
}
