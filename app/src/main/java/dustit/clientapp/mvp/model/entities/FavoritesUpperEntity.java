package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FavoritesUpperEntity {
    @SerializedName("IDs")
    @Expose
    private String ids;

    private List<FavoriteEntity> list;

    public FavoritesUpperEntity(String ids) {
        this.ids = ids;
        list = new ArrayList<>();
        String[] idArr = ids.split(" ");
        for (String s :
                idArr) {
            list.add(new FavoriteEntity(s));
        }
    }

    public List<FavoriteEntity> getList() {
        return list;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public static class FavoriteEntity {
        private String id;

        public FavoriteEntity(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
