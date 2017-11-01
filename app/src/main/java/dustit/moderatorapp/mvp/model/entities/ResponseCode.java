package dustit.moderatorapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Никита on 01.11.2017.
 */

public class ResponseCode {

    @SerializedName("ResponseCode")
    @Expose
    private int code;

    public ResponseCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
