package dustit.clientapp.mvp.presenters.interfaces;

public interface INewAccountActivityPresenter {
    void uploadImage(String path);
    void getUsername(int id);

    void getAchievements(int userId);

}
