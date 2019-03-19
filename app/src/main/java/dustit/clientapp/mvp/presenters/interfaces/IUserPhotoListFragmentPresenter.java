package dustit.clientapp.mvp.presenters.interfaces;

import dustit.clientapp.mvp.model.entities.UploadEntity;
import dustit.clientapp.mvp.presenters.interfaces.base.IFeedbackBasePresenter;

public interface IUserPhotoListFragmentPresenter extends IFeedbackBasePresenter {
    void setUserId(int userId);
    void addToFavourites(UploadEntity uploadEntity);
    void removeFromFavourites(UploadEntity uploadEntity);
    void loadBaseUploads();
    void loadMore(int offset);

    boolean isRegistered();

    void postLike(UploadEntity upload);

    void deleteLike(UploadEntity upload);

    void postDislike(UploadEntity upload);

    void deleteDislike(UploadEntity upload);

}
