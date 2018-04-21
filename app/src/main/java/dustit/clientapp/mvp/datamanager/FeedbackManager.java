package dustit.clientapp.mvp.datamanager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.model.apis.ServerAPI;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;
import dustit.clientapp.mvp.model.repositories.SharedPreferencesRepository;
import dustit.clientapp.utils.containers.Container;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

public class FeedbackManager {

    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private final List<IFeedbackInteraction> interactionList = new ArrayList<>();

    public void subscribe(IFeedbackInteraction feedbackInteraction) {
        interactionList.add(feedbackInteraction);
    }

    public void unsubscribe(IFeedbackInteraction feedbackInteraction) {
        interactionList.remove(feedbackInteraction);
    }

    @Inject
    public ServerAPI serverAPI;
    @Inject
    public SharedPreferencesRepository sharedPreferencesRepository;

    public FeedbackManager() {
        App.get().getAppComponent().inject(this);
    }

    public interface IFeedbackInteraction {
        void changedFeedback(RefreshedMem refreshedMem);

        void onError(RestoreMemEntity restoreMemEntity);
    }

    public void postLike(final MemEntity memEntity) {
        subscriptions.add(serverAPI.postLike(getToken(), memEntity.getId())
                .subscribe(createConsumer(memEntity))
        );
    }

    public void postDislike(final MemEntity memEntity) {
        subscriptions.add(serverAPI.postDislike(getToken(), memEntity.getId())
                .subscribe(createConsumer(memEntity))
        );
    }

    public void deleteLike(MemEntity memEntity) {
        subscriptions.add(serverAPI.deleteLike(getToken(), memEntity.getId())
                .subscribe(createConsumer(memEntity))
        );
    }

    public void deleteDislike(MemEntity memEntity) {
        subscriptions.add(serverAPI.deleteDislike(getToken(), memEntity.getId())
                .subscribe(createConsumer(memEntity))
        );
    }

    private Subscriber<RefreshedMem> createConsumer(final MemEntity memEntity) {
        final Container<RefreshedMem> container = new Container<>();
        return new Subscriber<RefreshedMem>() {
            @Override
            public void onCompleted() {
                for (IFeedbackInteraction interaction :
                        interactionList) {
                    interaction.changedFeedback(container.get().setId(memEntity.getId()));
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
        subscriptions.unsubscribe();
        subscriptions.clear();
    }
}
