package dustit.moderatorapp.mvp.datamanager;

import android.util.Log;

import javax.inject.Inject;

import dustit.moderatorapp.App;
import dustit.moderatorapp.mvp.model.entities.LoginUserEntity;
import dustit.moderatorapp.mvp.model.entities.TokenEntity;
import dustit.moderatorapp.mvp.model.entities.UserEntity;
import dustit.moderatorapp.mvp.model.repositories.ServerRepository;
import dustit.moderatorapp.mvp.model.repositories.SharedPreferencesRepository;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by shevc on 15.09.2017.
 * Let's GO!
 */

public class DataManager {
    @Inject
    ServerRepository serverRepository;
    @Inject
    SharedPreferencesRepository sharedPreferencesRepository;

    public DataManager() {
        App.get().getAppComponent().inject(this);
    }

    public Observable<TokenEntity> registerUser(UserEntity userEntity) {
        final String[] token = new String[1];
        return serverRepository.registerUser(userEntity);
        /*serverRepository.registerUser(new UserEntity(username, password))
                .subscribe(new Subscriber<TokenEntity>() {
                    @Override
                    public void onCompleted() {
                        //Save token for further use...
                        Log.d("MY", "Completed request");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("MY", e.toString());
                    }

                    @Override
                    public void onNext(TokenEntity tokenEntity) {
                        token[0] = tokenEntity.getToken();
                    }
                });*/
    }

    public Observable<TokenEntity> loginUser(LoginUserEntity loginUserEntity) {
        final String[] token = new String[1];
        return serverRepository.loginUser(loginUserEntity);
        /*serverRepository.loginUser(loginUserEntity)
                .subscribe(new Subscriber<TokenEntity>() {
                    @Override
                    public void onCompleted() {
                        Log.d("MY", "Completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("MY", e.toString());
                    }

                    @Override
                    public void onNext(TokenEntity tokenEntity) {
                        token[0] = tokenEntity.getToken();
                    }
                });*/
    }

    public boolean isFirstTime() {
        return sharedPreferencesRepository.isFirstTime();
    }
}
