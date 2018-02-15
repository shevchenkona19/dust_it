package dustit.clientapp.mvp.datamanager;

import android.content.Context;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.model.entities.CategoryEntity;
import dustit.clientapp.mvp.model.entities.PersonalCategoryUpperEntity;
import dustit.clientapp.mvp.model.entities.CommentEntity;
import dustit.clientapp.mvp.model.entities.CommentUpperEntity;
import dustit.clientapp.mvp.model.entities.FavoritesUpperEntity;
import dustit.clientapp.mvp.model.entities.LoginUserEntity;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.MemUpperEntity;
import dustit.clientapp.mvp.model.entities.PersonalCategory;
import dustit.clientapp.mvp.model.entities.PostCommentEntity;
import dustit.clientapp.mvp.model.entities.RegisterUserEntity;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.model.entities.SelectedCategoriesEntity;
import dustit.clientapp.mvp.model.entities.TestMemEntity;
import dustit.clientapp.mvp.model.entities.TestUpperEntity;
import dustit.clientapp.mvp.model.entities.TokenEntity;
import dustit.clientapp.mvp.model.entities.UsernameEntity;
import dustit.clientapp.mvp.model.repositories.ServerRepository;
import dustit.clientapp.mvp.model.repositories.SharedPreferencesRepository;
import dustit.clientapp.utils.L;
import dustit.clientapp.utils.ProgressRequestBody;
import okhttp3.MultipartBody;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by shevc on 22.09.2017.
 * Let's GO!
 */

public class DataManager {
    @Inject
    ServerRepository serverRepository;
    @Inject
    SharedPreferencesRepository preferencesRepository;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    @Inject
    public Context context;

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
        if (userSettingsDataManager.isRegistered()) {
            return serverRepository.getPersonilizedFeed(getToken(), count, offset)
                    .flatMap(new Func1<MemUpperEntity, Observable<MemEntity>>() {
                        @Override
                        public Observable<MemEntity> call(MemUpperEntity memUpperEntity) {
                            return Observable.from(memUpperEntity.getMemEntities());
                        }
                    });
        } else {
            return serverRepository.getFeed(getToken(), count, offset)
                    .flatMap(new Func1<MemUpperEntity, Observable<MemEntity>>() {
                        @Override
                        public Observable<MemEntity> call(MemUpperEntity memUpperEntity) {
                            return Observable.from(memUpperEntity.getMemEntities());
                        }
                    });
        }
    }

    public Observable<MemEntity> getHot(int count, int offset) {
        return serverRepository.getHot(getToken(), count, offset)
                .flatMap(new Func1<MemUpperEntity, Observable<MemEntity>>() {
                    @Override
                    public Observable<MemEntity> call(MemUpperEntity memUpperEntity) {
                        return Observable.from(memUpperEntity.getMemEntities());
                    }
                });
    }

    public Observable<MemEntity> getCategoriesFeed(String categoryId, int count, int offset) {
        return serverRepository.getCategoryFeed(getToken(), categoryId, count, offset)
                .flatMap(new Func1<MemUpperEntity, Observable<MemEntity>>() {
                    @Override
                    public Observable<MemEntity> call(MemUpperEntity memUpperEntity) {
                        return Observable.from(memUpperEntity.getMemEntities());
                    }
                });
    }

    public Observable<Category> getCategories() {
        return serverRepository.getCategories(getToken())
                .flatMap(new Func1<CategoryEntity, Observable<Category>>() {
                    @Override
                    public Observable<Category> call(CategoryEntity categoryEntity) {
                        return Observable.from(categoryEntity.getCategories());
                    }
                });
    }

    public Observable<ResponseEntity> postPersonalCategories(SelectedCategoriesEntity entity) {
        return serverRepository.postSelectedCategories(getToken(), entity);
    }

    public Observable<PersonalCategory> getPersonalCategories() {
        return serverRepository.getPersonalCategories(getToken())
                .flatMap(new Func1<PersonalCategoryUpperEntity, Observable<PersonalCategory>>() {
                    @Override
                    public Observable<PersonalCategory> call(PersonalCategoryUpperEntity personalCategoryUpperEntity) {
                        return Observable.from(personalCategoryUpperEntity.getCategories());
                    }
                });
    }

    public Observable<ResponseEntity> postLike(String id) {
        return serverRepository.postLike(preferencesRepository.getSavedToken(), id);
    }

    public Observable<ResponseEntity> deleteLike(String id) {
        return serverRepository.deleteLike(preferencesRepository.getSavedToken(), id);
    }

    public Observable<ResponseEntity> postDislike(String id) {
        return serverRepository.postDislike(preferencesRepository.getSavedToken(), id);
    }

    public Observable<ResponseEntity> deleteDislike(String id) {
        return serverRepository.deleteDislike(preferencesRepository.getSavedToken(), id);
    }

    public Observable<TestMemEntity> getTest() {
        return serverRepository.getTest(getToken())
                .flatMap(new Func1<TestUpperEntity, Observable<TestMemEntity>>() {
                    @Override
                    public Observable<TestMemEntity> call(TestUpperEntity testUpperEntity) {
                        return Observable.from(testUpperEntity.getList());
                    }
                });
    }

    public Observable<ResponseEntity> addToFavorites(String id) {
        return serverRepository.addToFavorites(getToken(), id);
    }

    public Observable<FavoritesUpperEntity> getAllFavorites() {
        return serverRepository.getFavorites(getToken());
    }

    public Observable<ResponseEntity> postComment(String id, PostCommentEntity entity) {
        return serverRepository.postComment(getToken(), id, entity);
    }

    public Observable<CommentEntity> getComments(String id, int count, int offset) {
        return serverRepository.getComments(getToken(), id, count, offset)
                .flatMap(new Func1<CommentUpperEntity, Observable<CommentEntity>>() {
                    @Override
                    public Observable<CommentEntity> call(CommentUpperEntity commentUpperEntity) {
                        return Observable.from(commentUpperEntity.getList());
                    }
                });
    }

    public void saveToken(String token) {
        if (token.equals("")) {
            preferencesRepository.clearUsername();
        }
        preferencesRepository.saveToken(token);
    }

    public String getToken() {
        L.print("TOKEN: " + preferencesRepository.getSavedToken());
        return preferencesRepository.getSavedToken();
    }

    public Observable<ResponseEntity> logout() {
        return serverRepository.logout(getToken());
    }

    public Observable<ResponseEntity> postPhoto(ProgressRequestBody requestBody, String fileName) {
        MultipartBody.Part body = MultipartBody.Part.createFormData("picture", fileName, requestBody);
        return serverRepository.postPhoto(getToken(), body);
    }

    public Observable<ResponseEntity> removeFromFavorites(String id) {
        return serverRepository.removeFromFavorites(getToken(), id);
    }

    public Observable<UsernameEntity> getMyUsername() {
        return serverRepository.getMyUsername(getToken());
    }

    public void cacheUsername(String username) {
        preferencesRepository.cacheUsername(username);
    }

    public boolean isUsernameCached() {
        return preferencesRepository.isUsernameCached();
    }

    public String getCachedUsername() {
        return preferencesRepository.getCachedUsername();
    }

    public Context getContext() {
        return context;
    }
}
