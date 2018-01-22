package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class TestUpperEntity {
    @SerializedName("test")
    @Expose
    private List<TestMemEntity> list;

    public TestUpperEntity(List<TestMemEntity> list) {
        this.list = list;
    }

    public List<TestMemEntity> getList() {
        return list;
    }

    public void setList(List<TestMemEntity> list) {
        this.list = list;
    }
}
