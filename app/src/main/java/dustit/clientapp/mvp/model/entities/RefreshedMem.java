package dustit.clientapp.mvp.model.entities;

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

    private String id;

    public RefreshedMem(String likes, String dislikes, String opinion) {
        this.likes = likes;
        this.dislikes = dislikes;
        this.opinion = opinion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getDislikes() {
        return dislikes;
    }

    public void setDislikes(String dislikes) {
        this.dislikes = dislikes;
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

    @Override
    public String toString() {
        return "Likes: " + likes +"; Dislikes: " + dislikes + "; Opinion: " + opinion;
    }
}
