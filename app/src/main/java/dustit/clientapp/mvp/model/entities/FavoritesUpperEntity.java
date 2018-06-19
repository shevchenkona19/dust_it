package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FavoritesUpperEntity {
    @SerializedName("favorites")
    @Expose
    private FavoriteEntity[] ids;

    public FavoritesUpperEntity(FavoriteEntity[] ids) {
        this.ids = ids;
    }

    public String[] getIds() {
        String[] strings = new String[ids.length];
        for(int i = 0; i< strings.length; i++) {
            strings[i] = ids[i].getId();
        }
        return strings;
    }
}
