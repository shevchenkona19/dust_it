package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Никита on 09.11.2017.
 */

public class CommentEntity {
    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("date")
    @Expose
    private String dateOfPost;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("time")
    @Expose
    private String time;

    public CommentEntity(String text, String dateOfPost, String username, String time) {
        this.text = text;
        this.dateOfPost = dateOfPost;
        this.username = username;
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDateOfPost() {
        return dateOfPost;
    }

    public void setDateOfPost(String dateOfPost) {
        this.dateOfPost = dateOfPost;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
