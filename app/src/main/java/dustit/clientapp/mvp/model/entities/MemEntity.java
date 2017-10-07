
package dustit.clientapp.mvp.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MemEntity implements Parcelable {

    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("Url")
    @Expose
    private String url;
    @SerializedName("Text")
    @Expose
    private String text;
    @SerializedName("Likes")
    @Expose
    private String likes;
    @SerializedName("IsLiked")
    @Expose
    private boolean isLiked;

    /**
     * No args constructor for use in serialization
     */
    public MemEntity() {
    }

    /**
     * @param id
     * @param text
     * @param likes
     * @param url
     */
    public MemEntity(String id, String url, String text, String likes, boolean isLiked) {
        super();
        this.id = id;
        this.url = url;
        this.text = text;
        this.likes = likes;
        this.isLiked = isLiked;
    }

    protected MemEntity(Parcel in) {
        id = in.readString();
        url = in.readString();
        text = in.readString();
        likes = in.readString();
        isLiked = in.readByte() != 0;
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
        parcel.writeByte((byte) (isLiked ? 1 : 0));
    }
}
