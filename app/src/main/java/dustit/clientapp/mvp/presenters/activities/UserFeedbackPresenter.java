package dustit.clientapp.mvp.presenters.activities;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.NewResponseEntity;
import dustit.clientapp.mvp.model.entities.UserFeedbackEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IUserFeedbackPresenter;
import dustit.clientapp.mvp.ui.interfaces.IUserFeedbackActivityView;
import dustit.clientapp.utils.L;
import rx.Subscriber;

public class UserFeedbackPresenter extends BasePresenter<IUserFeedbackActivityView> implements IUserFeedbackPresenter {

    @Inject
    DataManager dataManager;

    public UserFeedbackPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void sendFeedback(String title, String message) {
        getView().showLoading();
        AtomicReference<NewResponseEntity> res = new AtomicReference<>();
        addSubscription(dataManager.postUserFeedback(new UserFeedbackEntity(title, message)).subscribe(new Subscriber<NewResponseEntity>() {
            @Override
            public void onCompleted() {
                getView().hideLoading();
                if (res.get().isSuccess()) {
                    getView().showSuccess();
                } else {
                    getView().showError(res.get().getMessage());
                }
            }

            @Override
            public void onError(Throwable e) {
                getView().hideLoading();
                L.print("Error on post user feedback", e.getMessage());
                getView().showError(e.getMessage());
            }

            @Override
            public void onNext(NewResponseEntity newResponseEntity) {
                res.set(newResponseEntity);
            }
        }));
    }
}
