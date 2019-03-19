package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReferralInfoEntity {
    @SerializedName("userId")
    @Expose
    String userId;
    @SerializedName("myCode")
    @Expose
    String myCode;
    @SerializedName("usedCode")
    @Expose
    String usedCode;

    public ReferralInfoEntity(String userId, String myCode, String usedCode) {
        this.userId = userId;
        this.myCode = myCode;
        this.usedCode = usedCode;
    }

    public ReferralInfoEntity() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMyCode() {
        return myCode;
    }

    public void setMyCode(String myCode) {
        this.myCode = myCode;
    }

    public String getUsedCode() {
        return usedCode;
    }

    public void setUsedCode(String usedCode) {
        this.usedCode = usedCode;
    }
}
