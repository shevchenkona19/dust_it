package dustit.moderatorapp.mvp.model.api;

import dustit.moderatorapp.mvp.model.entities.LoginUserEntity;
import dustit.moderatorapp.mvp.model.entities.TokenEntity;
import dustit.moderatorapp.mvp.model.entities.UserEntity;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by shevc on 15.09.2017.
 * Let's GO!
 */

public interface ServerAPI {

    @POST("/moderator/users/register/")
    Observable<TokenEntity> registerUser(@Body UserEntity userEntity);

    @POST("/moderator/users/login/")
    Observable<TokenEntity> loginUser(@Body LoginUserEntity loginUserEntity);
}
