package dustit.clientapp.mvp.ui.interfaces;

public interface IUserFeedbackActivityView extends IActivityView {
    void showLoading();

    void hideLoading();

    void showError(String message);

    void showSuccess();
}
