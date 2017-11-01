package dustit.moderatorapp.mvp.model.repositories;

import javax.inject.Inject;

import dustit.moderatorapp.App;
import dustit.moderatorapp.mvp.model.api.ServerAPI;
import dustit.moderatorapp.mvp.model.entities.CategoriesIdEntity;
import dustit.moderatorapp.mvp.model.entities.CategoryEntity;
import dustit.moderatorapp.mvp.model.entities.LoginUserEntity;
import dustit.moderatorapp.mvp.model.entities.MemIdEntity;
import dustit.moderatorapp.mvp.model.entities.ResponseCode;
import dustit.moderatorapp.mvp.model.entities.TokenEntity;
import dustit.moderatorapp.mvp.model.entities.UserEntity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by shevc on 15.09.2017.
 * Let's GO!
 */

public class ServerRepository {
    @Inject
    ServerAPI serverAPI;

    public ServerRepository(){
        App.get().getAppComponent().inject(this);
    }

    public Observable<TokenEntity> registerUser(UserEntity userEntity) {
        return serverAPI.registerUser(userEntity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<TokenEntity> loginUser(LoginUserEntity loginUserEntity) {
        return serverAPI.loginUser(loginUserEntity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<CategoryEntity> getCategories(String token) {
        return serverAPI.getCategories(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<ResponseCode> postMem(String token, String id, CategoriesIdEntity entity) {
        return serverAPI.postMem(token, id, entity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<MemIdEntity> getNewMem(String token) {
        return serverAPI.getNewMem(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<ResponseCode> discardMem(String token, String id) {
        return serverAPI.discardMem(token, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<ResponseCode> logout(String token) {
        return serverAPI.logout(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
}
