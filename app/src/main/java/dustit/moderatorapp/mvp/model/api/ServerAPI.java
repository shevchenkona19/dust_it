package dustit.moderatorapp.mvp.model.api;

import dustit.moderatorapp.mvp.model.entities.CategoriesIdEntity;
import dustit.moderatorapp.mvp.model.entities.CategoryEntity;
import dustit.moderatorapp.mvp.model.entities.LoginUserEntity;
import dustit.moderatorapp.mvp.model.entities.MemIdEntity;
import dustit.moderatorapp.mvp.model.entities.ResponseCode;
import dustit.moderatorapp.mvp.model.entities.TokenEntity;
import dustit.moderatorapp.mvp.model.entities.UserEntity;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by shevc on 15.09.2017.
 * Let's GO!
 */

public interface ServerAPI {

    @POST("/moderator/register/")
    Observable<TokenEntity> registerUser(@Body UserEntity userEntity);

    @POST("/moderator/login/")
    Observable<TokenEntity> loginUser(@Body LoginUserEntity loginUserEntity);

    @GET("/moderator/getCategories")
    Observable<CategoryEntity> getCategories(@Query("token") String token);

    @POST("/moderator/postMem")
    Observable<ResponseCode> postMem(@Query("token") String token, @Query("id") String id, @Body CategoriesIdEntity categoriesIdEntity);

    @GET("/moderator/getNewMem")
    Observable<MemIdEntity> getNewMem(@Query("token") String token);

    @POST("/moderator/discardMem")
    Observable<ResponseCode> discardMem(@Query("token") String token, @Query("id") String id);

    @POST("/moderator/logout")
    Observable<ResponseCode> logout(@Query("token") String token);


}
