package dustit.clientapp.mvp.model.entities;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import dustit.clientapp.utils.IConstants;

public class UploadEntity {
    @SerializedName("imageId")
    @Expose
    private int imageId;
    @SerializedName("likes")
    @Expose
    private int likes;
    @SerializedName("dislikes")
    @Expose
    private int dislikes;
    @SerializedName("opinion")
    @Expose
    private String opinion;
    @SerializedName("comments_count")
    @Expose
    private int commentsCount;
    @SerializedName("isFavourite")
    @Expose
    private boolean isFavourite;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("userId")
    @Expose
    private int userId = -1;
    @SerializedName("uploadDate")
    @Expose
    private String uploadDate;

    public UploadEntity() {
    }

    public UploadEntity(int imageId, int likes, int dislikes, String opinion, int commentsCount, boolean isFavourite, String username, int userId, String uploadDate) {
        this.imageId = imageId;
        this.likes = likes;
        this.dislikes = dislikes;
        this.opinion = opinion;
        this.commentsCount = commentsCount;
        this.isFavourite = isFavourite;
        this.username = username;
        this.userId = userId;
        this.uploadDate = uploadDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void addLikes(int num) {
        likes += num;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public void addDislikes(int num) {
        dislikes += num;
    }

    @NonNull
    public IConstants.OPINION getOpinion() {
        if (opinion != null)
            switch (opinion) {
                case "0":
                    return IConstants.OPINION.DISLIKED;
                case "1":
                    return IConstants.OPINION.LIKED;
                default:
                    return IConstants.OPINION.NEUTRAL;
            }
        else return IConstants.OPINION.NEUTRAL;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public void setOpinion(IConstants.OPINION opinion) {
        switch (opinion) {
            case LIKED:
                this.opinion = "1";
                break;
            case DISLIKED:
                this.opinion = "0";
                break;
            case NEUTRAL:
                this.opinion = "null";
                break;
        }
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public MemEntity toMemEntity() {
        MemEntity mem = new MemEntity();
        mem.setId(imageId);
        mem.setLikes(likes);
        mem.setDislikes(dislikes);
        mem.setOpinion(opinion);
        mem.setCommentsCount(commentsCount);
        mem.setFavorite(isFavourite);
        mem.setUserId(userId);
        mem.setUploadDate(uploadDate);
        mem.setUsername(username);
        return mem;
    }
}
