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

    void onNoCategories();
}
