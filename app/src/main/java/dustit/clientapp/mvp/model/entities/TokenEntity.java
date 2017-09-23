
package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenEntity {

    @SerializedName("Token")
    @Expose
    private String token;

    public TokenEntity() {
    }

    public TokenEntity(String token) {
        super();
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
