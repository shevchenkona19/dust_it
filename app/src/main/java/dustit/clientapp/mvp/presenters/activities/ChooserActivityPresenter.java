package dustit.clientapp.mvp.presenters.activities;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.LoginUserEntity;
import dustit.clientapp.mvp.model.entities.TokenEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IChooserActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IChooserActivityView;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.L;
import rx.Subscriber;

/**
 * Created by shevc on 23.10.2017.
 * Let's GO!
 */

public class ChooserActivityPresenter extends BasePresenter<IChooserActivityView> implements IChooserActivityPresenter {
    @Inject
    DataManager dataManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    public ChooserActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void checkIfRegistered() {
        if (dataManager.getToken() == null) {
            return;
        }
        if (!dataManager.getToken().equals("")) {
            if (!userSettingsDataManager.isNoRegistration())
                getView().userAlreadyRegistered();
        }
    }

    @Override
    public void continueNoRegistration() {
        L.print("continueNoRegistration");
        userSettingsDataManager.setRegistered(false);
        getView().showLoading();
        addSubscription(dataManager.loginUser(new LoginUserEntity(IConstants.NO_REGISTRATION_USERNAME, IConstants.NO_REGISTRATION_PASSWORD))
                .subscribe(new Subscriber<TokenEntity>() {
                    @Override
                    public void onCompleted() {
                        userSettingsDataManager.setNoRegistration(true);
                        getView().onNoRegistrationCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print("error: " + e.getMessage());
                        getView().hideLoading();
                        getView().onErrorNoRegistration();
                    }

                    @Override
                    public void onNext(TokenEntity tokenEntity) {
                        dataManager.saveToken(tokenEntity.getToken());
                    }
                }));
    }
}
