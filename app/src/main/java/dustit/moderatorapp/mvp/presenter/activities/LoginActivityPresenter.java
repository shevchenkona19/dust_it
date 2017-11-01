package dustit.moderatorapp.mvp.presenter.activities;

import android.util.Log;

import javax.inject.Inject;

import dustit.moderatorapp.App;
import dustit.moderatorapp.mvp.datamanager.DataManager;
import dustit.moderatorapp.mvp.model.entities.LoginUserEntity;
import dustit.moderatorapp.mvp.model.entities.TokenEntity;
import dustit.moderatorapp.mvp.presenter.base.BasePresenter;
import dustit.moderatorapp.mvp.presenter.interfaces.ILoginActivityPresenter;
import dustit.moderatorapp.mvp.ui.interfaces.ILoginActivityView;
import rx.Subscriber;

/**
 * Created by shevc on 17.09.2017.
 * Let's GO!
 */

public class LoginActivityPresenter extends BasePresenter<ILoginActivityView> implements ILoginActivityPresenter {
    @Inject
    DataManager dataManager;

    public LoginActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void login(String username, String password) {
        dataManager.loginUser(new LoginUserEntity(username, password))
                .subscribe(new Subscriber<TokenEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onLoggedSuccessfully();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("MY", "Error in request" + e.getMessage());
                    }

                    @Override
                    public void onNext(TokenEntity tokenEntity) {
                        dataManager.saveToken(tokenEntity.getToken());
                    }
                });
    }
}
