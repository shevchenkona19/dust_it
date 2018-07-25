package dustit.clientapp.mvp.datamanager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.model.apis.ServerAPI;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;
import dustit.clientapp.mvp.model.repositories.ServerRepository;
import dustit.clientapp.mvp.model.repositories.SharedPreferencesRepository;
import dustit.clientapp.mvp.ui.interfaces.IActivityView;
import dustit.clientapp.mvp.ui.interfaces.IView;
import dustit.clientapp.utils.containers.Container;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

public class FeedbackManager extends BaseFeedbackManager<IView> {

    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private final List<IFeedbackInteraction> interactionList = new ArrayList<>();

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

    private Subscriber<RefreshedMem> createConsumer(final MemEntity memEntity) {
        final Container<RefreshedMem> container = new Container<>();
        return new Subscriber<RefreshedMem>() {
            @Override
            public void onCompleted() {
                final RefreshedMem refreshedMem = container.get();
                refreshedMem.setId(memEntity.getId());
                for (IFeedbackInteraction interaction :
                        interactionList) {
                    interaction.changedFeedback(refreshedMem);
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                final RestoreMemEntity restoreMemEntity = new RestoreMemEntity(memEntity);
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
}
