package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Никита on 03.11.2017.
 */

public class CategoryIdEntity {

    @SerializedName("Ids")
    @Expose
    private String ids;

    public CategoryIdEntity(String ids) {
        this.ids = ids;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
