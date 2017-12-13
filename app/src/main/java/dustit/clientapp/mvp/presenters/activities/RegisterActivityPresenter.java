package dustit.clientapp.mvp.presenters.activities;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.RegisterUserEntity;
import dustit.clientapp.mvp.model.entities.TokenEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IRegisterActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IRegisterActivityView;
import rx.Subscriber;

public class RegisterActivityPresenter extends BasePresenter<IRegisterActivityView> implements IRegisterActivityPresenter {

    @Inject
    DataManager dataManager;

    public RegisterActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void registerUser(String username, String password, String email) {
        addSubscription(dataManager.registerUser(new RegisterUserEntity(username, password, email))
                .subscribe(new Subscriber<TokenEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onRegisteredSuccessfully();
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