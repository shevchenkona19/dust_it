package dustit.clientapp.mvp.ui.activities;

import android.animation.LayoutTransition;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.customviews.WrapperLinearLayoutManager;
import dustit.clientapp.customviews.zoomableview.ZoomableDraweeViewSupport;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.CommentEntity;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.presenters.activities.MemViewPresenter;
import dustit.clientapp.mvp.ui.adapters.CommentsRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.interfaces.IMemViewView;
import dustit.clientapp.utils.IConstants;

/**
 * Created by shevc on 07.10.2017.
 * Let's GO!
 */

public class MemViewActivity extends AppCompatActivity implements CommentsRecyclerViewAdapter.ICommentInteraction, IMemViewView {

    private MemEntity mem;

    private enum Quarry {
        POST_LIKE,
        DELETE_LIKE,
        POST_DISLIKE,
        DELETE_DISLIKE
    }

    private Quarry currentQuarry;


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
    @BindView(R.id.appBarLayout)
    AppBarLayout toolbar;
    @BindView(R.id.pbDislikeLoading)
    ProgressBar pbDislikeLoading;
    @BindView(R.id.pbLikeLoading)
    ProgressBar pbLikeLoading;
    @BindView(R.id.zdvMem)
    ZoomableDraweeViewSupport zdvMem;

    private CommentsRecyclerViewAdapter commentAdapter;
    private final MemViewPresenter presenter = new MemViewPresenter();

    @Inject
    DataManager dataManager;
    private boolean isExpanded = false;


    public interface IMemViewRatingInteractionListener {
        void passPostLike(String id);

        void passDeleteLike(String id);

        void passPostDislike(String id);

        void passDeleteDislike(String id);
    }

    private static IMemViewRatingInteractionListener listener;

    public static void bind(IMemViewRatingInteractionListener interactionListener) {
        listener = interactionListener;
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
    public void onCommentSentSuccesfully() {
        etComment.setText("");
        pbCommentSend.setVisibility(View.INVISIBLE);
        ivSendComment.setVisibility(View.VISIBLE);
        presenter.loadCommentsBase(mem.getId());
        //add comment
    }

    @Override
    public void onCommentSendFail() {
        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        pbCommentSend.setVisibility(View.INVISIBLE);
        ivSendComment.setVisibility(View.VISIBLE);
        //show fail
    }

    @Override
    public void onErrorSendingQuerry() {
        hideDislikesLoading();
        hideLikesLoading();
        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        currentQuarry = null;
    }

    @Override
    public void onQuerrySendedSuccessfully(String id) {
        switch (currentQuarry) {
            case POST_LIKE:
                if (mem.isDisliked()) {
                    mem.setDisliked(false);
                    mem.setDislikes(String.valueOf(Integer.parseInt(mem.getDislikes()) - 1));
                }
                mem.setLikes(String.valueOf(Integer.parseInt(mem.getLikes()) + 1));
                mem.setLiked(true);
                listener.passPostLike(id);
                break;
            case DELETE_LIKE:
                mem.setLiked(false);
                mem.setLikes(String.valueOf(Integer.parseInt(mem.getLikes()) - 1));
                listener.passDeleteLike(id);
                break;
            case POST_DISLIKE:
                if (mem.isLiked()) {
                    mem.setLiked(false);
                    mem.setLikes(String.valueOf(Integer.parseInt(mem.getLikes()) - 1));
                }
                mem.setDislikes(String.valueOf(Integer.parseInt(mem.getDislikes()) + 1));
                mem.setDisliked(true);
                listener.passPostDislike(id);
                break;
            case DELETE_DISLIKE:
                mem.setDislikes(String.valueOf(Integer.parseInt(mem.getDislikes()) - 1));
                mem.setDisliked(false);
                listener.passDeleteDislike(id);
                break;
            default:
                onErrorSendingQuerry();
                break;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mem_view);
        App.get().getAppComponent().inject(this);
        ButterKnife.bind(this);
        pbDislikeLoading.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        pbLikeLoading.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        presenter.bind(this);
        commentAdapter = new CommentsRecyclerViewAdapter(this, this);
        mem = getIntent().getExtras().getParcelable(FeedActivity.MEM_ENTITY);
        pbCommentSend.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        initSlidr();
        initOnClicks();
        tvLikeCount.setText(mem.getLikes());
        tvDislikeCount.setText(mem.getDislikes());
        rvComments.setAdapter(commentAdapter);
        rvComments.setLayoutManager(new WrapperLinearLayoutManager(this));
        srlCommentsRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srlCommentsRefresh.setRefreshing(true);
                presenter.loadCommentsBase(mem.getId());
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            clUpperLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }
        DraweeController ctrl = Fresco.newDraweeControllerBuilder().setUri(IConstants.BASE_URL + "/feed/imgs?id=" + mem.getId())
                .setTapToRetryEnabled(true).build();
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setProgressBarImage(new ProgressBarDrawable())
                .build();
        zdvMem.setController(ctrl);
        zdvMem.setHierarchy(hierarchy);
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
        if (mem.isLiked()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivLike.setImageDrawable(this.getDrawable(R.drawable.ic_like));
            } else {
                ivLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
            }
        } else if (mem.isDisliked()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivDislike.setImageDrawable(this.getDrawable(R.drawable.ic_dislike));
            } else {
                ivDislike.setImageDrawable(getResources().getDrawable(R.drawable.ic_dislike));
            }
        }
    }

    private void initOnClicks() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLikesLoading();
                if (mem.isLiked()) {
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
                showDislikesLoading();
                if (mem.isDisliked()) {
                    presenter.deleteDislike(mem.getId());
                    currentQuarry = Quarry.DELETE_DISLIKE;
                } else {
                    presenter.postDislike(mem.getId());
                    currentQuarry = Quarry.POST_DISLIKE;
                }
            }
        });
        zdvMem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExpanded) {
                    tbLikePanel.setVisibility(View.VISIBLE);
                    tbUpperToolbar.setVisibility(View.VISIBLE);
                    isExpanded = false;
                } else {
                    tbUpperToolbar.setVisibility(View.GONE);
                    tbLikePanel.setVisibility(View.GONE);
                    isExpanded = true;
                }
            }
        });

        ivSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etComment.getText().toString().equals("")) {
                    presenter.postComment(mem.getId(), etComment.getText().toString());
                    pbCommentSend.setVisibility(View.VISIBLE);
                    ivSendComment.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void initSlidr() {
        SlidrConfig config = new SlidrConfig.Builder()
                .primaryColor(getResources().getColor(R.color.colorPrimary))
                .secondaryColor(getResources().getColor(R.color.colorPrimaryDark))
                .position(SlidrPosition.VERTICAL)
                .sensitivity(0.5f)
                .scrimColor(Color.BLACK)
                .scrimStartAlpha(0.6f)
                .scrimEndAlpha(0f)
                .velocityThreshold(2400)
                .distanceThreshold(0.25f)
                .edge(true)
                .edgeSize(0.18f)
                .build();
        Slidr.attach(this, config);
    }

    private void disExpandComments() {
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
        clNotExpandedBottomLayout.setVisibility(View.VISIBLE);
        clExpandedUpperLayout.setVisibility(View.GONE);
        rvComments.setVisibility(View.GONE);
        cvCommentSendPanel.setVisibility(View.GONE);
        zdvMem.setVisibility(View.VISIBLE);
    }

    private void expandComments() {
        ConstraintSet set = new ConstraintSet();
        set.clone(clUpperLayout);
        set.connect(R.id.ablExpandablePanel, ConstraintSet.TOP, R.id.appBarLayout, ConstraintSet.BOTTOM, 0);
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
        zdvMem.setVisibility(View.GONE);
        srlCommentsRefresh.setVisibility(View.VISIBLE);
        srlCommentsRefresh.setEnabled(true);
        rvComments.setVisibility(View.VISIBLE);
    }

    private void showLikesLoading() {
        ivLike.setVisibility(View.INVISIBLE);
        tvLikeCount.setVisibility(View.INVISIBLE);
        pbLikeLoading.setVisibility(View.VISIBLE);
    }

    private void hideLikesLoading() {
        pbLikeLoading.setVisibility(View.GONE);
        ivLike.setVisibility(View.VISIBLE);
        tvLikeCount.setVisibility(View.VISIBLE);
    }

    private void showDislikesLoading() {
        ivDislike.setVisibility(View.INVISIBLE);
        tvDislikeCount.setVisibility(View.INVISIBLE);
        pbDislikeLoading.setVisibility(View.VISIBLE);
    }

    private void hideDislikesLoading() {
        pbDislikeLoading.setVisibility(View.GONE);
        ivDislike.setVisibility(View.VISIBLE);
        tvDislikeCount.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        listener = null;
        super.onDestroy();
    }
}
