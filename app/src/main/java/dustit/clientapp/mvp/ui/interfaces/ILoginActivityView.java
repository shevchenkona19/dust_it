package dustit.clientapp.mvp.ui.interfaces;

/**
 * Created by shevc on 22.09.2017.
 * Let's GO!
 */

public interface ILoginActivityView extends IActivityView {
    void onLoggedSuccessfully();
    void onError(String message);
}
