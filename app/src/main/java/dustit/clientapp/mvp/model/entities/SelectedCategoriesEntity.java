package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 08.02.2018.
 */

public class SelectedCategoriesEntity {

    @SerializedName("Ids")
    @Expose
    private String[] ids;

    public SelectedCategoriesEntity(String[] ids) {
        this.ids = ids;
    }

    public String[] getIds() {
        return ids;
    }

    public void setIds(String[] ids) {
        this.ids = ids;
    }
}
