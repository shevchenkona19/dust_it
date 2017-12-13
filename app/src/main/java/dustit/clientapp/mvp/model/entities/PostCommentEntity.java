package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Никита on 09.11.2017.
 */

public class PostCommentEntity {
    @SerializedName("Text")
    @Expose
    private String text;

    @SerializedName("Date")
    @Expose
    private String date;

    @SerializedName("Time")
    @Expose
    private String time;

    public PostCommentEntity(String text, String date, String time) {
        this.text = text;
        this.date = date;
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
