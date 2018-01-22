
package dustit.clientapp.mvp.model.entities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MemUpperEntity {

    @SerializedName("memes")
    @Expose
    private List<MemEntity> memEntities = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public MemUpperEntity() {
    }

    /**
     * 
     * @param memEntities
     */
    public MemUpperEntity(List<MemEntity> memEntities) {
        super();
        this.memEntities = memEntities;
    }

    public List<MemEntity> getMemEntities() {
        return memEntities;
    }

    public void setMemEntities(List<MemEntity> memEntities) {
        this.memEntities = memEntities;
    }

}
