package dustit.clientapp.mvp.presenters.activities;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.LoginUserEntity;
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

    public LoginActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }
    @Override
    public void loginUser(String username, String password) {
        addSubscription(dataManager.loginUser(new LoginUserEntity(username, password))
                .subscribe(new Subscriber<TokenEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onLoggedSuccessfully();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onError(e.getMessage());
                    }

                    @Override
                    public void onNext(TokenEntity tokenEntity) {
                        dataManager.saveToken(tokenEntity.getToken());
                    }
                }));
    }
}