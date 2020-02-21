package dustit.clientapp.mvp.presenters.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.FeedbackManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.CommentEntity;
import dustit.clientapp.mvp.model.entities.IsFavourite;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.PostCommentEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IMemViewPresenter;
import dustit.clientapp.mvp.presenters.interfaces.base.IFeedbackBasePresenter;
import dustit.clientapp.mvp.ui.interfaces.IMemViewView;
import dustit.clientapp.mvp.ui.interfaces.IView;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.L;
import dustit.clientapp.utils.containers.Container;
import rx.Subscriber;

/**
 * Created by Никита on 11.11.2017.
 */

public class MemViewPresenter extends BasePresenter<IMemViewView> implements IFeedbackBasePresenter, FeedbackManager.IFeedbackInteraction, IMemViewPresenter {
    @Inject
    DataManager dataManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;
    @Inject
    FeedbackManager feedbackManager;

    public MemViewPresenter() {
        App.get().getAppComponent().inject(this);
        feedbackManager.subscribe(this);
    }

    @Override
    public void loadCommentsBase(int id) {
        getView().onStartLoading();
        final List<CommentEntity> list = new ArrayList<>();
        addSubscription(dataManager.getComments(id, 6, 0)
                .subscribe(new Subscriber<CommentEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onBaseUpdated(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print(e.getMessage());
                        if (getView() != null) {
                            getView().onErrorInLoading();
                        }
                    }

                    @Override
                    public void onNext(CommentEntity commentEntity) {
                        list.add(commentEntity);
                    }
                }));
    }

    @Override
    public void loadCommentsWithOffset(int id, int offset) {
        getView().onStartLoading();
        final List<CommentEntity> list = new ArrayList<>();
        addSubscription(dataManager.getComments(id, 5, offset)
                .subscribe(new Subscriber<CommentEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onPartialUpdate(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print(e.getMessage());
                        getView().onErrorInLoading();
                    }

                    @Override
                    public void onNext(CommentEntity commentEntity) {
                        list.add(commentEntity);
                    }
                }));
    }

    @Override
    public void postComment(int id, String text) {
        if (!isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        AtomicReference<ResponseEntity> atomicReference = new AtomicReference<>();
        PostCommentEntity commentEntity = new PostCommentEntity(text);
        addSubscription(dataManager.postComment(id, commentEntity)
                .subscribe(new Subscriber<ResponseEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onCommentSentSuccessfully();
                        ResponseEntity response = atomicReference.get();
                        if (response != null) {
                            if (response.isAchievementUpdate()) {
                                getView().onAchievementUpdate(response.getAchievementEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print(e.getMessage());
                        getView().onCommentSendFail();
                    }

                    @Override
                    public void onNext(ResponseEntity responseEntity) {
                        if (responseEntity.getResponse() != 200) {
                            getView().onCommentSendFail();
                        } else {
                            atomicReference.set(responseEntity);
                        }
                    }
                }));
    }

    @Override
    public void addToFavourites(MemEntity memEntity) {
        feedbackManager.addToFavourite(memEntity);
    }

    @Override
    public void removeFromFavourites(MemEntity memEntity) {
        feedbackManager.removeFromFavourites(memEntity);
    }

    @Override
    public void postLike(MemEntity memEntity) {
        feedbackManager.postLike(memEntity);
    }

    @Override
    public void postDislike(MemEntity memEntity) {
        feedbackManager.postDislike(memEntity);
    }

    @Override
    public void deleteLike(MemEntity memEntity) {
        feedbackManager.deleteLike(memEntity);
    }

    @Override
    public void deleteDislike(MemEntity memEntity) {
        feedbackManager.deleteDislike(memEntity);
    }

    @Override
    public void isFavourite(int id) {
        final Container<IsFavourite> favouriteContainer = new Container<>();
        addSubscription(dataManager.isFavourite(id).subscribe(new Subscriber<IsFavourite>() {
            @Override
            public void onCompleted() {
                getView().onIsFavourite(favouriteContainer.get().isFavourite());
            }

            @Override
            public void onError(Throwable e) {
                L.print("Error in isFavourite: " + e.getMessage());
                getView().onError();
            }

            @Override
            public void onNext(IsFavourite isFavourite) {
                favouriteContainer.put(isFavourite);
            }
        }));
    }

    @Override
    public void getCommentsToCommentId(int memId, int toCommentId) {
        getView().onStartLoading();
        List<CommentEntity> list = new ArrayList<>();
        addSubscription(dataManager.getCommentsToCommentId(memId, toCommentId).subscribe(new Subscriber<CommentEntity>() {
            @Override
            public void onCompleted() {
                getView().onCommentsToCommentIdLoaded(list);
            }

            @Override
            public void onError(Throwable e) {
                getView().onErrorInLoading();
            }

            @Override
            public void onNext(CommentEntity commentEntity) {
                list.add(commentEntity);
            }
        }));
    }

    @Override
    public void postAnswer(int id, int answerId, String text, int commentId) {
        if (!isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        AtomicReference<ResponseEntity> atomicReference = new AtomicReference<>();
        PostCommentEntity commentEntity = new PostCommentEntity(text);
        addSubscription(dataManager.postAnswerForComment(id, commentId, answerId, commentEntity)
                .subscribe(new Subscriber<ResponseEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onAnswerSentSuccessfully();
                        ResponseEntity response = atomicReference.get();
                        if (response != null) {
                            if (response.isAchievementUpdate()) {
                                getView().onAchievementUpdate(response.getAchievementEntity());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print(e.getMessage());
                        getView().onCommentSendFail();
                    }

                    @Override
                    public void onNext(ResponseEntity responseEntity) {
                        if (responseEntity.getResponse() != 200) {
                            getView().onCommentSendFail();
                        } else {
                            atomicReference.set(responseEntity);
                        }
                    }
                }));
    }

    @Override
    public void downloadImage(int imageId) {
        boolean permCheck = getView().checkPermission();
        if (permCheck) {
            Picasso.get()
                    .load(Uri.parse(IConstants.BASE_URL + "/feed/imgs?id=" + imageId))
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            String res = saveImage(bitmap);
                            if (res.equals("")) {
                                getView().onDownloadFailed();
                            } else {
                                getView().onDownloaded(res);

                            }
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            getView().onDownloadFailed();
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
        } else {
            getView().getPermissions();
        }
    }

    private String saveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/memes");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 1000000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return file.getAbsolutePath();
    }


    public boolean isRegistered() {
        return userSettingsDataManager.isRegistered();
    }

    public void updateFcmId() {
        String fcmId = userSettingsDataManager.getFcm();
        final boolean[] isError = {false};
        addSubscription(dataManager.setFcmId(fcmId).subscribe(new Subscriber<ResponseEntity>() {
            @Override
            public void onCompleted() {
                if (!isError[0]) {
                    userSettingsDataManager.onFcmUpdated();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseEntity responseEntity) {
                if (responseEntity.getResponse() != 200) {
                    isError[0] = true;
                }
            }
        }));
    }

    @Override
    public void changedFeedback(RefreshedMem refreshedMem) {
        getView().changedFeedback(refreshedMem);
    }

    @Override
    public void onError(RestoreMemEntity restoreMemEntity) {
        getView().onError(restoreMemEntity);
    }

    @Override
    public void unbind() {
        feedbackManager.unsubscribe(this);
        feedbackManager.unbind();
        super.unbind();
    }

    @Override
    public void bindToView(IView iView) {
        feedbackManager.bind(iView);
    }
}
