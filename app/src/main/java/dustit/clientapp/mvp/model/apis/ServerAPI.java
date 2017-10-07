package dustit.clientapp.mvp.model.apis;

import dustit.clientapp.mvp.model.entities.LoginUserEntity;
import dustit.clientapp.mvp.model.entities.MemUpperEntity;
import dustit.clientapp.mvp.model.entities.RegisterUserEntity;
import dustit.clientapp.mvp.model.entities.TokenEntity;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by shevc on 22.09.2017.
 * Let's GO!
 */

public interface ServerAPI {

    @POST("/moderator/users/register/")
    Observable<TokenEntity> registerUser(@Body RegisterUserEntity userEntity);

    @POST("/moderator/users/login/")
    Observable<TokenEntity> loginUser(@Body LoginUserEntity loginUserEntity);

    @GET("/client/getFeed")
    Observable<MemUpperEntity> getFeed(@Query("token") String token, @Query("count") int count,
                                       @Query("offset") int offset);
    @POST("/client/postLike")
    void postLike(@Query("token") String token, @Query("id") String id);

    @GET("/client/getMem")
    Observable<MemUpperEntity> getMem(@Query("token")String token, @Query("id") String id);

}
