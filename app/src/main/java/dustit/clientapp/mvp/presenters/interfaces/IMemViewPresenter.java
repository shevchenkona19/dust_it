package dustit.clientapp.mvp.presenters.interfaces;

import dustit.clientapp.mvp.model.entities.MemEntity;

public interface IMemViewPresenter {
    void loadCommentsBase(int id);

    void loadCommentsWithOffset(int id, int offset);

    void postComment(int id, String text);

    void addToFavourites(MemEntity memEntity);

    void removeFromFavourites(MemEntity memEntity);

    void postLike(MemEntity memEntity);

    void postDislike(MemEntity memEntity);

    void deleteLike(MemEntity memEntity);

    void deleteDislike(MemEntity memEntity);

    void isFavourite(int id);

    void getCommentsToCommentId(int memId, int toCommentId);

    void updateFcmId();

    void postAnswer(int id, int answerId, String text, int commentId);

    void downloadImage(int imageId);
}
