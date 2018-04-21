package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 08.02.2018.
 */

public class PersonalCategory {
    @SerializedName("categoryName")
    @Expose
    private
    String categoryName;
    @SerializedName("categoryIsUsed")
    @Expose
    private
    String isCategoryUsed;
    @SerializedName("categoryId")
    @Expose
    private
    String categoryId;

    public PersonalCategory(String categoryName, String isCategoryUsed, String categoryId) {
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

    public String getIsCategoryUsed() {
        return isCategoryUsed;
    }

    public void setIsCategoryUsed(String isCategoryUsed) {
        this.isCategoryUsed = isCategoryUsed;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isChecked() {
        switch (isCategoryUsed) {
            case "0":
                return false;
            case "1":
                return true;
            default:
                return false;
        }
    }

    public void setChecked(boolean checked) {
        if (checked) {
            isCategoryUsed = "1";
        } else {
            isCategoryUsed = "0";
        }
    }
}
