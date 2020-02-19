package dustit.clientapp.mvp.ui.fragments;

import android.Manifest;
import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.appbar.AppBarLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.customviews.WrapperLinearLayoutManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.CommentEntity;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.NewAchievementEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;
import dustit.clientapp.mvp.presenters.activities.MemViewPresenter;
import dustit.clientapp.mvp.ui.activities.AnswersActivity;
import dustit.clientapp.mvp.ui.adapters.CommentsRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.dialog.AchievementUnlockedDialog;
import dustit.clientapp.mvp.ui.interfaces.IMemViewView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.ImageShareUtils;
import dustit.clientapp.utils.KeyboardHandler;
import dustit.clientapp.utils.managers.ReviewManager;

import static dustit.clientapp.utils.IConstants.BASE_URL;

public class MemViewFragment extends Fragment implements CommentsRecyclerViewAdapter.ICommentInteraction, IMemViewView {
    private static final String MEM_KEY = "MEM_ENTITY";
    private static final String COMMENTS_KEY = "COMMENTS_KEY";
    private static final String SHOW_FAVORITES = "SHOW_FAVORITES";
    private static final int PERMISSION_DIALOG = 2020;
    private final MemViewPresenter presenter = new MemViewPresenter();
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
    View tbLikePanel;
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
    View ivSendComment;
    @BindView(R.id.srlMemViewRefreshComments)
    SwipeRefreshLayout srlCommentsRefresh;
    @BindView(R.id.pbIMemViewCommentSendingLoading)
    ProgressBar pbCommentSend;
    @BindView(R.id.abMemViewUpperBarLayout)
    AppBarLayout toolbar;
    @BindView(R.id.zdvMem)
    PhotoView pdvMem;
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
    UserSettingsDataManager userSettingsDataManager;
    private MemEntity mem;
    private Unbinder unbinder;
    private CommentsRecyclerViewAdapter commentAdapter;
    private boolean startComments = false;
    private boolean canPerformTap = true;
    private boolean navVisible = true;
    private int imageWidth = -1;
    private int imageHeight = -1;
    private int myId;
    private int answerUserId;
    private boolean isAnswering = false;
    private int commentId;
    private String answeringUsername;
    private boolean showFavorites = false;
    private boolean showNewComment = false;
    private int newCommentParentId;
    private int newCommentId;
    private IMemViewRatingInteractionListener interactionListener;
    private SlidingUpPanelLayout.PanelState prevPanelState = SlidingUpPanelLayout.PanelState.COLLAPSED;

    public static MemViewFragment newInstance(MemEntity mem, boolean startComments, int userId) {
        Bundle args = new Bundle();
        args.putParcelable(MEM_KEY, mem);
        args.putBoolean(COMMENTS_KEY, startComments);
        args.putInt(IConstants.IBundle.USER_ID, userId);
        final MemViewFragment fragment = new MemViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static MemViewFragment newInstance(MemEntity mem, int userId, boolean showFavorites) {
        Bundle args = new Bundle();
        args.putParcelable(MEM_KEY, mem);
        args.putBoolean(SHOW_FAVORITES, showFavorites);
        args.putInt(IConstants.IBundle.USER_ID, userId);
        final MemViewFragment fragment = new MemViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static MemViewFragment newInstance(@NotNull MemEntity memEntity, boolean startComments, int loadId, int parentComment, int newComment) {
        Bundle args = new Bundle();
        args.putParcelable(MEM_KEY, memEntity);
        args.putBoolean(COMMENTS_KEY, startComments);
        args.putInt(IConstants.IBundle.USER_ID, loadId);
        args.putInt(IConstants.IBundle.PARENT_COMMENT_ID, parentComment);
        args.putInt(IConstants.IBundle.NEW_COMMENT_ID, newComment);
        args.putBoolean(IConstants.IBundle.SHOW_COMMENTS, true);
        final MemViewFragment fragment = new MemViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args != null) {
            mem = args.getParcelable(MEM_KEY);
            myId = args.getInt(IConstants.IBundle.USER_ID);
            startComments = args.getBoolean(COMMENTS_KEY);
            showFavorites = args.getBoolean(SHOW_FAVORITES, false);
            showNewComment = args.getBoolean(IConstants.IBundle.SHOW_COMMENTS, false);
            newCommentParentId = args.getInt(IConstants.IBundle.PARENT_COMMENT_ID);
            newCommentId = args.getInt(IConstants.IBundle.NEW_COMMENT_ID);
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
        presenter.bindToView(this);
        commentAdapter = new CommentsRecyclerViewAdapter(getContext(), myId, this);
        commentAdapter.setHasStableIds(true);
        pbCommentSend.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        initOnClicks();
        rvComments.setAdapter(commentAdapter);
        rvComments.setNestedScrollingEnabled(true);
        rvComments.setHasFixedSize(true);
        rvComments.setLayoutManager(new WrapperLinearLayoutManager(getContext()));
        srlCommentsRefresh.setOnRefreshListener(() -> {
            srlCommentsRefresh.setRefreshing(true);
            presenter.loadCommentsBase(mem.getId());
            tvCommentEmpty.setVisibility(View.INVISIBLE);
        });
        clUpperLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        Glide.with(this)
                .load(Uri.parse(BASE_URL + "/feed/imgs?id=" + mem.getId()))
                .apply(new RequestOptions().placeholder(R.drawable.mem_placeholder))
                .into(pdvMem);
        if (showFavorites) {
            tbUpperToolbar.inflateMenu(R.menu.favorites_controls);
            tbUpperToolbar.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                switch (id) {
                    case R.id.share:
                        shareMem();
                        return true;
                    case R.id.download:
                        downloadMem();
                        return true;
                    default:
                        return false;
                }
            });
        }
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
                        enableClicks();
                        setStatusbarForComments();
                        prevPanelState = SlidingUpPanelLayout.PanelState.EXPANDED;
                        canPerformTap = true;
                        if (showNewComment) {
                            presenter.getCommentsToCommentId(mem.getId(), newCommentParentId);
                        } else {
                            presenter.loadCommentsBase(mem.getId());
                        }
                        supPanel.setDragView(clExpandedUpperLayout);
                        break;
                    case COLLAPSED:
                        disableClicks();
                        setStatusbarForMemView();
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
                            case DRAGGING:
                                supPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                                break;
                        }
                }
            }
        });
        refreshUi();
        initAutoHide();
        if (startComments) {
            expandComments(true);
            setStatusbarForComments();
        }
        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isAnswering) {
                    if (!etComment.getText().toString().contains("@" + answeringUsername) && !etComment.getText().toString().equals("")) {
                        isAnswering = false;
                        answeringUsername = "";
                        answerUserId = -1;
                    }
                }
            }
        });
        if (!userSettingsDataManager.isFcmUpdated()) {
            presenter.updateFcmId();
        }
        return view;
    }

    private void shareMem() {
        if (getContext() != null) {
            ImageShareUtils.shareImage(IConstants.BASE_URL + "/feed/imgs?id=" + mem.getId(), getContext());
        }
    }

    private void downloadMem() {
        presenter.downloadImage(mem.getId());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() != null) {
            if (supPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                setStatusbarForComments();
            } else if (supPanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                setStatusbarForMemView();
            }
        }
    }

    private void setStatusbarForComments() {
        if (getActivity() != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.commentsBackground));
            }
    }

    private void setStatusbarForMemView() {
        if (getActivity() != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            }
    }

    private void enableClicks() {
        ivLike.setVisibility(View.GONE);
        ivDislike.setVisibility(View.GONE);
        ivAddToFavourites.setVisibility(View.GONE);
    }

    private void disableClicks() {
        ivLike.setVisibility(View.VISIBLE);
        ivDislike.setVisibility(View.VISIBLE);
        ivAddToFavourites.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        presenter.unbind();
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
            if (tvDislikeCount != null) {
                tvCommentsCount.setText(String.valueOf(mem.getCommentsCount()));
                if (tvLikeCount != null) {
                    tvLikeCount.setText(String.valueOf(mem.getLikes()));
                    tvDislikeCount.setText(String.valueOf(mem.getDislikes()));
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
                presenter.deleteLike(mem);
                mem.setOpinion(IConstants.OPINION.NEUTRAL);
                mem.addLikes(-1);
            } else if (mem.getOpinion() == IConstants.OPINION.DISLIKED) {
                if (getContext() != null)
                    ReviewManager.get().positiveCount(new WeakReference<>(getContext()));
                presenter.postLike(mem);
                mem.addLikes(1);
                mem.addDislikes(-1);
                mem.setOpinion(IConstants.OPINION.LIKED);
            } else {
                if (getContext() != null)
                    ReviewManager.get().positiveCount(new WeakReference<>(getContext()));
                presenter.postLike(mem);
                mem.addLikes(1);
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
                presenter.deleteDislike(mem);
                mem.addDislikes(-1);
                mem.setOpinion(IConstants.OPINION.NEUTRAL);
            } else if (mem.getOpinion() == IConstants.OPINION.LIKED) {
                presenter.postDislike(mem);
                mem.addDislikes(1);
                mem.addLikes(-1);
                mem.setOpinion(IConstants.OPINION.DISLIKED);
            } else {
                presenter.postDislike(mem);
                mem.addDislikes(1);
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
                if (isAnswering) {
                    presenter.postAnswer(mem.getId(), answerUserId, etComment.getText().toString(), commentId);
                } else {
                    presenter.postComment(mem.getId(), etComment.getText().toString());
                }
            }
        });
        ivAddToFavourites.setOnClickListener(v -> {
            if (mem.isFavorite()) {
                presenter.removeFromFavourites(mem);
            } else {
                if (getContext() != null)
                    ReviewManager.get().positiveCount(new WeakReference<>(getContext()));
                presenter.addToFavourites(mem);
            }
        });
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
                rvComments.setVisibility(View.VISIBLE);
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
    public void changedFeedback(RefreshedMem refreshedMem) {
        mem.setLikes(refreshedMem.getLikes());
        mem.setDislikes(refreshedMem.getDislikes());
        mem.setOpinion(refreshedMem.getOpinion());
        mem.setFavorite(refreshedMem.isFavourite());
        refreshUi();
    }

    @Override
    public void onError(RestoreMemEntity restoreMemEntity) {
        mem.setLikes(restoreMemEntity.getLikes());
        mem.setDislikes(restoreMemEntity.getDislikes());
        mem.setOpinion(restoreMemEntity.getOpinion());
        mem.setFavorite(restoreMemEntity.isFavourite());
        refreshUi();
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
    public void onError() {
        Toast.makeText(getContext(), getText(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onIsFavourite(boolean isFavourite) {
        mem.setFavorite(isFavourite);
        refreshUi();
    }

    @Override
    public void onAchievementUpdate(NewAchievementEntity achievementEntity) {
        if (getContext() != null) {
            if (presenter.isRegistered())
                new AchievementUnlockedDialog(getContext(), achievementEntity.isFinalLevel()).bind(achievementEntity).show();
        }
    }

    @Override
    public void onAnswerSentSuccessfully() {
        presenter.getCommentsToCommentId(mem.getId(), commentId);
        answerUserId = -1;
        answeringUsername = "";
        pbCommentSend.setVisibility(View.INVISIBLE);
        ivSendComment.setVisibility(View.VISIBLE);
        commentId = -1;
    }

    @Override
    public void onCommentsToCommentIdLoaded(List<CommentEntity> list) {
        showNewComment = false;
        commentAdapter.updateListWhole(list);
        rvComments.scrollToPosition(list.size() - 1);
        if (!isAnswering) {
            openAnswersForComment(list.get(list.size() - 1), true, newCommentId);
        } else {
            etComment.setText("");
            isAnswering = false;
        }
    }

    @Override
    public void onDownloaded(String pathToImage) {
        if (getContext() != null)
            Toast.makeText(getContext(), getString(R.string.downloaded_to) + pathToImage, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean checkPermission() {
        if (getContext() != null) {
            int permCheckRead = ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int permCheckWrite = ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return permCheckRead == PackageManager.PERMISSION_GRANTED
                    || permCheckWrite == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    @Override
    public void getPermissions() {
        if (getActivity() != null)
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_DIALOG);
    }

    @Override
    public void onDownloadFailed() {
        if (getContext() != null)
            Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
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

    @Override
    public void answerComment(CommentEntity comment, int commentId) {
        etComment.setText(String.format("@%s,", comment.getUsername()));
        isAnswering = true;
        answerUserId = comment.getUserId();
        answeringUsername = comment.getUsername();
        this.commentId = commentId;
    }

    @Override
    public void openAnswersForComment(CommentEntity commentEntity) {
        if (getContext() != null) {
            Intent intent = new Intent(getContext(), AnswersActivity.class);
            intent.putExtra(IConstants.IBundle.BASE_COMMENT, commentEntity);
            intent.putExtra(IConstants.IBundle.MEM_ID, mem.getId());
            intent.putExtra(IConstants.IBundle.MY_ID, myId);
            startActivity(intent);
        }
    }

    public void openAnswersForComment(CommentEntity commentEntity, boolean startComments, int newCommentId) {
        if (getContext() != null) {
            Intent intent = new Intent(getContext(), AnswersActivity.class);
            intent.putExtra(IConstants.IBundle.BASE_COMMENT, commentEntity);
            intent.putExtra(IConstants.IBundle.MEM_ID, mem.getId());
            intent.putExtra(IConstants.IBundle.SHOW_COMMENT, startComments);
            intent.putExtra(IConstants.IBundle.NEW_COMMENT_ID, newCommentId);
            intent.putExtra(IConstants.IBundle.MY_ID, myId);
            startActivity(intent);
        }
    }

    private void setImageDrawable(ImageView ivImage, int d) {
        if (getContext() != null) {
            if (ivImage != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivImage.setImageDrawable(getContext().getDrawable(d));
                } else {
                    ivImage.setImageDrawable(getContext().getResources().getDrawable(d));
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_DIALOG) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    presenter.downloadImage(mem.getId());
                }
            } else {
                onError();
            }
        }
    }

    public interface IMemViewRatingInteractionListener {
        void closeMemView();
    }
}
