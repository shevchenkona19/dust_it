package dustit.moderatorapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Никита on 01.11.2017.
 */

public class MemIdEntity {

    @SerializedName("Id")
    @Expose
    private int id;

    public MemIdEntity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
