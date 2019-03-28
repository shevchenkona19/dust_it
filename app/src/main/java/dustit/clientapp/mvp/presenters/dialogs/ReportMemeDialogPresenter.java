package dustit.clientapp.mvp.presenters.dialogs;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.ReportEntity;
import dustit.clientapp.mvp.model.entities.SimpleResponseEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IReportMemeDialogPresenter;
import dustit.clientapp.mvp.ui.interfaces.IReportMemeDialogView;
import dustit.clientapp.utils.L;
import rx.Subscriber;

public class ReportMemeDialogPresenter extends BasePresenter<IReportMemeDialogView> implements IReportMemeDialogPresenter {
    @Inject
    DataManager dataManager;

    public ReportMemeDialogPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void reportMeme(String reportReason, MemEntity mem) {
        getView().onStartLoading();
        ReportEntity report = new ReportEntity();
        report.setReportReason(reportReason);
        report.setImageId(mem.getId());
        AtomicReference<SimpleResponseEntity> res = new AtomicReference<>();
        addSubscription(dataManager.reportMeme(report).subscribe(new Subscriber<SimpleResponseEntity>() {
            @Override
            public void onCompleted() {
                SimpleResponseEntity response = res.get();
                if (response != null) {
                    if (response.isSuccess()) {
                        getView().onReported();
                    } else {
                        getView().onFailedToReport(response.getError());
                    }
                    return;
                }
                getView().onFailedToReport();
            }

            @Override
            public void onError(Throwable e) {
                L.print("failed to send report: " + e.getMessage());
                getView().onFailedToReport();
            }

            @Override
            public void onNext(SimpleResponseEntity simpleResponseEntity) {
                res.set(simpleResponseEntity);
            }
        }));
    }
}
