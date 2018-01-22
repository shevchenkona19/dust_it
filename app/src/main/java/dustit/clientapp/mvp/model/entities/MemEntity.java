
package dustit.clientapp.mvp.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MemEntity implements Parcelable {

    @SerializedName("imageid")
    @Expose
    private String id;
    @SerializedName("Url")
    @Expose
    private String url;
    @SerializedName("Text")
    @Expose
    private String text;
    @SerializedName("likes")
    @Expose
    private String likes;
    @SerializedName("dislikes")
    @Expose
    private String dislikes;
    @SerializedName("IsDisliked")
    @Expose
    private boolean isDisliked;
    @SerializedName("IsLiked")
    @Expose
    private boolean isLiked;
    @SerializedName("IsFavorite")
    @Expose
    private boolean favorite;

    /**
     * No args constructor for use in serialization
     */
    public MemEntity() {
    }

    public MemEntity(String id, String url, String text, String likes, String dislikes, boolean isDisliked, boolean isLiked, boolean favorite) {
        this.id = id;
        this.url = url;
        this.text = text;
        this.likes = likes;
        this.dislikes = dislikes;
        this.isDisliked = isDisliked;
        this.isLiked = isLiked;
        this.favorite = favorite;
    }

    protected MemEntity(Parcel in) {
        id = in.readString();
        url = in.readString();
        text = in.readString();
        likes = in.readString();
        dislikes = in.readString();
        isDisliked = in.readByte() != 0;
        isLiked = in.readByte() != 0;
        favorite = in.readByte() != 0;
    }

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

    public String getDislikes() {
        return dislikes;
    }

    public void setDislikes(String dislikes) {
        this.dislikes = dislikes;
    }

    public boolean isDisliked() {
        return isDisliked;
    }

    public void setDisliked(boolean disliked) {
        isDisliked = disliked;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLikes() {
        return likes;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(url);
        parcel.writeString(text);
        parcel.writeString(likes);
        parcel.writeString(dislikes);
        parcel.writeByte((byte) (isDisliked ? 1 : 0));
        parcel.writeByte((byte) (isLiked ? 1 : 0));
        parcel.writeByte((byte) (favorite ? 1 : 0));
    }
}
