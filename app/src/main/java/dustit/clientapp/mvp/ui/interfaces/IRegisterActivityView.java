package dustit.clientapp.mvp.ui.interfaces;

/**
 * Created by shevc on 22.09.2017.
 * Let's GO!
 */

public interface IRegisterActivityView extends IActivityView{
    void onRegisteredSuccessfully();
    void onError(String message);

    void showReferralPrompt();

    void showReferralCodeInputDialog();

}
