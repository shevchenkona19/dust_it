package dustit.clientapp.mvp.presenters.activities;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.RegisterUserEntity;
import dustit.clientapp.mvp.model.entities.TokenEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IRegisterActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IRegisterActivityView;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.L;
import rx.Subscriber;

public class RegisterActivityPresenter extends BasePresenter<IRegisterActivityView> implements IRegisterActivityPresenter {

    @Inject
    DataManager dataManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    public RegisterActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void registerUser(String username, String password, String email) {
        AtomicReference<TokenEntity> token = new AtomicReference<>();
        addSubscription(dataManager.registerUser(new RegisterUserEntity(username, password, email))
                .subscribe(new Subscriber<TokenEntity>() {
                    @Override
                    public void onCompleted() {
                        TokenEntity tokenEntity = token.get();
                        if (tokenEntity.getMessage().equals("")) {
                            dataManager.saveToken(tokenEntity.getToken());
                            dataManager.saveId(tokenEntity.getId());
                            userSettingsDataManager.setRegistered(true);
                            getView().onRegisteredSuccessfully();
                        } else {
                            getView().onError(tokenEntity.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print("----------------------------------------------------, " + e.getMessage());
                        getView().onError(e.getMessage());
                    }

                    @Override
                    public void onNext(TokenEntity tokenEntity) {
                        token.set(tokenEntity);
                    }
                }));
    }
}