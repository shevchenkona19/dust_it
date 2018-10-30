package dustit.clientapp.mvp.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

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

    @SerializedName("achievementName")
    @Expose
    private String achievementName;

    @SerializedName("allNames")
    @Expose
    private List<String> allAchievementNames;

    public Achievement() {}

    public Achievement(int lvl, int count, int nextPrice, boolean isFinalLevel, String name, String achievementName, List<String> allAchievementNames) {
        this.lvl = lvl;
        this.count = count;
        this.nextPrice = nextPrice;
        this.isFinalLevel = isFinalLevel;
        this.name = name;
        this.achievementName = achievementName;
        this.allAchievementNames = allAchievementNames;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getAchievementName() {
        return achievementName;
    }

    public void setAchievementName(String achievementName) {
        this.achievementName = achievementName;
    }

    public List<String> getAllAchievementNames() {
        return allAchievementNames;
    }

    public void setAllAchievementNames(List<String> allAchievementNames) {
        this.allAchievementNames = allAchievementNames;
    }
}
