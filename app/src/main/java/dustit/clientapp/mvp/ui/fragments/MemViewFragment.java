package dustit.clientapp.mvp.ui.fragments;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.wooplr.spotlight.SpotlightView;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.customviews.WrapperLinearLayoutManager;
import dustit.clientapp.mvp.datamanager.FeedbackManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.CommentEntity;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;
import dustit.clientapp.mvp.presenters.activities.MemViewPresenter;
import dustit.clientapp.mvp.ui.adapters.CommentsRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.interfaces.IMemViewView;
import dustit.clientapp.mvp.ui.interfaces.IView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.KeyboardHandler;
import dustit.clientapp.utils.L;
import me.relex.photodraweeview.PhotoDraweeView;

public class MemViewFragment extends Fragment implements CommentsRecyclerViewAdapter.ICommentInteraction, IMemViewView, FeedbackManager.IFeedbackInteraction {
    private static final String MEM_KEY = "MEM_ENTITY";
    private static final String COMMENTS_KEY = "COMMENTS_KEY";

    private MemEntity mem;
    private Unbinder unbinder;
    private CommentsRecyclerViewAdapter commentAdapter;
    private final MemViewPresenter presenter = new MemViewPresenter();
    private boolean isExpanded = false;

    private boolean startComments = false;

    private boolean canPerformTap = true;
    private boolean navVisible = true;

    private int imageWidth = -1;
    private int imageHeight = -1;

    @Override
    public void changedFeedback(RefreshedMem refreshedMem) {
        mem.setLikes(refreshedMem.getLikes());
        mem.setDislikes(refreshedMem.getDislikes());
        mem.setOpinion(refreshedMem.getOpinion());
        refreshUi();
    }

    @Override
    public void onError(RestoreMemEntity restoreMemEntity) {
        mem.setLikes(restoreMemEntity.getLikes());
        mem.setDislikes(restoreMemEntity.getDislikes());
        mem.setOpinion(restoreMemEntity.getOpinion());
        refreshUi();
    }

    public interface IMemViewRatingInteractionListener {
        void closeMemView();
    }

    private IMemViewRatingInteractionListener interactionListener;

    @BindView(R.id.tvCommentEmptySet)
    TextView tvCommentEmpty;
    @BindView(R.id.ivMemViewLike)
    ImageView ivLike;
    @BindView(R.id.ivMemViewDisliked)
    ImageView ivDislike;
    @BindView(R.id.tvMemViewDislikeCount)
    TextView tvDislikeCount;
    @BindView(R.id.tvMemViewLikeCount)
    TextView tvLikeCount;
    @BindView(R.id.ablExpandablePanel)
    AppBarLayout tbLikePanel;
    @BindView(R.id.clMemViewUpperLayout)
    ConstraintLayout clUpperLayout;
    @BindView(R.id.tbUpperMemView)
    Toolbar tbUpperToolbar;
    @BindView(R.id.ivMemViewExpandComments)
    ImageView ivExpandComments;
    @BindView(R.id.clMemViewNotExpandedBottomLayout)
    ConstraintLayout clNotExpandedBottomLayout;
    @BindView(R.id.rvMemViewExpandedUpperLayout)
    RelativeLayout clExpandedUpperLayout;
    @BindView(R.id.ivMemViewExpandedToNotExpanded)
    ImageView ivDisexpand;
    @BindView(R.id.rvMemViewComments)
    RecyclerView rvComments;
    @BindView(R.id.cvMemViewCommentSendView)
    CardView cvCommentSendPanel;
    @BindView(R.id.etMemViewComment)
    EditText etComment;
    @BindView(R.id.ivMemViewCommentSend)
    ImageView ivSendComment;
    @BindView(R.id.srlMemViewRefreshComments)
    SwipeRefreshLayout srlCommentsRefresh;
    @BindView(R.id.pbIMemViewCommentSendingLoading)
    ProgressBar pbCommentSend;
    @BindView(R.id.abMemViewUpperBarLayout)
    AppBarLayout toolbar;
    @BindView(R.id.zdvMem)
    PhotoDraweeView pdvMem;
    @BindView(R.id.tvMemViewCommentsLabel)
    TextView tvCommentsLabel;
    @BindView(R.id.ivMemViewAddToPhotos)
    ImageView ivAddToFavourites;
    @BindView(R.id.supPanel)
    SlidingUpPanelLayout supPanel;
    @BindView(R.id.rlExpandablePanelWrapper)
    View rlExpandablePanelWrapper;
    @BindView(R.id.tvMemViewCommentsCount)
    TextView tvCommentsCount;

    @Inject
    FeedbackManager feedbackManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    private SlidingUpPanelLayout.PanelState prevPanelState = SlidingUpPanelLayout.PanelState.COLLAPSED;

    public static MemViewFragment newInstance(MemEntity mem, boolean startComments) {
        Bundle args = new Bundle();
        args.putParcelable(MEM_KEY, mem);
        args.putBoolean(COMMENTS_KEY, startComments);
        final MemViewFragment fragment = new MemViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args != null) {
            mem = args.getParcelable(MEM_KEY);
            startComments = args.getBoolean(COMMENTS_KEY);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IMemViewRatingInteractionListener) {
            interactionListener = (IMemViewRatingInteractionListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_mem_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        App.get().getAppComponent().inject(this);
        presenter.bind(this);
        feedbackManager.bind(this);
        if (userSettingsDataManager.isRegistered()) {
            presenter.isFavourite(mem.getId());
        }
        commentAdapter = new CommentsRecyclerViewAdapter(getContext(), this);
        pbCommentSend.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        initOnClicks();
        feedbackManager.subscribe(this);
        rvComments.setAdapter(commentAdapter);
        rvComments.setLayoutManager(new WrapperLinearLayoutManager(getContext()));
        srlCommentsRefresh.setOnRefreshListener(() -> {
            srlCommentsRefresh.setRefreshing(true);
            presenter.loadCommentsBase(mem.getId());
            tvCommentEmpty.setVisibility(View.INVISIBLE);
        });
        clUpperLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        final DraweeController ctrl = Fresco.newDraweeControllerBuilder().setUri(IConstants.BASE_URL + "/feed/imgs?id=" + mem.getId())
                .setTapToRetryEnabled(true)
                .setOldController(pdvMem.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        if (imageInfo == null || pdvMem == null) {
                            return;
                        }
                        if (getActivity() != null) {
                            ActivityCompat.startPostponedEnterTransition(getActivity());
                        }
                        pdvMem.update(imageInfo.getWidth(), imageInfo.getHeight());
                    }
                })
                .build();
        final GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setProgressBarImage(new ProgressBarDrawable())
                .build();
        pdvMem.setController(ctrl);
        pdvMem.setHierarchy(hierarchy);
        supPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                clExpandedUpperLayout.setAlpha(slideOffset);
                clNotExpandedBottomLayout.setAlpha(1 - slideOffset);
                if (canPerformTap) canPerformTap = false;
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                switch (newState) {
                    case DRAGGING:
                        if (srlCommentsRefresh.getVisibility() == View.INVISIBLE)
                            srlCommentsRefresh.setVisibility(View.VISIBLE);
                        if (canPerformTap)
                            canPerformTap = false;
                        break;
                    case EXPANDED:
                        prevPanelState = SlidingUpPanelLayout.PanelState.EXPANDED;
                        canPerformTap = true;
                        presenter.loadCommentsBase(mem.getId());
                        supPanel.setDragView(clExpandedUpperLayout);
                        break;
                    case COLLAPSED:
                        prevPanelState = SlidingUpPanelLayout.PanelState.COLLAPSED;
                        KeyboardHandler.hideKeyboard(getActivity());
                        canPerformTap = true;
                        srlCommentsRefresh.setVisibility(View.INVISIBLE);
                        supPanel.setDragView(ivExpandComments);
                        break;
                    case ANCHORED:
                        switch (prevPanelState) {
                            case EXPANDED:
                                supPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                                break;
                            case COLLAPSED:
                                supPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                                break;
                            case DRAGGING:
                                supPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                        }
                }
            }
        });
        refreshUi();
        initAutoHide();
        if (startComments) {
            expandComments(true);
        }
        if (presenter.isRegistered() && !startComments) {
            new SpotlightView.Builder(Objects.requireNonNull(getActivity()))
                    .introAnimationDuration(400)
                    .enableRevealAnimation(true)
                    .performClick(true)
                    .fadeinTextDuration(400)
                    .headingTvColor(Color.parseColor("#f98098"))
                    .headingTvSize(32)
                    .headingTvText(getString(R.string.add_to_favourites_title))
                    .subHeadingTvColor(Color.parseColor("#ffffff"))
                    .subHeadingTvSize(16)
                    .subHeadingTvText(getString(R.string.add_to_favourites_description))
                    .maskColor(Color.parseColor("#dc000000"))
                    .target(ivAddToFavourites)
                    .lineAnimDuration(400)
                    .lineAndArcColor(Color.parseColor("#ffb06a"))
                    .dismissOnTouch(false)
                    .dismissOnBackPress(false)
                    .enableDismissAfterShown(false)
                    .usageId(IConstants.ISpotlight.ADD_MEM_FAVS)
                    .show();
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        feedbackManager.unsubscribe(this);
        super.onDestroyView();
    }

    private void initAutoHide() {
        if (pdvMem != null) {
            pdvMem.setOnScaleChangeListener((scaleFactor, focusX, focusY) -> {
                if ((imageHeight == -1 && imageWidth == -1)) {
                    imageHeight = pdvMem.getHeight() / 2;
                    imageWidth = pdvMem.getWidth() / 2;
                }
                if (imageHeight - focusX > 50 || imageWidth - focusY > 50) {
                    if (navVisible) {
                        setNavVisible(false);
                        isExpanded = true;
                    }
                }
            });
        }
    }

    private void refreshUi() {
        if (getContext() != null) {
            if (mem.isFavorite()) {
                setImageDrawable(ivAddToFavourites, R.drawable.ic_saved);
            } else {
                setImageDrawable(ivAddToFavourites, R.drawable.ic_add_to_favourites);
            }
            tvCommentsCount.setText(String.valueOf(mem.getCommentsCount()));
            if (tvLikeCount != null) {
                tvLikeCount.setText(mem.getLikes());
                tvDislikeCount.setText(mem.getDislikes());
                final IConstants.OPINION opinion = mem.getOpinion();
                switch (opinion) {
                    case LIKED:
                        setImageDrawable(ivLike, R.drawable.ic_like_pressed);
                        setImageDrawable(ivDislike, R.drawable.ic_dislike);
                        break;
                    case DISLIKED:
                        setImageDrawable(ivLike, R.drawable.ic_like);
                        setImageDrawable(ivDislike, R.drawable.ic_dislike_pressed);
                        break;
                    case NEUTRAL:
                        setImageDrawable(ivLike, R.drawable.ic_like);
                        setImageDrawable(ivDislike, R.drawable.ic_dislike);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void setNavVisible(boolean visible) {
        if (supPanel != null) {
            navVisible = visible;
            if (visible) {
                supPanel.setEnabled(true);
                toolbar.setVisibility(View.VISIBLE);
                tbLikePanel.setVisibility(View.VISIBLE);
                rlExpandablePanelWrapper.setVisibility(View.VISIBLE);
            } else {
                supPanel.setEnabled(false);
                rlExpandablePanelWrapper.setVisibility(View.INVISIBLE);
                toolbar.setVisibility(View.INVISIBLE);
                tbLikePanel.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void initOnClicks() {
        ivExpandComments.setOnClickListener(view -> expandComments(false));
        ivDisexpand.setOnClickListener(view -> disExpandComments());
        tbUpperToolbar.setNavigationOnClickListener(view -> interactionListener.closeMemView());
        ivLike.setOnClickListener(view -> {
            if (!userSettingsDataManager.isRegistered()) {
                onNotRegistered();
                return;
            }
            if (mem.getOpinion() == IConstants.OPINION.LIKED) {
                feedbackManager.deleteLike(mem);
                mem.setOpinion(IConstants.OPINION.NEUTRAL);
                mem.setLikes(-1);
            } else if (mem.getOpinion() == IConstants.OPINION.DISLIKED) {
                feedbackManager.postLike(mem);
                mem.setLikes(1);
                mem.setDislikes(-1);
                mem.setOpinion(IConstants.OPINION.LIKED);
            } else {
                feedbackManager.postLike(mem);
                mem.setLikes(1);
                mem.setOpinion(IConstants.OPINION.LIKED);
            }
            refreshUi();
        });
        ivDislike.setOnClickListener(view -> {
            if (!userSettingsDataManager.isRegistered()) {
                onNotRegistered();
                return;
            }
            if (mem.getOpinion() == IConstants.OPINION.DISLIKED) {
                feedbackManager.deleteDislike(mem);
                mem.setDislikes(-1);
                mem.setOpinion(IConstants.OPINION.NEUTRAL);
            } else if (mem.getOpinion() == IConstants.OPINION.LIKED) {
                feedbackManager.postDislike(mem);
                mem.setDislikes(1);
                mem.setLikes(-1);
                mem.setOpinion(IConstants.OPINION.DISLIKED);
            } else {
                feedbackManager.postDislike(mem);
                mem.setDislikes(1);
                mem.setOpinion(IConstants.OPINION.DISLIKED);
            }
            refreshUi();
        });
        pdvMem.setOnViewTapListener((view, x, y) -> {
            if (canPerformTap) {
                setNavVisible(!navVisible);
            }
        });
        ivSendComment.setOnClickListener(view -> {
            if (!etComment.getText().toString().equals("")) {
                pbCommentSend.setVisibility(View.VISIBLE);
                ivSendComment.setVisibility(View.INVISIBLE);
                presenter.postComment(mem.getId(), etComment.getText().toString());
            }
        });
        ivAddToFavourites.setOnClickListener(v -> {
            if (mem.isFavorite()) {
                presenter.removeFromFavourites(mem.getId());
            } else {
                presenter.addToFavourites(mem.getId());
            }
        });
    }

    @Override
    public void setExitSharedElementCallback(SharedElementCallback callback) {
        super.setExitSharedElementCallback(callback);
    }

    @Override
    public void onNotRegistered() {
        pbCommentSend.setVisibility(View.INVISIBLE);
        ivSendComment.setVisibility(View.VISIBLE);
        AlertBuilder.showNotRegisteredPrompt(getContext());
    }

    @Override
    public void onBaseUpdated(List<CommentEntity> list) {
        if (list.size() > 0) {
            if (srlCommentsRefresh != null) {
                srlCommentsRefresh.setRefreshing(false);
                commentAdapter.updateListWhole(list);
                rvComments.scheduleLayoutAnimation();
                tvCommentEmpty.setVisibility(View.GONE);
            }
        } else {
            if (rvComments != null) {
                rvComments.setVisibility(View.GONE);
                tvCommentEmpty.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPartialUpdate(List<CommentEntity> list) {
        commentAdapter.updateListAtEnding(list);
    }

    @Override
    public void onErrorInLoading() {
        srlCommentsRefresh.setRefreshing(false);
        commentAdapter.onFailedToLoad();
    }

    @Override
    public void onStartLoading() {
        commentAdapter.onStartLoading();
    }

    @Override
    public void onCommentSentSuccessfully() {
        etComment.setText("");
        pbCommentSend.setVisibility(View.INVISIBLE);
        rvComments.setVisibility(View.VISIBLE);
        ivSendComment.setVisibility(View.VISIBLE);
        presenter.loadCommentsBase(mem.getId());
    }

    @Override
    public void onCommentSendFail() {
        Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
        pbCommentSend.setVisibility(View.INVISIBLE);
        ivSendComment.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAddedToFavourites() {
        mem.setFavorite(true);
        refreshUi();
    }

    @Override
    public void onRemovedFromFavourites() {
        mem.setFavorite(false);
        refreshUi();
    }

    @Override
    public void onError() {
        Toast.makeText(getContext(), getText(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onIsFavourite(boolean isFavourite) {
        mem.setFavorite(isFavourite);
        refreshUi();
    }

    private void disExpandComments() {
        if (getContext() != null)
            KeyboardHandler.hideKeyboard(getActivity());
        supPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    private void expandComments(boolean startWithComments) {
        supPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        if (startWithComments) {
            clExpandedUpperLayout.setAlpha(1);
            clNotExpandedBottomLayout.setAlpha(0);
        }
    }

    @Override
    public void loadCommentsPartial(int offset) {
        presenter.loadCommentsWithOffset(mem.getId(), offset);
    }

    @Override
    public void loadCommentsBase() {
        srlCommentsRefresh.setRefreshing(false);
        presenter.loadCommentsBase(mem.getId());
    }

    private void setImageDrawable(ImageView ivImage, int d) {
        if (getContext() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivImage.setImageDrawable(getContext().getDrawable(d));
            } else {
                ivImage.setImageDrawable(getContext().getResources().getDrawable(d));
            }
        }
    }
}
