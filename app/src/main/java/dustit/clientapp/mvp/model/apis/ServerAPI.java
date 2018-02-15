package dustit.clientapp.mvp.model.apis;

import dustit.clientapp.mvp.model.entities.CategoryEntity;
import dustit.clientapp.mvp.model.entities.PersonalCategoryUpperEntity;
import dustit.clientapp.mvp.model.entities.CommentUpperEntity;
import dustit.clientapp.mvp.model.entities.FavoritesUpperEntity;
import dustit.clientapp.mvp.model.entities.LoginUserEntity;
import dustit.clientapp.mvp.model.entities.MemUpperEntity;
import dustit.clientapp.mvp.model.entities.PostCommentEntity;
import dustit.clientapp.mvp.model.entities.RegisterUserEntity;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.model.entities.SelectedCategoriesEntity;
import dustit.clientapp.mvp.model.entities.TestUpperEntity;
import dustit.clientapp.mvp.model.entities.TokenEntity;
import dustit.clientapp.mvp.model.entities.UsernameEntity;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
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

    @GET("/feed/getFeed")
    Observable<MemUpperEntity> getFeed(@Header("Authorization") String token,
                                       @Query("count") int count,
                                       @Query("offset") int offset);

    @GET("feed/getCategoriesFeed")
    Observable<MemUpperEntity> getPersonalizedFeed(@Header("Authorization") String token,
                                                   @Query("count") int count,
                                                   @Query("offset") int offset);

    @GET("/feed/getHotFeed")
    Observable<MemUpperEntity> getHot(@Header("Authorization") String token,
                                      @Query("count") int count,
                                      @Query("offset") int offset);

    @GET("/feed/getCategoryFeed")
    Observable<MemUpperEntity> getCategoriesFeed(@Header("Authorization") String token,
                                                 @Query("category") String categoryId,
                                                 @Query("count") int count,
                                                 @Query("offset") int offset);

    @GET("/config/getCategories")
    Observable<CategoryEntity> getCategories(@Header("Authorization") String token);

    @POST("/config/postSelectedCategories")
    Observable<ResponseEntity> postSelectedCategories(@Header("Authorization") String token,
                                                      @Body SelectedCategoriesEntity entity);

    @GET("/config/getPersonalCategories")
    Observable<PersonalCategoryUpperEntity> getPersonalCategories(@Header("Authorization") String token);

    @GET("/feedback/postLike")
    Observable<ResponseEntity> postLike(@Header("Authorization") String token,
                                        @Query("id") String id);

    @GET("/feedback/deleteLike")
    Observable<ResponseEntity> deleteLike(@Header("Authorization") String token,
                                          @Query("id") String id);

    @GET("/feedback/postDislike")
    Observable<ResponseEntity> postDislike(@Header("Authorization") String token,
                                           @Query("id") String id);

    @GET("/feedback/deleteDislike")
    Observable<ResponseEntity> deleteDislike(@Header("Authorization") String token,
                                             @Query("id") String id);

    @POST("/account/logout")
    Observable<ResponseEntity> logout(@Header("Authorization") String token);

    @GET("/config/getTest")
    Observable<TestUpperEntity> getTest(@Header("Authorization") String token);

    @GET("/favorites/addToFavorites")
    Observable<ResponseEntity> addToFavorites(@Header("Authorization") String token,
                                              @Query("id") String id);

    @GET("/favorites/getAllFavorites")
    Observable<FavoritesUpperEntity> getAllFavorites(@Header("Authorization") String token);

    @POST("/feedback/postComment")
    Observable<ResponseEntity> postComment(@Header("Authorization") String token,
                                           @Query("id") String id,
                                           @Body PostCommentEntity entity);

    @GET("/feedback/getComments")
    Observable<CommentUpperEntity> getComments(@Header("Authorization") String token,
                                               @Query("id") String id,
                                               @Query("count") int count,
                                               @Query("offset") int offset);

    @Multipart
    @POST("/config/postPhoto")
    Observable<ResponseEntity> postPhoto(@Header("Authorization") String token, @Part MultipartBody.Part photo);

    @GET("/account/getMyUsername")
    Observable<UsernameEntity> getMyUsername(@Header("Authorization") String token);

    @GET("/favorites/removeFromFavorites")
    Observable<ResponseEntity> removeFromFavorites(@Header("Authorization") String token, @Query("id") String id);

}
