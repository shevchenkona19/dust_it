package dustit.clientapp.mvp.model.apis;

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
import retrofit2.http.Body;
import retrofit2.http.GET;
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

    @POST("/client/register/")
    Observable<TokenEntity> registerUser(@Body RegisterUserEntity userEntity);

    @POST("/client/login/")
    Observable<TokenEntity> loginUser(@Body LoginUserEntity loginUserEntity);

    @GET("/client/getFeed")
    Observable<MemUpperEntity> getFeed(@Query("token") String token,
                                       @Query("count") int count,
                                       @Query("offset") int offset);

    @GET("/client/getHotFeed")
    Observable<MemUpperEntity> getHot(@Query("token") String token,
                                      @Query("count") int count,
                                      @Query("offset") int offset);

    @GET("/client/getCategoryFeed")
    Observable<MemUpperEntity> getCategoriesFeed(@Query("token") String token,
                                                 @Query("category") String categoryId,
                                                 @Query("count") int count,
                                                 @Query("offset") int offset);

    @GET("/client/getCategories")
    Observable<CategoryEntity> getCategories(@Query("token") String token);

    @POST("/client/postSelectedCategories")
    Observable<ResponseEntity> postSelectedCategories(@Query("token") String token,
                                                      @Body CategoryIdEntity entity);

    @GET("/client/getPersonalCategories")
    Observable<CategoryIdEntity> getPersonalCategories(@Query("token") String token);

    @POST("/client/postLike")
    Observable<ResponseEntity> postLike(@Query("token") String token,
                                        @Query("id") String id);

    @POST("/client/deleteLike")
    Observable<ResponseEntity> deleteLike(@Query("token") String token,
                                          @Query("id") String id);

    @POST("/client/postDislike")
    Observable<ResponseEntity> postDislike(@Query("token") String token,
                                           @Query("id") String id);

    @POST("/client/deleteDislike")
    Observable<ResponseEntity> deleteDislike(@Query("token") String token,
                                             @Query("id") String id);

    @POST("/client/logout")
    Observable<ResponseEntity> logout(@Query("token") String token);

    @GET("/client/getTest")
    Observable<TestUpperEntity> getTest(@Query("token") String token);

    @POST("/client/addToFavorites")
    Observable<ResponseEntity> addToFavorites(@Query("token") String token,
                                              @Query("id") String id);

    @GET("/client/getAllFavorites")
    Observable<FavoritesUpperEntity> getAllFavorites(@Query("token") String token);

    @POST("/client/postComment")
    Observable<ResponseEntity> postComment(@Query("token") String token,
                                           @Query("id") String id,
                                           @Body PostCommentEntity entity);

    @GET("/client/getComments")
    Observable<CommentUpperEntity> getComments(@Query("token") String token,
                                               @Query("id") String id,
                                               @Query("count") int count,
                                               @Query("offset") int offset);

    @Multipart
    @POST("/client/postPhoto")
    Observable<ResponseEntity> postPhoto(@Query("token") String token, @Part MultipartBody.Part photo);

    @GET("/client/getMyUsername")
    Observable<UsernameEntity> getMyUsername(@Query("token") String token);

    @POST("/client/removeFromFavorites")
    Observable<ResponseEntity> removeFromFavorites(@Query("token") String token, @Query("id") String id);

}
