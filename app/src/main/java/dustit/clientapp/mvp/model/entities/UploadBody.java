package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadBody {
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("categories")
    @Expose
    private String categories;

    public UploadBody() {}

    public UploadBody(String photo, String categories) {
        this.photo = photo;
        this.categories = categories;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }
}
