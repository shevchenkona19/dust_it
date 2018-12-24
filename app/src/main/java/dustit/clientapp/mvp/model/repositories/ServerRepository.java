package dustit.clientapp.mvp.model.repositories;


import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.model.apis.ServerAPI;
import dustit.clientapp.mvp.model.entities.AchievementsEntity;
import dustit.clientapp.mvp.model.entities.CategoryEntity;
import dustit.clientapp.mvp.model.entities.CommentUpperEntity;
import dustit.clientapp.mvp.model.entities.FavoritesUpperEntity;
import dustit.clientapp.mvp.model.entities.IsFavourite;
import dustit.clientapp.mvp.model.entities.LoginUserEntity;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.MemUpperEntity;
import dustit.clientapp.mvp.model.entities.NewResponseEntity;
import dustit.clientapp.mvp.model.entities.PersonalCategoryUpperEntity;
import dustit.clientapp.mvp.model.entities.PhotoBody;
import dustit.clientapp.mvp.model.entities.PostCommentEntity;
import dustit.clientapp.mvp.model.entities.PostSelectedCategoriesUpperEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RegisterUserEntity;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.model.entities.TestUpperEntity;
import dustit.clientapp.mvp.model.entities.TokenEntity;
import dustit.clientapp.mvp.model.entities.UserFeedbackEntity;
import dustit.clientapp.mvp.model.entities.UsernameEntity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by shevc on 22.09.2017.
 * Let's GO!
 */

public class ServerRepository {
    @Inject
    ServerAPI serverAPI;

    public ServerRepository() {
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

    public Observable<MemUpperEntity> getPersonalisedFeed(String token, int count, int offset) {
        return serverAPI.getPersonalizedFeed(token, count, offset)
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
                                                             PostSelectedCategoriesUpperEntity entity) {
        return serverAPI.postSelectedCategories(token, entity)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PersonalCategoryUpperEntity> getPersonalCategories(String token) {
        return serverAPI.getPersonalCategories(token)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RefreshedMem> postLike(String token, String id) {
        return serverAPI.postLike(token, id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RefreshedMem> deleteLike(String token, String id) {
        return serverAPI.deleteLike(token, id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RefreshedMem> postDislike(String token, String id) {
        return serverAPI.postDislike(token, id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RefreshedMem> deleteDislike(String token, String id) {
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

    public Observable<FavoritesUpperEntity> getFavorites(String userId) {
        return serverAPI.getAllFavorites(userId)
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

    public Observable<ResponseEntity> postPhoto(String token, PhotoBody image) {
        return serverAPI.postPhoto(token, image).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UsernameEntity> getUsername(String userId) {
        return serverAPI.getUsername(userId).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseEntity> removeFromFavorites(String token, String id) {
        return serverAPI.removeFromFavorites(token, id).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RefreshedMem> refreshMem(String token, String id) {
        return serverAPI.refreshMem(token, id).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<IsFavourite> isFavourite(String token, String id) {
        return serverAPI.isFavourite(token, id).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<NewResponseEntity> postUserFeedback(String token, UserFeedbackEntity userFeedbackEntity) {
        return serverAPI.postUserFeedback(token, userFeedbackEntity).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AchievementsEntity> getAchievements(String userId) {
        return serverAPI.getAchievements(userId).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CommentUpperEntity> getAnswersForComment(String commentId, int limit, int offset) {
        return serverAPI.getAnswersForComment(commentId, limit, offset)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseEntity> postAnswerForComment(String token,
                                                           String imageId,
                                                           String commentId,
                                                           String userId,
                                                           PostCommentEntity entity) {
        return serverAPI.postCommentAnswer(token, imageId, commentId, userId, entity)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseEntity> setFcmId(String token, String fcmId) {
        return serverAPI.postFcmId(token, fcmId).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MemEntity> getMemById(String memId) {
        return serverAPI.getMemById(memId).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CommentUpperEntity> getCommentsToCommentId(String memId, String toCommentId) {
        return serverAPI.getCommentsToCommentId(memId, toCommentId).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CommentUpperEntity> getAnswersForCommentToId(String parentCommentId, String childCommentId, String imageId) {
        return serverAPI.getAnswersForCommentToId(parentCommentId, childCommentId, imageId).observeOn(AndroidSchedulers.mainThread());
    }
}