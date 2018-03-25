package dustit.clientapp.mvp.presenters.interfaces;

public interface IMemViewPresenter {
    void loadCommentsBase(String id);
    void loadCommentsWithOffset(String id, int offset);
    void postComment(String id, String text);
    void postLike(String id);
    void deleteLike(String id);
    void postDislike(String id);
    void deleteDislike(String id);
    void addToFavourites(String id);
    void removeFromFavourites(String id);
}
