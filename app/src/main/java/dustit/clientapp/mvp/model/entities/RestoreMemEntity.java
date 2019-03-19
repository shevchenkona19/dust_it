package dustit.clientapp.mvp.model.entities;

import org.jetbrains.annotations.Nullable;

import dustit.clientapp.utils.IConstants;

public class RestoreMemEntity {
    private String likes;
    private String dislikes;
    private String opinion;
    private String id;
    private boolean isFavourite;

    public RestoreMemEntity(MemEntity memEntity) {
        likes = memEntity.getLikes();
        dislikes = memEntity.getDislikes();
        setOpinion(memEntity.getOpinion());
        id = memEntity.getId();
        isFavourite = memEntity.isFavorite();
    }

    public RestoreMemEntity(String likes, String dislikes, String opinion, String id, boolean isFavourite) {
        this.likes = likes;
        this.dislikes = dislikes;
        setOpinion(opinion);
        this.id = id;
        this.isFavourite = isFavourite;
    }

    public RestoreMemEntity(String likes, String dislikes, IConstants.OPINION opinion, String id, boolean isFavourite) {
        this.likes = likes;
        this.dislikes = dislikes;
        setOpinion(opinion);
        this.id = id;
        this.isFavourite = isFavourite;
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

    public int getParsedDislikes() {
        return Integer.parseInt(dislikes);
    }

    public int getParsedLikes() {
        return Integer.parseInt(likes);
    }

    public void setDislikes(String dislikes) {
        this.dislikes = dislikes;
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
