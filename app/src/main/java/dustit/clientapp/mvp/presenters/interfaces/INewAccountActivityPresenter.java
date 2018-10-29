package dustit.clientapp.mvp.presenters.interfaces;

public interface INewAccountActivityPresenter {
    void uploadImage(String path);
    void getUsername(String id);
    void loadFavorites(String id);
    void removeFromFavorites(String id);

    void getAchievements(String userId);

}
