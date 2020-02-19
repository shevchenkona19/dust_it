package dustit.clientapp.mvp.ui.interfaces;

import java.util.List;

import dustit.clientapp.mvp.model.entities.UserEntity;

public interface ISearchActivityView extends IActivityView {
    void onSearchResultsArrived(List<UserEntity> list);
    void onStartLoading();

    void onError();

}
