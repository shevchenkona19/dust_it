package dustit.clientapp.mvp.model.entities;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

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
    private int id;
    @SerializedName("likes")
    @Expose
    private int likes;
    @SerializedName("dislikes")
    @Expose
    private int dislikes;
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
    private int userId = -1;
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
        id = in.readInt();
        likes = in.readInt();
        dislikes = in.readInt();
        opinion = in.readString();
        favorite = in.readByte() != 0;
        source = in.readString();
        width = in.readInt();
        height = in.readInt();
        commentsCount = in.readInt();
        userId = in.readInt();
        uploadDate = in.readString();
        username = in.readString();
    }

    public MemEntity(int id, int likes, int dislikes, String opinion, boolean favorite, String source, int width, int height, int commentsCount, int userId, String uploadDate, String username) {
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
        dest.writeInt(id);
        dest.writeInt(likes);
        dest.writeInt(dislikes);
        dest.writeString(opinion);
        dest.writeByte((byte) (favorite ? 1 : 0));
        dest.writeString(source);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeInt(commentsCount);
        dest.writeInt(userId);
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

    public int getDislikes() {
        return dislikes;
    }

    public void addDislikes(int num) {
        dislikes = dislikes + num;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public int getLikes() {
        return likes;
    }

    public void addLikes(int num) {
        likes = likes + num;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
