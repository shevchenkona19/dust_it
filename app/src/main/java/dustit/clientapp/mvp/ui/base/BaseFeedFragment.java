package dustit.clientapp.mvp.ui.base;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.datamanager.FeedbackManager;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;
import dustit.clientapp.mvp.ui.adapters.FeedRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.dialog.ReportMemeDialog;
import dustit.clientapp.mvp.ui.interfaces.IBaseFeedFragment;
import dustit.clientapp.mvp.ui.interfaces.IFragmentView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.ImageShareUtils;
import dustit.clientapp.utils.L;
import rx.exceptions.OnErrorNotImplementedException;

import static dustit.clientapp.utils.IConstants.NUMBER_OF_ADS;

/**
 * Created by User on 06.03.2018.
 */

public abstract class BaseFeedFragment extends Fragment implements FeedbackManager.IFeedbackInteraction, IBaseFeedFragment, FeedRecyclerViewAdapter.IFeedInteractionListener {

    private RecyclerView.RecycledViewPool feedPool;
    public FeedRecyclerViewAdapter adapter;
    @Inject
    FeedbackManager feedbackManager;
    private AdLoader adLoader;
    private IBaseFragmentInteraction fragmentInteraction;

    public RecyclerView.RecycledViewPool getFeedPool() {
        return feedPool;
    }

    public void setFeedPool(RecyclerView.RecycledViewPool feedPool) {
        this.feedPool = feedPool;
    }

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

    public boolean isUserRegistered() {
        return fragmentInteraction.isRegistered();
    }

    public void gotoFragment(byte id) {
        fragmentInteraction.gotoFragment(id);
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
        if (getContext() == null) return;
        new ReportMemeDialog(getContext(), mem);
    }

    @Override
    public void shareMem(MemEntity mem) {
        if (getContext() != null) {
            ImageShareUtils.shareImage(IConstants.BASE_URL + "/feed/imgs?id=" + mem.getId(), getContext());
        }
    }

    @Override
    public void getMoreAds() {
        List<UnifiedNativeAd> preloadAds = new ArrayList<>();
        AdLoader.Builder builder = new AdLoader.Builder(getContext(), getString(R.string.admob_nativead_id));
        adLoader = builder.forUnifiedNativeAd(
                unifiedNativeAd -> {
                    // A native ad loaded successfully, check if the ad loader has finished loading
                    // and if so, insert the ads into the list.
                    preloadAds.add(unifiedNativeAd);
                    if (!adLoader.isLoading()) {
                        adapter.addPreloadAds(preloadAds);
                    }
                }).withAdListener( new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        L.print("The previous native ad failed to load. Attempting to"
                                + " load another.: " + errorCode);
                        if (!adLoader.isLoading()) {
                            adapter.addPreloadAds(preloadAds);
                        }
                    }
                }).build();

        // Load the Native Express ad.
        adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
    }

    public interface IBaseFragmentInteraction {
        void launchMemView(View holder, MemEntity memEntity, boolean startComments);

        boolean isRegistered();

        void gotoFragment(byte id);

        void onError(String error);

    }
}
