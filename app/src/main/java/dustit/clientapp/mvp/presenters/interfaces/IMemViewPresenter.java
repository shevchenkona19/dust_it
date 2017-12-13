package dustit.clientapp.mvp.presenters.interfaces;

/**
 * Created by Никита on 11.11.2017.
 */

public interface IMemViewPresenter {
    void loadCommentsBase(String id);
    void loadCommentsWithOffset(String id, int offset);
    void postComment(String id, String text);
    void postLike(String id);
    void deleteLike(String id);
    void postDislike(String id);
    void deleteDislike(String id);
}
