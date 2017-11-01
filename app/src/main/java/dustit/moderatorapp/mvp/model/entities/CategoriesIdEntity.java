package dustit.moderatorapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Никита on 01.11.2017.
 */

public class CategoriesIdEntity {

    @SerializedName("IDs")
    @Expose
    private String ids;

    public CategoriesIdEntity(String ids) {
        this.ids = ids;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
