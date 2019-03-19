package dustit.clientapp.mvp.presenters.interfaces;

/**
 * Created by shevc on 22.09.2017.
 * Let's GO!
 */

public interface IRegisterActivityPresenter {
    void onRegisterPressed();
    void registerUser(String username, String password, String email, String referralCode);

    void showReferralDialog();

}
