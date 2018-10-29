package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewAchievementEntity {
    @SerializedName("newLvl")
    @Expose
    private int newLevel;

    @SerializedName("nextPrice")
    @Expose
    private int nextPrice;

    @SerializedName("currentValue")
    @Expose
    private int currentValue;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("achievementName")
    @Expose
    private String achievementName;

    @SerializedName("isFinalLevel")
    @Expose
    private boolean isFinalLevel;

    public NewAchievementEntity(int newLevel, int nextPrice, int currentValue, String name, String achievementName, boolean isFinalLevel) {
        this.newLevel = newLevel;
        this.nextPrice = nextPrice;
        this.currentValue = currentValue;
        this.name = name;
        this.achievementName = achievementName;
        this.isFinalLevel = isFinalLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public void setNewLevel(int newLevel) {
        this.newLevel = newLevel;
    }

    public int getNextPrice() {
        return nextPrice;
    }

    public void setNextPrice(int nextPrice) {
        this.nextPrice = nextPrice;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
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

    public boolean isFinalLevel() {
        return isFinalLevel;
    }

    public void setFinalLevel(boolean finalLevel) {
        isFinalLevel = finalLevel;
    }
}
