package dustit.clientapp.mvp.model.apis;

import dustit.clientapp.mvp.model.entities.AchievementsEntity;
import dustit.clientapp.mvp.model.entities.CategoryEntity;
import dustit.clientapp.mvp.model.entities.CommentUpperEntity;
import dustit.clientapp.mvp.model.entities.FavoritesUpperEntity;
import dustit.clientapp.mvp.model.entities.UserSearchResponseEntity;
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
import dustit.clientapp.mvp.model.entities.UsernameEntity;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by shevc on 22.09.2017.
 * Let's GO!
 */

public interface ServerAPI {

    @DELETE("/account/fcmId")
    Observable<ResponseEntity> deleteFcmId(@Header("Authorization") String token);

    @POST("/account/register/")
    Observable<TokenEntity> registerUser(@Body RegisterUserEntity userEntity);

    @POST("/account/login/")
    Observable<TokenEntity> loginUser(@Body LoginUserEntity loginUserEntity);

    @GET("/feed/mainFeed")
    Observable<MemUpperEntity> getFeed(@Header("Authorization") String token,
                                       @Query("count") int count,
                                       @Query("offset") int offset);

    @GET("feed/categoriesFeed")
    Observable<MemUpperEntity> getPersonalizedFeed(@Header("Authorization") String token,
                                                   @Query("count") int count,
                                                   @Query("offset") int offset);

    @GET("/feed/hotFeed")
    Observable<MemUpperEntity> getHot(@Header("Authorization") String token,
                                      @Query("count") int count,
                                      @Query("offset") int offset);

    @GET("/feed/categoryFeed")
    Observable<MemUpperEntity> getCategoriesFeed(@Header("Authorization") String token,
                                                 @Query("categoryId") String categoryId,
                                                 @Query("count") int count,
                                                 @Query("offset") int offset);

    @GET("/config/categories")
    Observable<CategoryEntity> getCategories(@Header("Authorization") String token);

    @POST("/config/selectedCategories")
    Observable<ResponseEntity> postSelectedCategories(@Header("Authorization") String token,
                                                      @Body PostSelectedCategoriesUpperEntity entity);

    @GET("/config/personalCategories")
    Observable<PersonalCategoryUpperEntity> getPersonalCategories(@Header("Authorization") String token);

    @POST("/feedback/like")
    Observable<RefreshedMem> postLike(@Header("Authorization") String token,
                                      @Query("id") int id);

    @DELETE("/feedback/like")
    Observable<RefreshedMem> deleteLike(@Header("Authorization") String token,
                                        @Query("id") int id);

    @POST("/feedback/dislike")
    Observable<RefreshedMem> postDislike(@Header("Authorization") String token,
                                         @Query("id") int id);

    @DELETE("/feedback/dislike")
    Observable<RefreshedMem> deleteDislike(@Header("Authorization") String token,
                                           @Query("id") int id);

    @POST("/account/logout")
    Observable<ResponseEntity> logout(@Header("Authorization") String token);

    @GET("/config/test")
    Observable<TestUpperEntity> getTest(@Header("Authorization") String token);

    @POST("/favorites/addToFavorites")
    Observable<RefreshedMem> addToFavorites(@Header("Authorization") String token,
                                            @Query("id") int id);

    @GET("/v2/favorites/allFavorites")
    Observable<FavoritesUpperEntity> getAllFavorites(@Query("userId") int userId);

    @POST("/feedback/comment")
    Observable<ResponseEntity> postComment(@Header("Authorization") String token,
                                           @Query("id") int id,
                                           @Body PostCommentEntity entity);

    @GET("/feedback/comments")
    Observable<CommentUpperEntity> getComments(@Header("Authorization") String token,
                                               @Query("id") int id,
                                               @Query("count") int count,
                                               @Query("offset") int offset);

    @POST("/config/photo")
    Observable<ResponseEntity> postPhoto(@Header("Authorization") String token,
                                         @Body PhotoBody photoBody);

    @GET("/v1/account/username")
    Observable<UsernameEntity> getUsername(@Query("userId") int userId);

    @DELETE("/favorites/removeFromFavorites")
    Observable<RefreshedMem> removeFromFavorites(@Header("Authorization") String token,
                                                 @Query("id") int id);

    @GET("/favorites/isFavourite")
    Observable<IsFavourite> isFavourite(@Header("Authorization") String token,
                                        @Query("id") int id);

    @POST("/feedback/messageForDev")
    Observable<NewResponseEntity> postUserFeedback(@Header("Authorization") String token,
                                                   @Body UserFeedbackEntity userFeedbackEntity);

    @GET("/account/achievements")
    Observable<AchievementsEntity> getAchievements(@Query("userId") int id);

    @GET("/feedback/answersForComment")
    Observable<CommentUpperEntity> getAnswersForComment(@Query("commentId") int commentId,
                                                        @Query("limit") int limit,
                                                        @Query("offset") int offset);

    @POST("/feedback/commentAnswer")
    Observable<ResponseEntity> postCommentAnswer(@Header("Authorization") String token,
                                                 @Query("id") int imageId,
                                                 @Query("commentId") int commentId,
                                                 @Query("answerUserId") int answerUserId,
                                                 @Body PostCommentEntity postCommentEntity);

    @POST("/account/fcmId")
    Observable<ResponseEntity> postFcmId(@Header("Authorization") String token,
                                         @Query("fcmId") String fcmId);

    @GET("/feed/mem")
    Observable<MemEntity> getMemById(@Query("memId") int memId);

    @GET("/feedback/commentsToCommentId")
    Observable<CommentUpperEntity> getCommentsToCommentId(@Query("memId") int memId,
                                                          @Query("toCommentId") int toCommentId);

    @GET("/feedback/answersForCommentToId")
    Observable<CommentUpperEntity> getAnswersForCommentToId(@Query("parentCommentId") int parentCommentId,
                                                            @Query("childCommentId") int childCommentId,
                                                            @Query("imageId") int imageId);

    @GET("/account/myReferralInfo")
    Observable<ReferralInfoEntity> getMyReferralInfo(@Header("Authorization") String token);

    @GET("/account/userUploads")
    Observable<UploadsUpperEntity> getUserUploads(@Header("Authorization") String token,
                                                  @Query("userId") int userId,
                                                  @Query("limit") int limit,
                                                  @Query("offset") int offset);

    @POST("/account/uploadMeme")
    Observable<NewResponseEntity> uploadMeme(@Header("Authorization") String token,
                                             @Body UploadBody uploadBody);

    @POST("/reports/report")
    Observable<SimpleResponseEntity> reportMeme(@Header("Authorization") String token,
                                                @Body ReportEntity reportEntity);

    @GET("/feed/searchUser")
    Observable<UserSearchResponseEntity> searchUsers(@Query("query") String searchQuery);
}
