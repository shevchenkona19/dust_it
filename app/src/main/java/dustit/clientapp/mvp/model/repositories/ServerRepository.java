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
import dustit.clientapp.mvp.model.entities.ReferralInfoEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RegisterUserEntity;
import dustit.clientapp.mvp.model.entities.ReportEntity;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.model.entities.SimpleResponseEntity;
import dustit.clientapp.mvp.model.entities.TestUpperEntity;
import dustit.clientapp.mvp.model.entities.TokenEntity;
import dustit.clientapp.mvp.model.entities.UploadBody;
import dustit.clientapp.mvp.model.entities.UploadsUpperEntity;
import dustit.clientapp.mvp.model.entities.UserFeedbackEntity;
import dustit.clientapp.mvp.model.entities.UserSearchResponseEntity;
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

    public Observable<RefreshedMem> postLike(String token, int id) {
        return serverAPI.postLike(token, id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RefreshedMem> deleteLike(String token, int id) {
        return serverAPI.deleteLike(token, id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RefreshedMem> postDislike(String token, int id) {
        return serverAPI.postDislike(token, id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RefreshedMem> deleteDislike(String token, int id) {
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

    public Observable<RefreshedMem> addToFavorites(String token, int id) {
        return serverAPI.addToFavorites(token, id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<FavoritesUpperEntity> getFavorites(int userId) {
        return serverAPI.getAllFavorites(userId)
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Observable<ResponseEntity> postComment(String token,
                                                  int id,
                                                  PostCommentEntity entity) {
        return serverAPI.postComment(token, id, entity)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CommentUpperEntity> getComments(String token,
                                                      int id,
                                                      int count,
                                                      int offset) {
        return serverAPI.getComments(token, id, count, offset)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseEntity> postPhoto(String token, PhotoBody image) {
        return serverAPI.postPhoto(token, image).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UsernameEntity> getUsername(int userId) {
        return serverAPI.getUsername(userId).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RefreshedMem> removeFromFavorites(String token, int id) {
        return serverAPI.removeFromFavorites(token, id).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<IsFavourite> isFavourite(String token, int id) {
        return serverAPI.isFavourite(token, id).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<NewResponseEntity> postUserFeedback(String token, UserFeedbackEntity userFeedbackEntity) {
        return serverAPI.postUserFeedback(token, userFeedbackEntity).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AchievementsEntity> getAchievements(int userId) {
        return serverAPI.getAchievements(userId).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CommentUpperEntity> getAnswersForComment(int commentId, int limit, int offset) {
        return serverAPI.getAnswersForComment(commentId, limit, offset)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseEntity> postAnswerForComment(String token,
                                                           int imageId,
                                                           int commentId,
                                                           int userId,
                                                           PostCommentEntity entity) {
        return serverAPI.postCommentAnswer(token, imageId, commentId, userId, entity)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseEntity> setFcmId(String token, String fcmId) {
        return serverAPI.postFcmId(token, fcmId).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MemEntity> getMemById(int memId) {
        return serverAPI.getMemById(memId).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CommentUpperEntity> getCommentsToCommentId(int memId, int toCommentId) {
        return serverAPI.getCommentsToCommentId(memId, toCommentId).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CommentUpperEntity> getAnswersForCommentToId(int parentCommentId, int childCommentId, int imageId) {
        return serverAPI.getAnswersForCommentToId(parentCommentId, childCommentId, imageId).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ReferralInfoEntity> getMyReferralInfo(String token) {
        return serverAPI.getMyReferralInfo(token).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UploadsUpperEntity> getUserUploads(String token, int userId, int offset, int limit) {
        return serverAPI.getUserUploads(token, userId, offset, limit)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<NewResponseEntity> uploadMeme(String token, UploadBody body) {
        return serverAPI.uploadMeme(token, body)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SimpleResponseEntity> reportMeme(String token, ReportEntity reportEntity) {
        return serverAPI.reportMeme(token, reportEntity)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UserSearchResponseEntity> searchUsers(String query) {
        return serverAPI.searchUsers(query)
                .observeOn(AndroidSchedulers.mainThread());
    }
}