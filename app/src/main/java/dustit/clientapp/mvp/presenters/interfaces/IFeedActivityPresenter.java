package dustit.clientapp.mvp.presenters.interfaces;

/**
 * Created by shevc on 07.10.2017.
 * Let's GO!
 */

public interface IFeedActivityPresenter {
    void getMyUsername();
    void getCategories();
    void setFeedVisited();
    boolean isFeedFirstTime();
}
