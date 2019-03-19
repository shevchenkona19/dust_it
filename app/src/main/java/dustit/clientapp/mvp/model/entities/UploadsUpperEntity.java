package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UploadsUpperEntity {
    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("uploads")
    @Expose
    private List<UploadEntity> uploadEntities;
    @SerializedName("error")
    @Expose
    private String error;

    public UploadsUpperEntity(boolean success, List<UploadEntity> uploadEntities, String error) {
        this.success = success;
        this.uploadEntities = uploadEntities;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<UploadEntity> getUploadEntities() {
        return uploadEntities;
    }

    public void setUploadEntities(List<UploadEntity> uploadEntities) {
        this.uploadEntities = uploadEntities;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public UploadsUpperEntity() {
    }
}
