package dustit.clientapp.mvp.datamanager;

import java.util.List;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.model.entities.LoginUserEntity;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.MemUpperEntity;
import dustit.clientapp.mvp.model.entities.RegisterUserEntity;
import dustit.clientapp.mvp.model.entities.TokenEntity;
import dustit.clientapp.mvp.model.repositories.ServerRepository;
import rx.Observable;
import rx.functions.Func1;

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

    public Observable<MemEntity> getFeed(int count, int offset) {
        return serverRepository.getFeed("token", count, offset)
                .flatMap(new Func1<MemUpperEntity, Observable<MemEntity>>() {
                    @Override
                    public Observable<MemEntity> call(MemUpperEntity memUpperEntity) {
                        return Observable.from(memUpperEntity.getMemEntities());
                    }
                });
    }

    public void postLike(String id) {
        serverRepository.postLike("token", id);
    }
}
