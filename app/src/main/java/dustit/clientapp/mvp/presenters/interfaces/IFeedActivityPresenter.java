package dustit.clientapp.mvp.presenters.interfaces;

import org.jetbrains.annotations.Nullable;

/**
 * Created by shevc on 07.10.2017.
 * Let's GO!
 */

public interface IFeedActivityPresenter {
    void getMyUsername();
    void getCategories();
    void setFeedVisited();
    boolean isFeedFirstTime();

    void loadMemForComments(@Nullable int memId, @Nullable int parentComment, @Nullable int newComment);
}
