package dustit.clientapp.mvp.model.apis;

import dustit.clientapp.mvp.model.entities.AchievementsEntity;
import dustit.clientapp.mvp.model.entities.CategoryEntity;
import dustit.clientapp.mvp.model.entities.IsFavourite;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.NewResponseEntity;
import dustit.clientapp.mvp.model.entities.PersonalCategoryUpperEntity;
import dustit.clientapp.mvp.model.entities.CommentUpperEntity;
import dustit.clientapp.mvp.model.entities.FavoritesUpperEntity;
import dustit.clientapp.mvp.model.entities.LoginUserEntity;
import dustit.clientapp.mvp.model.entities.MemUpperEntity;
import dustit.clientapp.mvp.model.entities.PhotoBody;
import dustit.clientapp.mvp.model.entities.PostCommentEntity;
import dustit.clientapp.mvp.model.entities.PostSelectedCategoriesUpperEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RegisterUserEntity;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.model.entities.SelectedCategoriesEntity;
import dustit.clientapp.mvp.model.entities.TestUpperEntity;
import dustit.clientapp.mvp.model.entities.TokenEntity;
import dustit.clientapp.mvp.model.entities.UserFeedbackEntity;
import dustit.clientapp.mvp.model.entities.UsernameEntity;
import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by shevc on 22.09.2017.
 * Let's GO!
 */

public interface ServerAPI {

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
                                      @Query("id") String id);

    @DELETE("/feedback/like")
    Observable<RefreshedMem> deleteLike(@Header("Authorization") String token,
                                        @Query("id") String id);

    @POST("/feedback/dislike")
    Observable<RefreshedMem> postDislike(@Header("Authorization") String token,
                                         @Query("id") String id);

    @DELETE("/feedback/dislike")
    Observable<RefreshedMem> deleteDislike(@Header("Authorization") String token,
                                           @Query("id") String id);

    @POST("/account/logout")
    Observable<ResponseEntity> logout(@Header("Authorization") String token);

    @GET("/config/test")
    Observable<TestUpperEntity> getTest(@Header("Authorization") String token);

    @POST("/favorites/addToFavorites")
    Observable<ResponseEntity> addToFavorites(@Header("Authorization") String token,
                                              @Query("id") String id);

    @GET("/v1/favorites/allFavorites")
    Observable<FavoritesUpperEntity> getAllFavorites(@Query("userId") String userId);

    @POST("/feedback/comment")
    Observable<ResponseEntity> postComment(@Header("Authorization") String token,
                                           @Query("id") String id,
                                           @Body PostCommentEntity entity);

    @GET("/feedback/comments")
    Observable<CommentUpperEntity> getComments(@Header("Authorization") String token,
                                               @Query("id") String id,
                                               @Query("count") int count,
                                               @Query("offset") int offset);

    @POST("/config/photo")
    Observable<ResponseEntity> postPhoto(@Header("Authorization") String token,
                                         @Body PhotoBody photoBody);

    @GET("/v1/account/username")
    Observable<UsernameEntity> getUsername(@Query("userId") String userId);

    @DELETE("/favorites/removeFromFavorites")
    Observable<ResponseEntity> removeFromFavorites(@Header("Authorization") String token,
                                                   @Query("id") String id);

    @GET("/feed/refreshMem")
    Observable<RefreshedMem> refreshMem(@Header("Authorization") String token,
                                        @Query("memId") String memId);

    @GET("/favorites/isFavourite")
    Observable<IsFavourite> isFavourite(@Header("Authorization") String token,
                                        @Query("id") String id);

    @POST("/feedback/messageForDev")
    Observable<NewResponseEntity> postUserFeedback(@Header("Authorization") String token,
                                                   @Body UserFeedbackEntity userFeedbackEntity);

    @GET("/account/achievements")
    Observable<AchievementsEntity> getAchievements(@Query("userId") String id);

    @GET("/feedback/answersForComment")
    Observable<CommentUpperEntity> getAnswersForComment(@Query("commentId") String commentId,
                                                        @Query("limit") int limit,
                                                        @Query("offset") int offset);

    @POST("/feedback/commentAnswer")
    Observable<ResponseEntity> postCommentAnswer(@Header("Authorization") String token,
                                                 @Query("id") String imageId,
                                                 @Query("commentId") String commentId,
                                                 @Query("answerUserId") String answerUserId,
                                                 @Body PostCommentEntity postCommentEntity);

    @POST("/account/fcmId")
    Observable<ResponseEntity> postFcmId(@Header("Authorization") String token,
                                         @Query("fcmId") String fcmId);

    @GET("/feed/mem")
    Observable<MemEntity> getMemById(@Query("memId") String memId);

    @GET("/feedback/commentsToCommentId")
    Observable<CommentUpperEntity> getCommentsToCommentId(@Query("memId") String memId,
                                                          @Query("toCommentId") String toCommentId);

    @GET("/feedback/answersForCommentToId")
    Observable<CommentUpperEntity> getAnswersForCommentToId(@Query("parentCommentId") String parentCommentId,
                                                         @Query("childCommentId") String childCommentId,
                                                         @Query("imageId") String imageId);

}
