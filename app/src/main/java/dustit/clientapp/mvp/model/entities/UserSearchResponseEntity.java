package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserSearchResponseEntity {
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("foundUsers")
    @Expose
    private List<UserEntity> users;

    public UserSearchResponseEntity() {
    }

    public UserSearchResponseEntity(boolean success, List<UserEntity> users) {
        this.success = success;
        this.users = users;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<UserEntity> getUsers() {
        return users;
    }
}
