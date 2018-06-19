package dustit.clientapp.mvp.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TestMemEntity implements Parcelable {
    @SerializedName("imageId")
    @Expose
    private String memId;

    @SerializedName("categoryName")
    @Expose
    private String categoryName;

    public TestMemEntity(String memId, String categoryName) {
        this.memId = memId;
        this.categoryName = categoryName;
    }

    protected TestMemEntity(Parcel in) {
        memId = in.readString();
        categoryName = in.readString();
    }

    public static final Creator<TestMemEntity> CREATOR = new Creator<TestMemEntity>() {
        @Override
        public TestMemEntity createFromParcel(Parcel in) {
            return new TestMemEntity(in);
        }

        @Override
        public TestMemEntity[] newArray(int size) {
            return new TestMemEntity[size];
        }
    };

    public String getMemId() {
        return memId;
    }

    public void setMemId(String memId) {
        this.memId = memId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(memId);
        parcel.writeString(categoryName);
    }
}
