package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Никита on 09.11.2017.
 */

public class CommentUpperEntity {
    @SerializedName("Comments")
    @Expose
    private List<CommentEntity> list;

    @SerializedName("Count")
    @Expose
    private int count;

    public CommentUpperEntity(List<CommentEntity> list, int count) {
        this.list = list;
        this.count = count;
    }

    public List<CommentEntity> getList() {
        return list;
    }

    public void setList(List<CommentEntity> list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
