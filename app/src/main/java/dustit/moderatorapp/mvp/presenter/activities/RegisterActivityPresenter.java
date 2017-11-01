package dustit.moderatorapp.mvp.presenter.activities;

import android.util.Log;

import javax.inject.Inject;

import dustit.moderatorapp.App;
import dustit.moderatorapp.mvp.datamanager.DataManager;
import dustit.moderatorapp.mvp.model.entities.TokenEntity;
import dustit.moderatorapp.mvp.model.entities.UserEntity;
import dustit.moderatorapp.mvp.presenter.base.BasePresenter;
import dustit.moderatorapp.mvp.presenter.interfaces.IRegisterActivityPresenter;
import dustit.moderatorapp.mvp.ui.interfaces.IRegisterActivityView;
import rx.Subscriber;

/**
 * Created by shevc on 15.09.2017.
 * Let's GO!
 */

public class RegisterActivityPresenter extends BasePresenter<IRegisterActivityView> implements IRegisterActivityPresenter {
    @Inject
    DataManager dataManager;

    public RegisterActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }
    @Override
    public void register(String username, String password) {
        dataManager.registerUser(new UserEntity(username, password))
                .subscribe(new Subscriber<TokenEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onRegistered();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onError();
                        Log.d("MY", e.getMessage());
                    }

                    @Override
                    public void onNext(TokenEntity tokenEntity) {
                        dataManager.saveToken(tokenEntity.getToken());
                    }
                });
    }
}
