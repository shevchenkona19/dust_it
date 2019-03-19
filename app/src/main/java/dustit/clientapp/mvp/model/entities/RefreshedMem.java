package dustit.clientapp.mvp.model.entities;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import dustit.clientapp.utils.IConstants;

public class RefreshedMem {
    @SerializedName("likes")
    @Expose
    private String likes;

    @SerializedName("dislikes")
    @Expose
    private String dislikes;

    @SerializedName("opinion")
    @Expose
    private String opinion;

    @SerializedName("isFavourite")
    @Expose
    private boolean isFavourite;

    @SerializedName("achievementUpdate")
    @Expose
    private boolean achievementUpdate;

    @SerializedName("achievement")
    @Expose
    private NewAchievementEntity achievementEntity;

    private String id;

    public RefreshedMem(String likes, String dislikes, String opinion, boolean achievementUpdate, NewAchievementEntity achievementEntity, String id, boolean isFavourite) {
        this.likes = likes;
        this.dislikes = dislikes;
        this.opinion = opinion;
        this.achievementUpdate = achievementUpdate;
        this.achievementEntity = achievementEntity;
        this.id = id;
        this.isFavourite = isFavourite;
    }

    public RefreshedMem() {
    }

    public String getLikes() {
        return likes;
    }

    public int getParsedLikes() {
        return Integer.parseInt(likes);
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getDislikes() {
        return dislikes;
    }

    public int getParsedDislikes() {
        return Integer.parseInt(dislikes);
    }

    public void setDislikes(String dislikes) {
        this.dislikes = dislikes;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    @Nullable
    public IConstants.OPINION getOpinion() {
        if (opinion != null) {
            switch (opinion) {
                case "0":
                    return IConstants.OPINION.DISLIKED;
                case "1":
                    return IConstants.OPINION.LIKED;
                default:
                    return IConstants.OPINION.NEUTRAL;
            }
        } else {
            return IConstants.OPINION.NEUTRAL;
        }
    }

    public void setOpinion(IConstants.OPINION opinion) {
        switch (opinion) {
            case LIKED:
                this.opinion = "1";
                break;
            case DISLIKED:
                this.opinion = "0";
                break;
            case NEUTRAL:
                this.opinion = "null";
                break;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Likes: " + likes + "; Dislikes: " + dislikes + "; Opinion: " + opinion;
    }

    public MemEntity populateMemEntity(MemEntity memEntity) {
        if (memEntity == null) return null;
        memEntity.setLikes(likes);
        memEntity.setDislikes(dislikes);
        memEntity.setOpinion(opinion);
        memEntity.setFavorite(isFavourite);
        return memEntity;
    }
}
