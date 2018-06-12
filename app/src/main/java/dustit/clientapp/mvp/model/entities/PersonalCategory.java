package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 08.02.2018.
 */

public class PersonalCategory {
    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("categoryIsUsed")
    @Expose
    private boolean isCategoryUsed;
    @SerializedName("categoryId")
    @Expose
    private String categoryId;

    public PersonalCategory(String categoryName, boolean isCategoryUsed, String categoryId) {
        this.categoryName = categoryName;
        this.isCategoryUsed = isCategoryUsed;
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean getIsCategoryUsed() {
        return isCategoryUsed;
    }

    public void setIsCategoryUsed(boolean isCategoryUsed) {
        this.isCategoryUsed = isCategoryUsed;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isChecked() {
        return isCategoryUsed;
    }

    public void setChecked(boolean checked) {
        isCategoryUsed = checked;
    }
}
