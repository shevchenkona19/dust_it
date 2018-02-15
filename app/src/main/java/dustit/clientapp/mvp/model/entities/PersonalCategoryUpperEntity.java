package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Никита on 03.11.2017.
 */

public class PersonalCategoryUpperEntity {

    @SerializedName("categories")
    @Expose
    private List<PersonalCategory> categories;

    public PersonalCategoryUpperEntity(List<PersonalCategory> ids) {
        this.categories = ids;
    }

    public List<PersonalCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<PersonalCategory> ids) {
        this.categories = ids;
    }
}
