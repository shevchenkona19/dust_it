package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Achievement {
    @SerializedName("lvl")
    @Expose
    private int lvl;

    @SerializedName("count")
    @Expose
    private int count;

    @SerializedName("nextPrice")
    @Expose
    private int nextPrice;

    @SerializedName("isFinalLevel")
    @Expose
    private boolean isFinalLevel;

    @SerializedName("name")
    @Expose
    private String name;

    public Achievement(int lvl, int count, int nextPrice, boolean isFinalLevel, String name) {
        this.lvl = lvl;
        this.count = count;
        this.nextPrice = nextPrice;
        this.isFinalLevel = isFinalLevel;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getNextPrice() {
        return nextPrice;
    }

    public void setNextPrice(int nextPrice) {
        this.nextPrice = nextPrice;
    }

    public boolean isFinalLevel() {
        return isFinalLevel;
    }

    public void setFinalLevel(boolean finalLevel) {
        isFinalLevel = finalLevel;
    }

    public String getName() {
        return name;
    }
}
