package dustit.moderatorapp.mvp.datamanager;

import javax.inject.Inject;

import dustit.moderatorapp.App;
import dustit.moderatorapp.mvp.model.entities.CategoriesIdEntity;
import dustit.moderatorapp.mvp.model.entities.Category;
import dustit.moderatorapp.mvp.model.entities.CategoryEntity;
import dustit.moderatorapp.mvp.model.entities.LoginUserEntity;
import dustit.moderatorapp.mvp.model.entities.MemIdEntity;
import dustit.moderatorapp.mvp.model.entities.ResponseCode;
import dustit.moderatorapp.mvp.model.entities.TokenEntity;
import dustit.moderatorapp.mvp.model.entities.UserEntity;
import dustit.moderatorapp.mvp.model.repositories.ServerRepository;
import dustit.moderatorapp.mvp.model.repositories.SharedPreferencesRepository;
import rx.Observable;
import rx.functions.Func1;

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
        return serverRepository.registerUser(userEntity);
    }

    public Observable<TokenEntity> loginUser(LoginUserEntity loginUserEntity) {
        return serverRepository.loginUser(loginUserEntity);
    }

    public Observable<ResponseCode> logout() {
        return serverRepository.logout(sharedPreferencesRepository.getCurrentToken().getToken());
    }

    public Observable<Category> getCategories() {
        return serverRepository.getCategories(sharedPreferencesRepository.getCurrentToken().getToken())
                .flatMap(new Func1<CategoryEntity, Observable<Category>>() {
                    @Override
                    public Observable<Category> call(CategoryEntity categoryEntity) {
                        return Observable.from(categoryEntity.getCategories());
                    }
                });
    }

    public Observable<ResponseCode> postMem(String id, CategoriesIdEntity entity) {
        return serverRepository.postMem(sharedPreferencesRepository.getCurrentToken().getToken(),
                id, entity);
    }

    public Observable<MemIdEntity> getNewMem() {
        return serverRepository.getNewMem(sharedPreferencesRepository.getCurrentToken().getToken());
    }

    public Observable<ResponseCode> discardMem(String id) {
        return serverRepository.discardMem(sharedPreferencesRepository.getCurrentToken().getToken(), id);
    }

    public void saveToken(String token) {
        sharedPreferencesRepository.saveToken(token);
    }

    public void deleteCurrentToken() {
        sharedPreferencesRepository.deleteToken();
    }

    public boolean isLogged() {
        return sharedPreferencesRepository.isLogged();
    }

    public String getToken() {
        return sharedPreferencesRepository.getCurrentToken().getToken();
    }
}
