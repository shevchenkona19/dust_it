package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import dustit.clientapp.utils.L;

/**
 * Created by shevc on 15.10.2017.
 * Let's GO!
 */

public class ResponseEntity {

    @SerializedName("message")
    @Expose
    private int response;

    @SerializedName("achievementUpdate")
    @Expose
    private boolean achievementUpdate;

    @SerializedName("achievement")
    @Expose
    private NewAchievementEntity achievementEntity;

    @SerializedName("newCommentId")
    @Expose
    private String newCommentId;

    public ResponseEntity(int response, boolean achievementUpdate, NewAchievementEntity achievementEntity) {
        this.response = response;
        this.achievementUpdate = achievementUpdate;
        this.achievementEntity = achievementEntity;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public boolean isAchievementUpdate() {
        return achievementUpdate;
    }

    public void setAchievementUpdate(boolean achievementUpdate) {
        this.achievementUpdate = achievementUpdate;
    }

    public NewAchievementEntity getAchievementEntity() {
        return achievementEntity;
    }

    public void setAchievementEntity(NewAchievementEntity achievementEntity) {
        this.achievementEntity = achievementEntity;
    }

    public String getNewCommentId() {
        return newCommentId;
    }

    public void setNewCommentId(String newCommentId) {
        this.newCommentId = newCommentId;
    }
}
