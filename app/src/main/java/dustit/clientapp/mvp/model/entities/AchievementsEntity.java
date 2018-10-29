package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AchievementsEntity {
    @SerializedName("likes")
    @Expose
    private Achievement likes;

    @SerializedName("dislikes")
    @Expose
    private Achievement dislikes;

    @SerializedName("comments")
    @Expose
    private Achievement comments;

    @SerializedName("favourites")
    @Expose
    private Achievement favourites;

    @SerializedName("views")
    @Expose
    private Achievement views;

    @SerializedName("firstHundred")
    @Expose
    private boolean firstHundred;

    @SerializedName("firstThousand")
    @Expose
    private boolean firstThousand;

    public AchievementsEntity(Achievement likes, Achievement dislikes, Achievement comments, Achievement favourites, Achievement views, boolean firstHundred, boolean firstThousand) {
        this.likes = likes;
        this.dislikes = dislikes;
        this.comments = comments;
        this.favourites = favourites;
        this.views = views;
        this.firstHundred = firstHundred;
        this.firstThousand = firstThousand;
    }

    public Achievement getLikes() {
        return likes;
    }

    public void setLikes(Achievement likes) {
        this.likes = likes;
    }

    public Achievement getDislikes() {
        return dislikes;
    }

    public void setDislikes(Achievement dislikes) {
        this.dislikes = dislikes;
    }

    public Achievement getComments() {
        return comments;
    }

    public void setComments(Achievement comments) {
        this.comments = comments;
    }

    public Achievement getFavourites() {
        return favourites;
    }

    public void setFavourites(Achievement favourites) {
        this.favourites = favourites;
    }

    public Achievement getViews() {
        return views;
    }

    public void setViews(Achievement views) {
        this.views = views;
    }

    public boolean isFirstHundred() {
        return firstHundred;
    }

    public void setFirstHundred(boolean firstHundred) {
        this.firstHundred = firstHundred;
    }

    public boolean isFirstThousand() {
        return firstThousand;
    }

    public void setFirstThousand(boolean firstThousand) {
        this.firstThousand = firstThousand;
    }
}
