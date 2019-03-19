package dustit.clientapp.mvp.ui.interfaces;

import java.util.List;

import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.NewAchievementEntity;

/**
 * Created by Никита on 03.11.2017.
 */

public interface ICategoriesFragmentView extends IFragmentView {
    void onBaseUpdated(List<MemEntity> list);

    void onPartialUpdate(List<MemEntity> list);

    void onErrorInLoading();

    void onAchievementUpdate(NewAchievementEntity achievementEntity);
}

