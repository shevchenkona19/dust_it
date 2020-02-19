package dustit.clientapp.mvp.model.entities;

import org.jetbrains.annotations.Nullable;

import dustit.clientapp.utils.IConstants;

public class RestoreMemEntity {
    private int likes;
    private int dislikes;
    private String opinion;
    private int id;
    private boolean isFavourite;

    public RestoreMemEntity(MemEntity memEntity) {
        likes = memEntity.getLikes();
        dislikes = memEntity.getDislikes();
        setOpinion(memEntity.getOpinion());
        id = memEntity.getId();
        isFavourite = memEntity.isFavorite();
    }

    public RestoreMemEntity(int likes, int dislikes, String opinion, int id, boolean isFavourite) {
        this.likes = likes;
        this.dislikes = dislikes;
        setOpinion(opinion);
        this.id = id;
        this.isFavourite = isFavourite;
    }

    public RestoreMemEntity(int likes, int dislikes, IConstants.OPINION opinion, int id, boolean isFavourite) {
        this.likes = likes;
        this.dislikes = dislikes;
        setOpinion(opinion);
        this.id = id;
        this.isFavourite = isFavourite;
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

    public void setOpinion(String opinion) {
        switch (opinion) {
            case "1":
                this.opinion = "1";
            case "0":
                this.opinion = "0";
            default:
                this.opinion = "null";
        }
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
