package dustit.clientapp.mvp.presenters.fragments;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.FeedbackManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;
import dustit.clientapp.mvp.model.entities.UploadEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IUserPhotoListFragmentPresenter;
import dustit.clientapp.mvp.ui.interfaces.IUserPhotoListFragmentView;
import dustit.clientapp.mvp.ui.interfaces.IView;
import rx.Subscriber;

public class UserPhotoListFragmentPresenter extends BasePresenter<IUserPhotoListFragmentView> implements IUserPhotoListFragmentPresenter, FeedbackManager.IFeedbackInteraction {

    @Inject
    DataManager dataManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;
    @Inject
    FeedbackManager feedbackManager;

    private int userId;

    public UserPhotoListFragmentPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void bind(IUserPhotoListFragmentView iUserPhotoListFragmentView) {
        feedbackManager.subscribe(this);
        super.bind(iUserPhotoListFragmentView);
    }

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public void addToFavourites(UploadEntity uploadEntity) {
        feedbackManager.addToFavourite(uploadEntity);
    }

    @Override
    public void removeFromFavourites(UploadEntity uploadEntity) {
        feedbackManager.removeFromFavourites(uploadEntity);
    }

    @Override
    public void loadBaseUploads() {
        getView().startLoading();
        loadUploads(6, 0, false);
    }

    @Override
    public void loadMore(int offset) {
        loadUploads(5, offset, true);
    }

    @Override
    public boolean isRegistered() {
        return userSettingsDataManager.isRegistered();
    }

    @Override
    public void postLike(UploadEntity upload) {
        feedbackManager.postLike(upload);
    }

    @Override
    public void deleteLike(UploadEntity upload) {
        feedbackManager.deleteLike(upload);
    }

    @Override
    public void postDislike(UploadEntity upload) {
        feedbackManager.postDislike(upload);
    }

    @Override
    public void deleteDislike(UploadEntity upload) {
        feedbackManager.deleteDislike(upload);
    }

    private void loadUploads(int limit, int offset, boolean isPartial) {
        List<UploadEntity> list = new ArrayList<>();
        addSubscription(dataManager.getUserUploads(userId, limit, offset).subscribe(new Subscriber<UploadEntity>() {
            @Override
            public void onCompleted() {
                if (isPartial) {
                    getView().onUploadsPartialArrived(list);
                } else {
                    getView().onUploadsBaseArrived(list);
                }
            }

            @Override
            public void onError(Throwable e) {
                getView().onUploadsFailed();
            }

            @Override
            public void onNext(UploadEntity uploadEntity) {
                list.add(uploadEntity);
            }
        }));
    }

    @Override
    public void changedFeedback(RefreshedMem refreshedMem) {
        getView().onChangedMemFeedback(refreshedMem);
    }

    @Override
    public void onError(RestoreMemEntity restoreMemEntity) {
        getView().onErrorInFeedback(restoreMemEntity);
    }

    @Override
    public void unbind() {
        feedbackManager.unsubscribe(this);
        super.unbind();
    }

    @Override
    public void bindToView(IView iView) {
        feedbackManager.bind(iView);
    }
}
