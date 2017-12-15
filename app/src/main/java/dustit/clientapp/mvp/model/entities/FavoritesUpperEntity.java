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
    }

    public void initList() {
        list = new ArrayList<>();
        if(ids.charAt(0) == ' ') ids = ids.replaceFirst(" ", "");
        if (ids.contains(" ")) {
            String[] idArr = ids.split(" ");
            for (String s :
                    idArr) {
                list.add(new FavoriteEntity(s));
            }
        } else {
            if (ids.length() > 0) {
                list.add(new FavoriteEntity(ids));
            }
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
}
