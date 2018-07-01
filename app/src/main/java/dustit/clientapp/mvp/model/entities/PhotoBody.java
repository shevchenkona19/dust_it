package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotoBody {

    @SerializedName("photo")
    @Expose
    private String photo;

    @SerializedName("mime")
    @Expose
    private String ext;

    public PhotoBody() {
    }

    public PhotoBody(String photo, String ext) {
        this.photo = photo;
        this.ext = ext;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
