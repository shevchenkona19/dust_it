package dustit.clientapp.mvp.ui.interfaces;

import java.util.List;

import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.NewAchievementEntity;

/**
 * Created by shevc on 23.10.2017.
 * Let's GO!
 */

public interface IHotFragmentView extends IFragmentView {
    void onBaseUpdated(List<MemEntity> list);

    void onPartialUpdate(List<MemEntity> list);

    void onErrorInLoading();

    void onAchievementUpdate(NewAchievementEntity achievementEntity);
}
