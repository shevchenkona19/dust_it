package dustit.clientapp.mvp.ui.interfaces;

import java.util.List;

import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;

/**
 * Created by shevc on 05.10.2017.
 * Let's GO!
 */

public interface IFeedFragmentView extends IFragmentView {

    void onBaseUpdated(List<MemEntity> list);

    void onPartialUpdate(List<MemEntity> list);

    void onErrorInLoading();

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

    void onErrorInRemovingFromFavorites(String s);

    void onRemovedFromFavorites(String s);

    void onMemRefreshed(RefreshedMem refreshedMem, String id);

    void onErrorInRefreshingMem();
}
