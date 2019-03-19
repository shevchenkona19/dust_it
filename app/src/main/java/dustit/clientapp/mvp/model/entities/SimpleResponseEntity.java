package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SimpleResponseEntity {
    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("error")
    @Expose
    private String error;

    public SimpleResponseEntity() {
    }

    public SimpleResponseEntity(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }
}
