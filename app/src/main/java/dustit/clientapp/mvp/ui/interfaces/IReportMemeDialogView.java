package dustit.clientapp.mvp.ui.interfaces;

public interface IReportMemeDialogView extends IView {
    void onStartLoading();
    void onFailedToReport();
    void onReported();

    void onFailedToReport(String error);
}
