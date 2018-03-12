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
import android.support.v4.view.ViewCompat;
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
import dustit.clientapp.utils.L;
import dustit.clientapp.utils.managers.ThemeManager;
import me.relex.photodraweeview.OnScaleChangeListener;
import me.relex.photodraweeview.OnViewTapListener;
import me.relex.photodraweeview.PhotoDraweeView;

public class MemViewFragment extends Fragment implements CommentsRecyclerViewAdapter.ICommentInteraction, IMemViewView {
    private static final String MEM_KEY = "MEM_ENTITY";
    private static final String SHARED_TRANSITION_KEY = "sd";
    private MemEntity mem;
    private Unbinder unbinder;
    private String themeId;
    private CommentsRecyclerViewAdapter commentAdapter;
    private final MemViewPresenter presenter = new MemViewPresenter();
    private boolean isExpanded = false;
    private boolean isCommentsExpanded = false;
    private String transitionName;
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

    public static MemViewFragment newInstance(MemEntity mem, String transitionName) {
        Bundle args = new Bundle();
        args.putParcelable(MEM_KEY, mem);
        args.putString(SHARED_TRANSITION_KEY, transitionName);
        MemViewFragment fragment = new MemViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args != null) {
            mem = args.getParcelable(MEM_KEY);
            transitionName = args.getString(SHARED_TRANSITION_KEY);
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
        ViewCompat.setTransitionName(pdvMem, transitionName);
        App.get().getAppComponent().inject(this);
        presenter.bind(this);
        commentAdapter = new CommentsRecyclerViewAdapter(getContext(), this);
        pbCommentSend.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        initOnClicks();
        rvComments.setAdapter(commentAdapter);
        rvComments.setLayoutManager(new WrapperLinearLayoutManager(getContext()));
        srlCommentsRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srlCommentsRefresh.setRefreshing(true);
                presenter.loadCommentsBase(mem.getId());
            }
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
                        ActivityCompat.startPostponedEnterTransition(getActivity());
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
        ivExpandComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandComments();
            }
        });
        ivDisexpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disExpandComments();
            }
        });
        refreshUi();
        setColors();
        initAutoHide();
        themeId = themeManager.subscribeToThemeChanges(new ThemeManager.IThemable() {
            @Override
            public void notifyThemeChanged(ThemeManager.Theme t) {
                setColors();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        themeManager.unsubscribe(themeId);
        super.onDestroyView();
    }

    private void setColors() {
        toolbar.setBackgroundResource(themeManager.getPrimaryColor());
        clUpperLayout.setBackgroundResource(themeManager.getBackgroundMainColor());
        tbLikePanel.setBackgroundResource(themeManager.getPrimaryColor());
        tvCommentsLabel.setTextColor(getColorFromResources(themeManager.getMainTextToolbarColor()));
        tvLikeCount.setTextColor(getColorFromResources(themeManager.getAccentColor()));
        tvDislikeCount.setTextColor(getColorFromResources(themeManager.getAccentColor()));
        cvCommentSendPanel.setCardBackgroundColor(getColorFromResources(themeManager.getCardBackgroundColor()));
        tvCommentEmpty.setTextColor(getColorFromResources(themeManager.getSecondaryTextMainAppColor()));
        etComment.setTextColor(getColorFromResources(themeManager.getMainTextMainAppColor()));
        etComment.setHintTextColor(getColorFromResources(themeManager.getSecondaryTextMainAppColor()));
        ivBack.setColorFilter(getColorFromResources(themeManager.getAccentColor()), PorterDuff.Mode.SRC_ATOP);
        ivMenu.setColorFilter(getColorFromResources(themeManager.getAccentColor()), PorterDuff.Mode.SRC_ATOP);
        ivDisexpand.setColorFilter(getColorFromResources(themeManager.getAccentColor()), PorterDuff.Mode.SRC_ATOP);
        ivAddToFavourites.setColorFilter(getColorFromResources(themeManager.getAccentColor()), PorterDuff.Mode.SRC_ATOP);
        ivExpandComments.setColorFilter(getColorFromResources(themeManager.getAccentColor()), PorterDuff.Mode.SRC_ATOP);
        ivLike.setColorFilter(getColorFromResources(themeManager.getAccentColor()), PorterDuff.Mode.SRC_ATOP);
        ivDislike.setColorFilter(getColorFromResources(themeManager.getAccentColor()), PorterDuff.Mode.SRC_ATOP);

    }

    private int getColorFromResources(int c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext() != null) {
                return getContext().getColor(c);
            } else {
                return 0;
            }
        } else {
            return getResources().getColor(c);
        }
    }

    private void initAutoHide() {
        pdvMem.setOnScaleChangeListener(new OnScaleChangeListener() {
            @Override
            public void onScaleChange(float scaleFactor, float focusX, float focusY) {
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
            }
        });
    }

    private void refreshUi() {
        if (tvLikeCount != null) {
            tvLikeCount.setText(mem.getLikes());
            tvDislikeCount.setText(mem.getDislikes());
            final IConstants.OPINION opinion = mem.getOpinion();
            if (opinion != null) {
                switch (opinion) {
                    case LIKED:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ivLike.setImageDrawable(getContext().getDrawable(R.drawable.ic_like_pressed));
                        } else {
                            ivLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_pressed));
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ivDislike.setImageDrawable(getContext().getDrawable(R.drawable.ic_dislike));
                        } else {
                            ivDislike.setImageDrawable(getResources().getDrawable(R.drawable.ic_dislike));
                        }
                        break;
                    case DISLIKED:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ivLike.setImageDrawable(getContext().getDrawable(R.drawable.ic_like));
                        } else {
                            ivLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ivDislike.setImageDrawable(getContext().getDrawable(R.drawable.ic_dislike_pressed));
                        } else {
                            ivDislike.setImageDrawable(getResources().getDrawable(R.drawable.ic_dislike_pressed));
                        }
                        break;
                    case NEUTRAL:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ivLike.setImageDrawable(getContext().getDrawable(R.drawable.ic_like));
                        } else {
                            ivLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ivDislike.setImageDrawable(getContext().getDrawable(R.drawable.ic_dislike));
                        } else {
                            ivDislike.setImageDrawable(getResources().getDrawable(R.drawable.ic_dislike));
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void initOnClicks() {
        final IConstants.OPINION opinion = mem.getOpinion();
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = ivMenu.getDrawable();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }
                vgMoreLayout.setVisibility(isMoreLayoutVisible ? View.GONE : View.VISIBLE);
                isMoreLayoutVisible = !isMoreLayoutVisible;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getContext() != null) {
                            if (isMoreLayoutVisible) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    ivMenu.setImageDrawable(getContext().getDrawable(R.drawable.anim_from_cross_to_menu));
                                } else {
                                    ivMenu.setImageDrawable(getContext().getResources().getDrawable(R.drawable.anim_from_cross_to_menu));
                                }
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    ivMenu.setImageDrawable(getContext().getDrawable(R.drawable.anim_from_menu_to_cross));
                                } else {
                                    ivMenu.setImageDrawable(getContext().getResources().getDrawable(R.drawable.anim_from_menu_to_cross));
                                }
                            }
                        }
                    }
                }, 300);
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interactionListener.closeMemView();
            }
        });
        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (opinion == IConstants.OPINION.LIKED) {
                    presenter.deleteLike(mem.getId());
                    currentQuarry = Quarry.DELETE_LIKE;
                } else {
                    presenter.postLike(mem.getId());
                    currentQuarry = Quarry.POST_LIKE;
                }
            }
        });
        ivDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (opinion == IConstants.OPINION.DISLIKED) {
                    presenter.deleteDislike(mem.getId());
                    currentQuarry = Quarry.DELETE_DISLIKE;
                } else {
                    presenter.postDislike(mem.getId());
                    currentQuarry = Quarry.POST_DISLIKE;
                }
            }
        });
        pdvMem.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (isExpanded) {
                    toolbar.setVisibility(View.VISIBLE);
                    tbLikePanel.setVisibility(View.VISIBLE);
                    isExpanded = false;
                } else {
                    toolbar.setVisibility(View.GONE);
                    tbLikePanel.setVisibility(View.GONE);
                    isExpanded = true;
                }
            }
        });
        ivSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etComment.getText().toString().equals("")) {
                    pbCommentSend.setVisibility(View.VISIBLE);
                    ivSendComment.setVisibility(View.INVISIBLE);
                    presenter.postComment(mem.getId(), etComment.getText().toString());
                }
            }
        });
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
            //TODO: show empty
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

    private void disExpandComments() {
        View view = clUpperLayout;
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
                toolbar.setElevation(1);
                tbLikePanel.setElevation(1);
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
        ConstraintSet set = new ConstraintSet();
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
}
