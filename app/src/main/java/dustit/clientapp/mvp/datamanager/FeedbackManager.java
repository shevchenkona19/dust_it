package dustit.clientapp.mvp.datamanager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.model.apis.ServerAPI;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.NewAchievementEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;
import dustit.clientapp.mvp.model.entities.UploadEntity;
import dustit.clientapp.mvp.model.repositories.ServerRepository;
import dustit.clientapp.mvp.model.repositories.SharedPreferencesRepository;
import dustit.clientapp.mvp.ui.interfaces.IActivityView;
import dustit.clientapp.mvp.ui.interfaces.IView;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.containers.Container;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

public class FeedbackManager extends BaseFeedbackManager<IView> {

    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private final List<IFeedbackInteraction> interactionList = new ArrayList<>();
    private IAchievementListener achievementListener;

    public void subscribe(IFeedbackInteraction feedbackInteraction) {
        interactionList.add(feedbackInteraction);
    }

    public void unsubscribe(IFeedbackInteraction feedbackInteraction) {
        interactionList.remove(feedbackInteraction);
    }

    @Inject
    ServerRepository serverRepository;
    @Inject
    SharedPreferencesRepository sharedPreferencesRepository;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    public FeedbackManager() {
        App.get().getAppComponent().inject(this);
    }

    public interface IFeedbackInteraction {

        void changedFeedback(RefreshedMem refreshedMem);

        void onError(RestoreMemEntity restoreMemEntity);
    }

    public void postLike(final MemEntity memEntity) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        subscriptions.add(serverRepository.postLike(getToken(), memEntity.getId())
                .subscribe(createConsumer(memEntity))
        );
    }

    public void postDislike(final MemEntity memEntity) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        subscriptions.add(serverRepository.postDislike(getToken(), memEntity.getId())
                .subscribe(createConsumer(memEntity))
        );
    }

    public void deleteLike(MemEntity memEntity) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        subscriptions.add(serverRepository.deleteLike(getToken(), memEntity.getId())
                .subscribe(createConsumer(memEntity))
        );
    }

    public void deleteDislike(MemEntity memEntity) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        subscriptions.add(serverRepository.deleteDislike(getToken(), memEntity.getId())
                .subscribe(createConsumer(memEntity))
        );
    }

    public void postLike(final UploadEntity memEntity) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        subscriptions.add(serverRepository.postLike(getToken(), memEntity.getImageId())
                .subscribe(createConsumer(memEntity))
        );
    }

    public void postDislike(final UploadEntity memEntity) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        subscriptions.add(serverRepository.postDislike(getToken(), memEntity.getImageId())
                .subscribe(createConsumer(memEntity))
        );
    }

    public void deleteLike(UploadEntity memEntity) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        subscriptions.add(serverRepository.deleteLike(getToken(), memEntity.getImageId())
                .subscribe(createConsumer(memEntity))
        );
    }

    public void deleteDislike(UploadEntity memEntity) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        subscriptions.add(serverRepository.deleteDislike(getToken(), memEntity.getImageId())
                .subscribe(createConsumer(memEntity))
        );
    }

    public void addToFavourite(MemEntity memEntity) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        subscriptions.add(serverRepository.addToFavorites(getToken(), memEntity.getId()).subscribe(createConsumer(memEntity)));
    }

    public void removeFromFavourites(MemEntity memEntity) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        subscriptions.add(serverRepository.removeFromFavorites(getToken(), memEntity.getId()).subscribe(createConsumer(memEntity)));
    }

    public void addToFavourite(UploadEntity memEntity) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        subscriptions.add(serverRepository.addToFavorites(getToken(), memEntity.getImageId()).subscribe(createConsumer(memEntity)));
    }

    public void removeFromFavourites(UploadEntity memEntity) {
        if (!userSettingsDataManager.isRegistered()) {
            getView().onNotRegistered();
            return;
        }
        subscriptions.add(serverRepository.removeFromFavorites(getToken(), memEntity.getImageId()).subscribe(createConsumer(memEntity)));
    }

    private Subscriber<RefreshedMem> createConsumer(final MemEntity memEntity) {
        return createConsumer(memEntity.getLikes(), memEntity.getDislikes(), memEntity.getOpinion(), memEntity.getId(), memEntity.isFavorite());
    }

    private Subscriber<RefreshedMem> createConsumer(final UploadEntity upload) {
        return createConsumer(upload.getLikes(), upload.getDislikes(), upload.getOpinion(), upload.getImageId(), upload.isFavourite());
    }

    private Subscriber<RefreshedMem> createConsumer(int likes, int dislikes, IConstants.OPINION opinion, int id, boolean isFavourite) {
        final Container<RefreshedMem> container = new Container<>();
        return new Subscriber<RefreshedMem>() {
            @Override
            public void onCompleted() {
                final RefreshedMem refreshedMem = container.get();
                refreshedMem.setId(id);
                boolean isSent = false;
                for (IFeedbackInteraction interaction :
                        interactionList) {
                    interaction.changedFeedback(refreshedMem);
                    if (!isSent) {
                        if (refreshedMem.isAchievementUpdate()) {
                            isSent = true;
                            achievementListener.onAchievementUpdate(refreshedMem.getAchievementEntity());
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                final RestoreMemEntity restoreMemEntity = new RestoreMemEntity(likes, dislikes, opinion, id, isFavourite);
                for (IFeedbackInteraction interaction :
                        interactionList) {
                    interaction.onError(restoreMemEntity);
                }
            }

            @Override
            public void onNext(RefreshedMem responseEntity) {
                container.put(responseEntity);
            }
        };
    }

    private String getToken() {
        return sharedPreferencesRepository.getSavedToken();
    }

    public void destroy() {
        unbind();
        subscriptions.unsubscribe();
        subscriptions.clear();
    }

    public interface IAchievementListener {
        void onAchievementUpdate(NewAchievementEntity achievementEntity);
    }

    public void bindForAchievements(IAchievementListener listener) {
        achievementListener = listener;
    }
}
