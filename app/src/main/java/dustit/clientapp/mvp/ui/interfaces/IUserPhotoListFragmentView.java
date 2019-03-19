package dustit.clientapp.mvp.ui.interfaces;

import java.util.List;

import dustit.clientapp.mvp.model.entities.NewAchievementEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;
import dustit.clientapp.mvp.model.entities.UploadEntity;

public interface IUserPhotoListFragmentView extends IFragmentView {
    void onUploadsBaseArrived(List<UploadEntity> list);

    void onUploadsPartialArrived(List<UploadEntity> list);

    void onUploadsFailed();

    void startLoading();

    void onChangedMemFeedback(RefreshedMem refreshedMem);

    void onErrorInFeedback(RestoreMemEntity restoreMemEntity);

    void onAchievementUpdate(NewAchievementEntity achievementEntity);

}
