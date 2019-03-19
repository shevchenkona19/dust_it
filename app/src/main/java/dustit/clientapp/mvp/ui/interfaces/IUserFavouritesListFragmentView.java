package dustit.clientapp.mvp.ui.interfaces;

import java.util.List;

import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.NewAchievementEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;

public interface IUserFavouritesListFragmentView extends IFragmentView {
    void onFavouritesLoaded(List<MemEntity> memEntities);
    void onFavouritesFailed();

    void onChangedFeedback(RefreshedMem refreshedMem);

    void restoreMem(RestoreMemEntity restoreMemEntity);

    void onAchievementUpdate(NewAchievementEntity achievementEntity);

}
