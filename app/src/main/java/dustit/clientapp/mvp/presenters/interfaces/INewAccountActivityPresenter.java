package dustit.clientapp.mvp.presenters.interfaces;

public interface INewAccountActivityPresenter {
    void uploadImage(String path);
    void getUsername(String id);

    void getAchievements(String userId);

}
