package dustit.clientapp.mvp.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TestMemEntity implements Parcelable {
    @SerializedName("Id")
    @Expose
    private String memId;

    @SerializedName("CategoryId")
    @Expose
    private String categoryId;

    public TestMemEntity(String memId, String categoryId) {
        this.memId = memId;
        this.categoryId = categoryId;
    }

    protected TestMemEntity(Parcel in) {
        memId = in.readString();
        categoryId = in.readString();
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(memId);
        parcel.writeString(categoryId);
    }
}
