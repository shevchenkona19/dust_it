package dustit.clientapp.mvp.ui.interfaces;

import java.util.List;

import dustit.clientapp.mvp.model.entities.MemEntity;

/**
 * Created by shevc on 05.10.2017.
 * Let's GO!
 */

public interface IFeedFragmentView extends IFragmentView {

    void onBaseUpdated(List<MemEntity> list);

    void onPartialUpdate(List<MemEntity> list);

    void onErrorInLoading();

    void onStartLoading();

    void onLikePostError(String id);

    void onLikeDeletingError(String id);

    void onDislikePostError(String id);

    void onDislikeDeletingError(String id);

    void onLikePostedSuccessfully(String id);

    void onLikeDeletedSuccessfully(String id);

    void onDislikePostedSuccessfully(String id);

    void onDislikeDeletedSuccessfully(String id);

    void onAddedToFavorites(String id);

    void onErrorInAddingToFavorites(String id);
}
