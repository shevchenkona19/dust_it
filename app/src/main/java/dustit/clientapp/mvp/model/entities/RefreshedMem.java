package dustit.clientapp.mvp.model.entities;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import dustit.clientapp.utils.IConstants;

public class RefreshedMem {
    @SerializedName("likes")
    @Expose
    private int likes;

    @SerializedName("dislikes")
    @Expose
    private int dislikes;

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

    private int id;

    public RefreshedMem(int likes, int dislikes, String opinion, boolean achievementUpdate, NewAchievementEntity achievementEntity, int id, boolean isFavourite) {
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

    public int getLikes() {
        return likes;
    }


    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

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
