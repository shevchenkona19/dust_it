package dustit.clientapp.mvp.ui.fragments;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.customviews.WrapperLinearLayoutManager;
import dustit.clientapp.mvp.model.entities.CommentEntity;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.presenters.activities.MemViewPresenter;
import dustit.clientapp.mvp.ui.adapters.CommentsRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.interfaces.IMemViewView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.managers.ThemeManager;
import me.relex.photodraweeview.PhotoDraweeView;

public class MemViewFragment extends Fragment implements CommentsRecyclerViewAdapter.ICommentInteraction, IMemViewView {
    private static final String MEM_KEY = "MEM_ENTITY";
    private static final String SHARED_TRANSITION_KEY = "sd";
    private MemEntity mem;
    private Unbinder unbinder;
    private CommentsRecyclerViewAdapter commentAdapter;
    private final MemViewPresenter presenter = new MemViewPresenter();
    private boolean isExpanded = false;
    private boolean isCommentsExpanded = false;
    private boolean isMoreLayoutVisible = false;
    private final Handler handler = new Handler();


    private int imageWidth = -1;
    private int imageHeight = -1;

    private enum Quarry {
        POST_LIKE,
        DELETE_LIKE,
        POST_DISLIKE,
        DELETE_DISLIKE
    }

    private MemViewFragment.Quarry currentQuarry;

    public interface IMemViewRatingInteractionListener {
        void passPostLike(String id);

        void passDeleteLike(String id);

        void passPostDislike(String id);

        void passDeleteDislike(String id);

        void closeMemView();
    }

    private IMemViewRatingInteractionListener interactionListener;

    @BindView(R.id.tvCommentEmptySet)
    TextView tvCommentEmpty;
    @BindView(R.id.ivMemViewMenu)
    ImageView ivMenu;
    @BindView(R.id.ivMemViewBack)
    ImageView ivBack;
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
    @BindView(R.id.clMemViewMoreLayout)
    ViewGroup vgMoreLayout;
    @BindView(R.id.tvMemViewSrc)
    TextView tvSrc;

    @Inject
    ThemeManager themeManager;

    public static MemViewFragment newInstance(MemEntity mem) {
        Bundle args = new Bundle();
        args.putParcelable(MEM_KEY, mem);
        MemViewFragment fragment = new MemViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args != null) {
            mem = args.getParcelable(MEM_KEY);
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
        View view = inflater.inflate(R.layout.activity_mem_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        App.get().getAppComponent().inject(this);
        presenter.bind(this);
        commentAdapter = new CommentsRecyclerViewAdapter(getContext(), this);
        pbCommentSend.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        initOnClicks();
        rvComments.setAdapter(commentAdapter);
        rvComments.setLayoutManager(new WrapperLinearLayoutManager(getContext()));
        srlCommentsRefresh.setOnRefreshListener(() -> {
            srlCommentsRefresh.setRefreshing(true);
            presenter.loadCommentsBase(mem.getId());
        });
        clUpperLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        DraweeController ctrl = Fresco.newDraweeControllerBuilder().setUri(IConstants.BASE_URL + "/feed/imgs?id=" + mem.getId())
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
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setProgressBarImage(new ProgressBarDrawable())
                .build();
        tvSrc.setText(mem.getSource());
        pdvMem.setController(ctrl);
        pdvMem.setHierarchy(hierarchy);
        refreshUi();
        initAutoHide();
        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    private void initAutoHide() {
        pdvMem.setOnScaleChangeListener((scaleFactor, focusX, focusY) -> {
            if ((imageHeight == -1 && imageWidth == -1)) {
                imageHeight = pdvMem.getHeight() / 2;
                imageWidth = pdvMem.getWidth() / 2;
            }
            if (imageHeight - focusX > 50 || imageWidth - focusY > 50) {
                //hide ui
                toolbar.setVisibility(View.GONE);
                tbLikePanel.setVisibility(View.GONE);
                isExpanded = true;
            }
        });
    }

    private void refreshUi() {
        if (mem.isFavorite()) {
            setImageDrawable(ivAddToFavourites, R.drawable.ic_added_to_favourites);
        } else {
            setImageDrawable(ivAddToFavourites, R.drawable.ic_add_to_favourites);
        }
        if (tvLikeCount != null) {
            tvLikeCount.setText(mem.getLikes());
            tvDislikeCount.setText(mem.getDislikes());
            final IConstants.OPINION opinion = mem.getOpinion();
            if (opinion != null) {
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

    private void initOnClicks() {
        ivExpandComments.setOnClickListener(view -> expandComments());
        ivDisexpand.setOnClickListener(view -> disExpandComments());
        ivMenu.setOnClickListener(v -> {
            Drawable drawable = ivMenu.getDrawable();
            if (drawable instanceof Animatable) {
                ((Animatable) drawable).start();
            }
            vgMoreLayout.setVisibility(isMoreLayoutVisible ? View.GONE : View.VISIBLE);
            isMoreLayoutVisible = !isMoreLayoutVisible;
            handler.postDelayed(() -> {
                if (getContext() != null) {
                    if (isMoreLayoutVisible) {
                        setImageDrawable(ivMenu, R.drawable.anim_from_cross_to_menu);
                    } else {
                        setImageDrawable(ivMenu, R.drawable.anim_from_menu_to_cross);
                    }
                }
            }, 300);
        });
        ivBack.setOnClickListener(view -> interactionListener.closeMemView());
        ivLike.setOnClickListener(view -> {
            if (mem.getOpinion() == IConstants.OPINION.LIKED) {
                presenter.deleteLike(mem.getId());
                currentQuarry = Quarry.DELETE_LIKE;
            } else {
                presenter.postLike(mem.getId());
                currentQuarry = Quarry.POST_LIKE;
            }
        });
        ivDislike.setOnClickListener(view -> {
            if (mem.getOpinion() == IConstants.OPINION.DISLIKED) {
                presenter.deleteDislike(mem.getId());
                currentQuarry = Quarry.DELETE_DISLIKE;
            } else {
                presenter.postDislike(mem.getId());
                currentQuarry = Quarry.POST_DISLIKE;
            }
        });
        pdvMem.setOnViewTapListener((view, x, y) -> {
            if (isExpanded) {
                toolbar.setVisibility(View.VISIBLE);
                tbLikePanel.setVisibility(View.VISIBLE);
                isExpanded = false;
            } else {
                toolbar.setVisibility(View.GONE);
                tbLikePanel.setVisibility(View.GONE);
                isExpanded = true;
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
        AlertBuilder.showNotRegisteredPrompt(getContext());
    }

    @Override
    public void onBaseUpdated(List<CommentEntity> list) {
        if (list.size() > 0) {
            srlCommentsRefresh.setRefreshing(false);
            commentAdapter.updateListWhole(list);
            tvCommentEmpty.setVisibility(View.GONE);
        } else {
            rvComments.setVisibility(View.GONE);
            tvCommentEmpty.setVisibility(View.VISIBLE);
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
    public void onErrorSendingQuarry() {
        Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
        currentQuarry = null;
    }

    @Override
    public void onQuarrySendedSuccessfully(String id) {
        final IConstants.OPINION opinion = mem.getOpinion();
        switch (currentQuarry) {
            case POST_LIKE:
                if (opinion == IConstants.OPINION.DISLIKED) mem.setDislikes(-1);
                mem.setLikes(1);
                mem.setOpinion(IConstants.OPINION.LIKED);
                interactionListener.passPostLike(id);
                break;
            case DELETE_LIKE:
                mem.setOpinion(IConstants.OPINION.NEUTRAL);
                mem.setLikes(-1);
                interactionListener.passDeleteLike(id);
                break;
            case POST_DISLIKE:
                if (opinion == IConstants.OPINION.LIKED) mem.setLikes(-1);
                mem.setDislikes(1);
                mem.setOpinion(IConstants.OPINION.DISLIKED);
                interactionListener.passPostDislike(id);
                break;
            case DELETE_DISLIKE:
                mem.setDislikes(-1);
                mem.setOpinion(IConstants.OPINION.NEUTRAL);
                interactionListener.passDeleteDislike(id);
                break;
            default:
                onErrorSendingQuarry();
                break;
        }
        refreshUi();
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

    private void disExpandComments() {
        final View view = clUpperLayout;
        if (getContext() != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            srlCommentsRefresh.setVisibility(View.GONE);
            srlCommentsRefresh.setEnabled(false);
            ConstraintSet set = new ConstraintSet();
            set.clone(clUpperLayout);
            set.clear(R.id.ablExpandablePanel, ConstraintSet.TOP);
            set.connect(R.id.ablExpandablePanel, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            set.applyTo(clUpperLayout);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setElevation(9);
                tbLikePanel.setElevation(9);
            }
            tvCommentEmpty.setVisibility(View.GONE);
            clNotExpandedBottomLayout.setVisibility(View.VISIBLE);
            clExpandedUpperLayout.setVisibility(View.GONE);
            rvComments.setVisibility(View.GONE);
            cvCommentSendPanel.setVisibility(View.GONE);
            pdvMem.setVisibility(View.VISIBLE);
            isCommentsExpanded = false;
        }
    }

    private void expandComments() {
        final ConstraintSet set = new ConstraintSet();
        set.clone(clUpperLayout);
        set.connect(R.id.ablExpandablePanel, ConstraintSet.TOP, R.id.abMemViewUpperBarLayout, ConstraintSet.BOTTOM, 0);
        set.clear(R.id.ablExpandablePanel, ConstraintSet.BOTTOM);
        set.applyTo(clUpperLayout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
            tbLikePanel.setElevation(0);
        }
        clNotExpandedBottomLayout.setVisibility(View.GONE);
        clExpandedUpperLayout.setVisibility(View.VISIBLE);
        cvCommentSendPanel.setVisibility(View.VISIBLE);
        presenter.loadCommentsBase(mem.getId());
        pdvMem.setVisibility(View.GONE);
        srlCommentsRefresh.setVisibility(View.VISIBLE);
        srlCommentsRefresh.setEnabled(true);
        rvComments.setVisibility(View.VISIBLE);
        isCommentsExpanded = true;
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
