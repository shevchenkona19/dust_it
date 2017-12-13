package dustit.clientapp.mvp.model.repositories;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.model.apis.ServerAPI;
import dustit.clientapp.mvp.model.entities.CategoryEntity;
import dustit.clientapp.mvp.model.entities.CategoryIdEntity;
import dustit.clientapp.mvp.model.entities.CommentUpperEntity;
import dustit.clientapp.mvp.model.entities.FavoritesUpperEntity;
import dustit.clientapp.mvp.model.entities.LoginUserEntity;
import dustit.clientapp.mvp.model.entities.MemUpperEntity;
import dustit.clientapp.mvp.model.entities.PostCommentEntity;
import dustit.clientapp.mvp.model.entities.RegisterUserEntity;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.model.entities.TestUpperEntity;
import dustit.clientapp.mvp.model.entities.TokenEntity;
import dustit.clientapp.mvp.model.entities.UsernameEntity;
import okhttp3.MultipartBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by shevc on 22.09.2017.
 * Let's GO!
 */

public class ServerRepository {
    @Inject
    ServerAPI serverAPI;

    public ServerRepository(){
        App.get().getAppComponent().inject(this);
    }

    public Observable<TokenEntity> registerUser(RegisterUserEntity userEntity) {
        return serverAPI.registerUser(userEntity)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<TokenEntity> loginUser(LoginUserEntity loginUserEntity) {
        return serverAPI.loginUser(loginUserEntity)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MemUpperEntity> getFeed(String token,
                                              int count,
                                              int offset) {
        return serverAPI.getFeed(token, count, offset)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MemUpperEntity> getHot(String token,
                                             int count,
                                             int offset) {
        return serverAPI.getHot(token, count, offset)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MemUpperEntity> getCategoryFeed(String token,
                                                      String categoryId,
                                                      int count,
                                                      int offset) {
        return serverAPI.getCategoriesFeed(token, categoryId, count, offset)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CategoryEntity> getCategories(String token) {
        return serverAPI.getCategories(token)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseEntity> postSelectedCategories(String token,
                                                             CategoryIdEntity entity) {
        return serverAPI.postSelectedCategories(token, entity)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CategoryIdEntity> getPersonalCategories(String token) {
        return serverAPI.getPersonalCategories(token)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseEntity> postLike(String token, String id) {
        return serverAPI.postLike(token, id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseEntity> deleteLike(String token, String id) {
        return serverAPI.deleteLike(token, id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseEntity> postDislike(String token, String id) {
        return serverAPI.postDislike(token, id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseEntity> deleteDislike(String token, String id) {
        return serverAPI.deleteDislike(token, id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseEntity> logout(String token) {
        return serverAPI.logout(token)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<TestUpperEntity> getTest(String token) {
        return serverAPI.getTest(token)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseEntity> addToFavorites(String token, String id) {
        return serverAPI.addToFavorites(token, id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<FavoritesUpperEntity> getFavorites(String token) {
        return serverAPI.getAllFavorites(token)
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Observable<ResponseEntity> postComment(String token,
                                                  String id,
                                                  PostCommentEntity entity) {
        return serverAPI.postComment(token, id, entity)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CommentUpperEntity> getComments(String token,
                                                      String id,
                                                      int count,
                                                      int offset) {
        return serverAPI.getComments(token, id, count, offset)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseEntity> postPhoto(String token, MultipartBody.Part body) {
        return serverAPI.postPhoto(token, body).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UsernameEntity> getMyUsername(String token) {
        return serverAPI.getMyUsername(token).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseEntity> removeFromFavorites(String token, String id) {
        return serverAPI.removeFromFavorites(token, id).observeOn(AndroidSchedulers.mainThread());
    }
}
