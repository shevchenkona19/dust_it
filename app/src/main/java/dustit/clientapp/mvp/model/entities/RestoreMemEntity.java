package dustit.clientapp.mvp.model.entities;

import org.jetbrains.annotations.Nullable;

import dustit.clientapp.utils.IConstants;

public class RestoreMemEntity {
    private String likes;
    private String dislikes;
    private String opinion;
    private String id;

    public RestoreMemEntity(MemEntity memEntity) {
        likes = memEntity.getLikes();
        dislikes = memEntity.getDislikes();
        setOpinion(memEntity.getOpinion());
        id = memEntity.getId();
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
}
