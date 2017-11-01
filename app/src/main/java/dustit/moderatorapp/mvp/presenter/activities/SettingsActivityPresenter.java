package dustit.moderatorapp.mvp.presenter.activities;

import javax.inject.Inject;

import dustit.moderatorapp.App;
import dustit.moderatorapp.mvp.datamanager.DataManager;
import dustit.moderatorapp.mvp.model.entities.ResponseCode;
import dustit.moderatorapp.mvp.presenter.base.BasePresenter;
import dustit.moderatorapp.mvp.ui.interfaces.ISettingsActivityView;
import rx.Subscriber;

/**
 * Created by Никита on 01.11.2017.
 */

public class SettingsActivityPresenter extends BasePresenter<ISettingsActivityView> {
    @Inject
    DataManager dataManager;

    public SettingsActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    public void logout() {
        dataManager.logout().subscribe(new Subscriber<ResponseCode>() {
            @Override
            public void onCompleted() {
                dataManager.deleteCurrentToken();
                getView().onSuccess();
            }

            @Override
            public void onError(Throwable e) {
                getView().onError();
            }

            @Override
            public void onNext(ResponseCode responseCode) {
                if (responseCode.getCode() != 200) {
                    getView().onError();
                }
            }
        });
    }
}
