package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by User on 28.02.2018.
 */

public class PostSelectedCategoriesUpperEntity {

    @SerializedName("Ids")
    @Expose
    private String[] ids;

    public String[] getCategories() {
        return ids;
    }

    public void setCategories(String[] ids) {
        this.ids = ids;
    }

    public PostSelectedCategoriesUpperEntity(String[] categoryIds) {
        ids = categoryIds;
    }
}
