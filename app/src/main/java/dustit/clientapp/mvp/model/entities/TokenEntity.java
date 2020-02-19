
package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenEntity {

    @SerializedName("token")
    @Expose
    private String token = "";
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("message")
    @Expose
    private String message = "";

    public TokenEntity() {
    }

    public TokenEntity(String token, int id, String message) {
        this.token = token;
        this.message = message;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
