package dustit.clientapp.mvp.datamanager;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.model.entities.LoginUserEntity;
import dustit.clientapp.mvp.model.entities.RegisterUserEntity;
import dustit.clientapp.mvp.model.entities.TokenEntity;
import dustit.clientapp.mvp.model.repositories.ServerRepository;
import rx.Observable;

/**
 * Created by shevc on 22.09.2017.
 * Let's GO!
 */

public class DataManager {
    @Inject
    ServerRepository serverRepository;

    public DataManager() {
        App.get().getAppComponent().inject(this);
    }

    public Observable<TokenEntity> loginUser(LoginUserEntity userEntity) {
        return serverRepository.loginUser(userEntity);
    }

    public Observable<TokenEntity> registerUser(RegisterUserEntity registerUserEntity) {
        return serverRepository.registerUser(registerUserEntity);
    }
}
