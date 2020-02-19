package dustit.clientapp.mvp.presenters.interfaces;

public interface IAnswersActivityPresenter {
    void loadBase(int limit);

    void loadBase();

    void loadPartial(int limit);

    void postRespond(int userId, String text, int imageId);

    void loadCommentsToId(int newCommentId, int baseCommentId, int imageId);

}
