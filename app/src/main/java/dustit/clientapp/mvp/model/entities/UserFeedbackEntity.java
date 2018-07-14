package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserFeedbackEntity {
    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("message")
    @Expose
    private String message;

    public UserFeedbackEntity() {

    }

    public UserFeedbackEntity(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
