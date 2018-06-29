
package dustit.clientapp.mvp.model.entities;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MemUpperEntity {

    @SerializedName("memes")
    @Expose
    private List<MemEntity> memEntities = new ArrayList<>();
    @SerializedName("message")
    @Expose
    private String message = "";

    /**
     * No args constructor for use in serialization
     * 
     */
    public MemUpperEntity() {
    }

    public MemUpperEntity(List<MemEntity> memEntities, String message) {
        this.memEntities = memEntities;
        this.message = message;
    }

    public List<MemEntity> getMemEntities() {
        return memEntities;
    }

    public void setMemEntities(List<MemEntity> memEntities) {
        this.memEntities = memEntities;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
