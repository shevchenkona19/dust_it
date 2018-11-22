package dustit.clientapp.mvp.presenters.activities;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.LoginUserEntity;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.model.entities.TokenEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.ILoginActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.ILoginActivityView;
import rx.Subscriber;

/**
 * Created by shevc on 22.09.2017.
 * Let's GO!
 */

public class LoginActivityPresenter extends BasePresenter<ILoginActivityView> implements ILoginActivityPresenter {
    @Inject
    DataManager dataManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    public LoginActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void loginUser(String username, String password) {
        AtomicReference<TokenEntity> token = new AtomicReference<>();
        addSubscription(dataManager.loginUser(new LoginUserEntity(username, password))
                .subscribe(new Subscriber<TokenEntity>() {
                    @Override
                    public void onCompleted() {
                        TokenEntity tokenEntity = token.get();
                        if (tokenEntity.getMessage().equals("")) {
                            dataManager.saveId(tokenEntity.getId());
                            dataManager.saveToken(tokenEntity.getToken());
                            userSettingsDataManager.setRegistered(true);
                            setFcmForUser();
                            getView().onLoggedSuccessfully();
                        } else {
                            getView().onError(tokenEntity.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onError(e.getMessage());
                    }

                    @Override
                    public void onNext(TokenEntity tokenEntity) {
                        token.set(tokenEntity);
                    }
                }));
    }

    private void setFcmForUser() {
        if (!userSettingsDataManager.getFcm().equals("")) {
            AtomicReference<ResponseEntity> reference = new AtomicReference<>();
            addSubscription(dataManager.setFcmId(userSettingsDataManager.getFcm()).subscribe(new Subscriber<ResponseEntity>() {
                @Override
                public void onCompleted() {
                    if (reference.get().getResponse() == 200) {
                        userSettingsDataManager.setFcmUpdate(true);
                    } else {
                        userSettingsDataManager.setFcmUpdate(false);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    userSettingsDataManager.setFcmUpdate(false);
                }

                @Override
                public void onNext(ResponseEntity responseEntity) {
                    reference.set(responseEntity);
                }
            }));
        }
    }
}
