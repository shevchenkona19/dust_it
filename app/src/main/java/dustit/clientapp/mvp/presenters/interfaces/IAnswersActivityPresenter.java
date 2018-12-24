package dustit.clientapp.mvp.presenters.interfaces;

public interface IAnswersActivityPresenter {
    void loadBase(int limit);

    void loadBase();

    void loadPartial(int limit);

    void postRespond(String userId, String text, String imageId);

    void loadCommentsToId(String newCommentId, String baseCommentId, String imageId);

}
