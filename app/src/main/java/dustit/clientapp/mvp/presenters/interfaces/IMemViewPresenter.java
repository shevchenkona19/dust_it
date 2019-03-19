package dustit.clientapp.mvp.presenters.interfaces;

import dustit.clientapp.mvp.model.entities.MemEntity;

public interface IMemViewPresenter {
    void loadCommentsBase(String id);

    void loadCommentsWithOffset(String id, int offset);

    void postComment(String id, String text);

    void addToFavourites(MemEntity memEntity);

    void removeFromFavourites(MemEntity memEntity);

    void postLike(MemEntity memEntity);

    void postDislike(MemEntity memEntity);

    void deleteLike(MemEntity memEntity);

    void deleteDislike(MemEntity memEntity);

    void isFavourite(String id);

    void getCommentsToCommentId(String memId, String toCommentId);

    void updateFcmId();

    void postAnswer(String id, String answerId, String text, String commentId);

    void downloadImage(String imageId);
}
