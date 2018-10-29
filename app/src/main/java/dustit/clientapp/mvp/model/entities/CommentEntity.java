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

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("time")
    @Expose
    private String time;

    @SerializedName("likeAchievementLvl")
    @Expose
    private int likeAchievementLvl;

    @SerializedName("dislikesAchievementLvl")
    @Expose
    private int dislikesAchievementLvl;

    @SerializedName("commentsAchievementLvl")
    @Expose
    private int commentsAchievementLvl;

    @SerializedName("favouritesAchievementLvl")
    @Expose
    private int favouritesAchievementLvl;

    @SerializedName("viewsAchievementLvl")
    @Expose
    private int viewsAchievementLvl;

    @SerializedName("firstHundred")
    @Expose
    private boolean firstHundred;

    @SerializedName("firstThousand")
    @Expose
    private boolean firstThousand;


    public CommentEntity(String text, String dateOfPost, String username, String userId, String time, int likeAchievementLvl, int dislikesAchievementLvl, int commentsAchievementLvl, int favouritesAchievementLvl, int viewsAchievementLvl, boolean firstHundred, boolean firstThousand) {
        this.text = text;
        this.dateOfPost = dateOfPost;
        this.username = username;
        this.userId = userId;
        this.time = time;
        this.likeAchievementLvl = likeAchievementLvl;
        this.dislikesAchievementLvl = dislikesAchievementLvl;
        this.commentsAchievementLvl = commentsAchievementLvl;
        this.favouritesAchievementLvl = favouritesAchievementLvl;
        this.viewsAchievementLvl = viewsAchievementLvl;
        this.firstHundred = firstHundred;
        this.firstThousand = firstThousand;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getLikeAchievementLvl() {
        return likeAchievementLvl;
    }

    public void setLikeAchievementLvl(int likeAchievementLvl) {
        this.likeAchievementLvl = likeAchievementLvl;
    }

    public int getDislikesAchievementLvl() {
        return dislikesAchievementLvl;
    }

    public void setDislikesAchievementLvl(int dislikesAchievementLvl) {
        this.dislikesAchievementLvl = dislikesAchievementLvl;
    }

    public int getCommentsAchievementLvl() {
        return commentsAchievementLvl;
    }

    public void setCommentsAchievementLvl(int commentsAchievementLvl) {
        this.commentsAchievementLvl = commentsAchievementLvl;
    }

    public int getFavouritesAchievementLvl() {
        return favouritesAchievementLvl;
    }

    public void setFavouritesAchievementLvl(int favouritesAchievementLvl) {
        this.favouritesAchievementLvl = favouritesAchievementLvl;
    }

    public int getViewsAchievementLvl() {
        return viewsAchievementLvl;
    }

    public void setViewsAchievementLvl(int viewsAchievementLvl) {
        this.viewsAchievementLvl = viewsAchievementLvl;
    }

    public boolean getFirstHundred() {
        return firstHundred;
    }

    public void setFirstHundred(boolean firstHundred) {
        this.firstHundred = firstHundred;
    }

    public boolean getFirstThousand() {
        return firstThousand;
    }

    public void setFirstThousand(boolean firstThousand) {
        this.firstThousand = firstThousand;
    }
}
