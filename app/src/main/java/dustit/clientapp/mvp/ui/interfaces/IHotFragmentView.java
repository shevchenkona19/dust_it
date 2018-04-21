package dustit.clientapp.mvp.ui.interfaces;

import java.util.List;

import dustit.clientapp.mvp.model.entities.MemEntity;

/**
 * Created by shevc on 23.10.2017.
 * Let's GO!
 */

public interface IHotFragmentView extends IFragmentView {
    void onBaseUpdated(List<MemEntity> list);

    void onPartialUpdate(List<MemEntity> list);

    void onErrorInLoading();

    void onAddedToFavorites(String id);

    void onErrorInAddingToFavorites(String id);

    void onRemovedFromFavorites(String id);

    void onErrorInRemovingFromFavorites(String id);
}
