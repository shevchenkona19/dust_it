package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FavoritesUpperEntity {
    @SerializedName("favorites")
    @Expose
    private List<MemEntity> memEntities;

    public FavoritesUpperEntity(List<MemEntity> ids) {
        this.memEntities = ids;
    }

    public int getLength() { return memEntities.size(); }

    public List<MemEntity> getList() { return memEntities; }
}
