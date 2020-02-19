package dustit.clientapp.mvp.datamanager;

import android.content.Context;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.model.entities.AchievementsEntity;
import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.model.entities.CategoryEntity;
import dustit.clientapp.mvp.model.entities.CommentEntity;
import dustit.clientapp.mvp.model.entities.CommentUpperEntity;
import dustit.clientapp.mvp.model.entities.FavoritesUpperEntity;
import dustit.clientapp.mvp.model.entities.IsFavourite;
import dustit.clientapp.mvp.model.entities.LoginUserEntity;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.MemUpperEntity;
import dustit.clientapp.mvp.model.entities.NewResponseEntity;
import dustit.clientapp.mvp.model.entities.PersonalCategory;
import dustit.clientapp.mvp.model.entities.PersonalCategoryUpperEntity;
import dustit.clientapp.mvp.model.entities.PhotoBody;
import dustit.clientapp.mvp.model.entities.PostCommentEntity;
import dustit.clientapp.mvp.model.entities.PostSelectedCategoriesUpperEntity;
import dustit.clientapp.mvp.model.entities.ReferralInfoEntity;
import dustit.clientapp.mvp.model.entities.RegisterUserEntity;
import dustit.clientapp.mvp.model.entities.ReportEntity;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.model.entities.SimpleResponseEntity;
import dustit.clientapp.mvp.model.entities.TestMemEntity;
import dustit.clientapp.mvp.model.entities.TestUpperEntity;
import dustit.clientapp.mvp.model.entities.TokenEntity;
import dustit.clientapp.mvp.model.entities.UploadBody;
import dustit.clientapp.mvp.model.entities.UploadEntity;
import dustit.clientapp.mvp.model.entities.UploadsUpperEntity;
import dustit.clientapp.mvp.model.entities.UserFeedbackEntity;
import dustit.clientapp.mvp.model.entities.UserSearchResponseEntity;
import dustit.clientapp.mvp.model.entities.UsernameEntity;
import dustit.clientapp.mvp.model.repositories.ServerRepository;
import dustit.clientapp.mvp.model.repositories.SharedPreferencesRepository;
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

    public Observable<MemUpperEntity> getFeed(int count, int offset) {
        if (userSettingsDataManager.isRegistered()) {
            return serverRepository.getPersonalisedFeed(getToken(), count, offset);
        } else {
            return serverRepository.getFeed(getToken(), count, offset);
        }
    }

    public Observable<MemUpperEntity> getHot(int count, int offset) {
        return serverRepository.getHot(getToken(), count, offset);
    }

    public Observable<MemUpperEntity> getCategoriesFeed(String categoryId, int count, int offset) {
        return serverRepository.getCategoryFeed(getToken(), categoryId, count, offset);
    }

    public Observable<Category> getCategories() {
        return serverRepository.getCategories(getToken())
                .flatMap((Func1<CategoryEntity, Observable<Category>>) categoryEntity -> Observable.from(categoryEntity.getCategories()));
    }

    public Observable<ResponseEntity> postPersonalCategories(PostSelectedCategoriesUpperEntity entity) {
        return serverRepository.postSelectedCategories(getToken(), entity);
    }

    public Observable<PersonalCategory> getPersonalCategories() {
        return serverRepository.getPersonalCategories(getToken())
                .flatMap((Func1<PersonalCategoryUpperEntity, Observable<PersonalCategory>>) personalCategoryUpperEntity -> Observable.from(personalCategoryUpperEntity.getCategories()));
    }

    public Observable<TestMemEntity> getTest() {
        return serverRepository.getTest(getToken())
                .flatMap((Func1<TestUpperEntity, Observable<TestMemEntity>>) testUpperEntity -> Observable.from(testUpperEntity.getList()));
    }

    public Observable<FavoritesUpperEntity> getAllFavorites(int id) {
        return serverRepository.getFavorites(id);
    }

    public Observable<ResponseEntity> postComment(int id, PostCommentEntity entity) {
        return serverRepository.postComment(getToken(), id, entity);
    }

    public Observable<CommentEntity> getComments(int id, int count, int offset) {
        return serverRepository.getComments(getToken(), id, count, offset)
                .flatMap((Func1<CommentUpperEntity, Observable<CommentEntity>>) commentUpperEntity -> Observable.from(commentUpperEntity.getList()));
    }

    public void saveToken(String token) {
        if (token.equals("")) {
            preferencesRepository.clearUsername();
        }
        preferencesRepository.saveToken(token);
    }

    public String getToken() {
        return preferencesRepository.getSavedToken();
    }

    public Observable<ResponseEntity> logout() {
        return serverRepository.logout(getToken());
    }

    public Observable<ResponseEntity> postPhoto(PhotoBody photoBody) {
        return serverRepository.postPhoto(getToken(), photoBody);
    }

    public Observable<UsernameEntity> getUsername(int id) {
        return serverRepository.getUsername(id);
    }

    public Observable<AchievementsEntity> getAchievements(int userId) {
        return serverRepository.getAchievements(userId);
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

    public Observable<IsFavourite> isFavourite(int id) {
        return serverRepository.isFavourite(getToken(), id);
    }

    public Observable<NewResponseEntity> postUserFeedback(UserFeedbackEntity userFeedbackEntity) {
        return serverRepository.postUserFeedback(getToken(), userFeedbackEntity);
    }

    public void saveId(int id) {
        preferencesRepository.saveMyId(id);
    }

    public int loadId() {
        return preferencesRepository.loadId();
    }

    public Observable<CommentEntity> getAnswersForComment(int commentId, int limit, int offset) {
        return serverRepository.getAnswersForComment(commentId, limit, offset)
                .flatMap((Func1<CommentUpperEntity, Observable<CommentEntity>>) commentUpperEntity -> Observable.from(commentUpperEntity.getList()));
    }

    public Observable<ResponseEntity> postAnswerForComment(int imageId,
                                                           int commentId,
                                                           int userId,
                                                           PostCommentEntity entity) {
        return serverRepository.postAnswerForComment(getToken(), imageId, commentId, userId, entity);
    }

    public Observable<ResponseEntity> setFcmId(String fcmId) {
        return serverRepository.setFcmId(getToken(), fcmId);
    }

    public Observable<MemEntity> getMemById(int memId) {
        return serverRepository.getMemById(memId);
    }

    public Observable<CommentEntity> getCommentsToCommentId(int memId, int toCommentId) {
        return serverRepository.getCommentsToCommentId(memId, toCommentId)
                .flatMap((Func1<CommentUpperEntity, Observable<CommentEntity>>) commentUpperEntity -> Observable.from(commentUpperEntity.getList()));
    }

    public Observable<CommentEntity> getAnswersForCommentToId(int childCommentId, int parentCommentId, int imageId) {
        return serverRepository.getAnswersForCommentToId(parentCommentId, childCommentId, imageId)
                .flatMap((Func1<CommentUpperEntity, Observable<CommentEntity>>) commentUpperEntity -> Observable.from(commentUpperEntity.getList()));
    }

    public Observable<ReferralInfoEntity> getMyReferralInfo() {
        return serverRepository.getMyReferralInfo(getToken());
    }

    public Observable<UploadEntity> getUserUploads(int userId, int limit, int offset) {
        return serverRepository.getUserUploads(getToken(), userId, limit, offset)
                .flatMap((Func1<UploadsUpperEntity, Observable<UploadEntity>>) uploadsUpperEntity -> Observable.from(uploadsUpperEntity.getUploadEntities()));
    }

    public Observable<NewResponseEntity> uploadMeme(UploadBody body) {
        return serverRepository.uploadMeme(getToken(), body);
    }

    public Observable<SimpleResponseEntity> reportMeme(ReportEntity reportEntity) {
        return serverRepository.reportMeme(getToken(), reportEntity);
    }

    public Observable<UserSearchResponseEntity> searchUsers(String query) {
        return serverRepository.searchUsers(query);
    }
}
