package dustit.clientapp.mvp.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shevc on 04.10.2017.
 * Let's GO!
 */

public class ThemeEntity implements Parcelable {
    private String name;
    private boolean checked;


    public ThemeEntity(String name, boolean checked) {
        this.name = name;
        this.checked = checked;
    }

    protected ThemeEntity(Parcel in) {
        name = in.readString();
        checked = in.readByte() != 0;
    }

    public static final Creator<ThemeEntity> CREATOR = new Creator<ThemeEntity>() {
        @Override
        public ThemeEntity createFromParcel(Parcel in) {
            return new ThemeEntity(in);
        }

        @Override
        public ThemeEntity[] newArray(int size) {
            return new ThemeEntity[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeByte((byte) (checked ? 1 : 0));
    }
}
