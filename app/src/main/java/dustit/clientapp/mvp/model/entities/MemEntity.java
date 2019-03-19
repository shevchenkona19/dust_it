package dustit.clientapp.mvp.model.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import dustit.clientapp.utils.IConstants;

public class MemEntity implements Parcelable {
    public static final Creator<MemEntity> CREATOR = new Creator<MemEntity>() {
        @Override
        public MemEntity createFromParcel(Parcel in) {
            return new MemEntity(in);
        }

        @Override
        public MemEntity[] newArray(int size) {
            return new MemEntity[size];
        }
    };
    @SerializedName("imageId")
    @Expose
    private String id;
    @SerializedName("likes")
    @Expose
    private String likes;
    @SerializedName("dislikes")
    @Expose
    private String dislikes;
    @SerializedName("opinion")
    @Expose
    private String opinion;
    @SerializedName("isFavourite")
    @Expose
    private boolean favorite;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("width")
    @Expose
    private int width;
    @SerializedName("height")
    @Expose
    private int height;
    @SerializedName("comments_count")
    @Expose
    private int commentsCount;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("uploadDate")
    @Expose
    private String uploadDate;
    @SerializedName("username")
    @Expose
    private String username;

    /**
     * No args constructor for use in serialization
     */
    public MemEntity() {
    }

    protected MemEntity(Parcel in) {
        id = in.readString();
        likes = in.readString();
        dislikes = in.readString();
        opinion = in.readString();
        favorite = in.readByte() != 0;
        source = in.readString();
        width = in.readInt();
        height = in.readInt();
        commentsCount = in.readInt();
        userId = in.readString();
        uploadDate = in.readString();
        username = in.readString();
    }

    public MemEntity(String id, String likes, String dislikes, String opinion, boolean favorite, String source, int width, int height, int commentsCount, String userId, String uploadDate, String username) {
        this.id = id;
        this.likes = likes;
        this.dislikes = dislikes;
        this.opinion = opinion;
        this.favorite = favorite;
        this.source = source;
        this.width = width;
        this.height = height;
        this.commentsCount = commentsCount;
        this.userId = userId;
        this.uploadDate = uploadDate;
        this.username = username;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(likes);
        dest.writeString(dislikes);
        dest.writeString(opinion);
        dest.writeByte((byte) (favorite ? 1 : 0));
        dest.writeString(source);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeInt(commentsCount);
        dest.writeString(userId);
        dest.writeString(uploadDate);
        dest.writeString(username);
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getDislikes() {
        return dislikes;
    }

    public void setDislikes(int num) {
        dislikes = String.valueOf(Integer.parseInt(dislikes) + num);
    }

    public void setDislikes(String dislikes) {
        this.dislikes = dislikes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(int num) {
        likes = String.valueOf(Integer.parseInt(likes) + num);
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @NonNull
    public IConstants.OPINION getOpinion() {
        if (opinion != null) {
            switch (opinion) {
                case "0":
                    return IConstants.OPINION.DISLIKED;
                case "1":
                    return IConstants.OPINION.LIKED;
                default:
                    return IConstants.OPINION.NEUTRAL;
            }
        } else {
            return IConstants.OPINION.NEUTRAL;
        }
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
