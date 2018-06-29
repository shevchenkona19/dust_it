package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Никита on 15.12.2017.
 */

public class FavoriteEntity {
    @SerializedName("imageId")
    @Expose
    private String id;

    public FavoriteEntity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
