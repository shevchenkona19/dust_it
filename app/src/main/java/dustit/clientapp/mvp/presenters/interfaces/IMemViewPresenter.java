package dustit.clientapp.mvp.presenters.interfaces;

public interface IMemViewPresenter {
    void loadCommentsBase(String id);
    void loadCommentsWithOffset(String id, int offset);
    void postComment(String id, String text);
    void addToFavourites(String id);
    void removeFromFavourites(String id);
    void isFavourite(String id);
    void loadAnswersForComment(String commentId);

    void postAnswer(String id, String answerId, String text, String commentId);
}
