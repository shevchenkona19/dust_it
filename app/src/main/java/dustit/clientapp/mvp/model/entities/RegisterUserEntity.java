
package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterUserEntity {

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("referral")
    @Expose
    private String referral;

    /**
     * No args constructor for use in serialization
     * 
     */
    public RegisterUserEntity() {
    }

    /**
     *  @param username
     * @param password
     * @param email
     * @param referralCode
     */
    public RegisterUserEntity(String username, String password, String email, String referralCode) {
        super();
        this.username = username;
        this.password = password;
        this.email = email;
        this.referral = referralCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
