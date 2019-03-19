package dustit.clientapp.mvp.ui.base;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.FeedbackManager;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;
import dustit.clientapp.mvp.ui.adapters.FeedRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.interfaces.IBaseFeedFragment;
import dustit.clientapp.mvp.ui.interfaces.IFragmentView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.ImageShareUtils;
import rx.exceptions.OnErrorNotImplementedException;

/**
 * Created by User on 06.03.2018.
 */

public abstract class BaseFeedFragment extends Fragment implements FeedbackManager.IFeedbackInteraction, IBaseFeedFragment, FeedRecyclerViewAdapter.IFeedInteractionListener {

    @Inject
    FeedbackManager feedbackManager;

    public RecyclerView.RecycledViewPool feedPool;

    public FeedRecyclerViewAdapter adapter;

    public interface IBaseFragmentInteraction {
        void notifyOnScrollChanged(int distance);

        void launchMemView(View holder, MemEntity memEntity, boolean startComments);

        void notifyFeedScrollIdle(boolean b);

        boolean isRegistered();

        void notifyFeedOnTop();

        void gotoFragment(byte id);

        void onError(String error);

    }

    public void setFeedPool(RecyclerView.RecycledViewPool feedPool) {
        this.feedPool = feedPool;
    }

    public RecyclerView.RecycledViewPool getFeedPool() {
        return feedPool;
    }

    private IBaseFragmentInteraction fragmentInteraction;

    public void bindFeedback(IFragmentView view) {
        feedbackManager.bind(view);
    }

    public void bindWithBase(Context context) {
        if (context instanceof IBaseFragmentInteraction) {
            fragmentInteraction = (IBaseFragmentInteraction) context;
        } else {
            throw new OnErrorNotImplementedException(new Throwable("Must implement FragmentInteraction"));
        }
        App.get().getAppComponent().inject(this);
    }

    public void subscribeToFeedbackChanges() {
        feedbackManager.subscribe(this);
    }

    public void unsubscribeFromFeedbackChanges() {
        feedbackManager.unsubscribe(this);
    }

    public void launchMemView(View view, MemEntity memEntity, boolean startComments) {
        fragmentInteraction.launchMemView(view, memEntity, startComments);
    }

    public void notifyFeedScrollChanged(int scrollY) {
        fragmentInteraction.notifyOnScrollChanged(scrollY);
    }

    public boolean isUserRegistered() {
        return fragmentInteraction.isRegistered();
    }

    public void gotoFragment(byte id) {
        fragmentInteraction.gotoFragment(id);
    }


    public void notifyFeedScrollIdle(boolean b) {
        fragmentInteraction.notifyFeedScrollIdle(b);
    }

    public void notifyFeedOnTop() {
        fragmentInteraction.notifyFeedOnTop();
    }

    @Override
    public void onError(RestoreMemEntity restoreMemEntity) {
        adapter.restoreMem(restoreMemEntity);
    }

    @Override
    public void changedFeedback(RefreshedMem refreshedMem) {
        adapter.refreshMem(refreshedMem);
    }

    @Override
    public void onMemSelected(@NotNull View animStart, @NotNull MemEntity mem) {
        launchMemView(animStart, mem, false);
    }

    @Override
    public void onCommentsSelected(View animStart, MemEntity mem) {
        launchMemView(animStart, mem, true);
    }

    @Override
    public void postLike(@NotNull MemEntity mem) {
        feedbackManager.postLike(mem);
    }

    @Override
    public void postDislike(@NotNull MemEntity mem) {
        feedbackManager.postDislike(mem);
    }

    @Override
    public void deleteLike(@NotNull MemEntity mem) {
        feedbackManager.deleteLike(mem);
    }

    @Override
    public void deleteDislike(@NotNull MemEntity mem) {
        feedbackManager.deleteDislike(mem);
    }

    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(getContext());
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
    public void reportMeme(MemEntity mem) {

    }

    @Override
    public void shareMem(MemEntity mem) {
        if (getContext() != null) {
            ImageShareUtils.shareImage(IConstants.BASE_URL + "/feed/imgs?id=" + mem.getId(), getContext());
        }
    }
}
