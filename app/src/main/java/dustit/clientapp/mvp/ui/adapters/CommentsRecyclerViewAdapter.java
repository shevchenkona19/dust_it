package dustit.clientapp.mvp.ui.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.CommentEntity;
import dustit.clientapp.mvp.ui.activities.AccountActivity;
import dustit.clientapp.utils.AchievementHelper;
import dustit.clientapp.utils.GlideApp;
import dustit.clientapp.utils.GlideRequests;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.L;

/**
 * Created by Никита on 11.11.2017.
 */

public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Inject
    DataManager dataManager;
    private List<CommentEntity> list;
    private LayoutInflater inflater;
    private boolean isLoading = false;
    private boolean commentsEnded = false;
    private Context context;
    private GlideRequests glide;
    private int myId;
    private ICommentInteraction interactionListener;

    public CommentsRecyclerViewAdapter(Context context, int myId, ICommentInteraction commentInteraction) {
        list = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        interactionListener = commentInteraction;
        this.myId = myId;
        glide = GlideApp.with(context);
        this.context = context;
        App.get().getAppComponent().inject(this);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View v1 = inflater.inflate(R.layout.item_loading, parent, false);
            return new FeedLoadingViewHolder(v1);
        }
        View v3 = inflater.inflate(R.layout.item_comments_comment, parent, false);
        return new CommentViewHolder(v3);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        int pos = holder.getAdapterPosition();
        if (holder instanceof CommentViewHolder) {
            final CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            CommentEntity comment = list.get(pos);
            commentViewHolder.bind(comment);
            commentViewHolder.tvAnswersCount.setOnClickListener((v -> interactionListener.openAnswersForComment(comment)));
            commentViewHolder.ivAnswers.setOnClickListener(v -> interactionListener.openAnswersForComment(comment));
            commentViewHolder.sdvUserPhoto.setOnClickListener((v) -> {
                int userId = comment.getUserId();
                Intent intent = new Intent(context, AccountActivity.class);
                intent.putExtra(IConstants.IBundle.IS_ME, userId == myId);
                if (userId != myId) {
                    intent.putExtra(IConstants.IBundle.USER_ID, comment.getUserId());
                }
                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) context).toBundle());
            });
            glide.load(AchievementHelper.resolveAchievementSmallIcon("likes", comment.getLikeAchievementLvl())).into(commentViewHolder.ivLikeLevel);
            glide.load(AchievementHelper.resolveAchievementSmallIcon("dislikes", comment.getDislikesAchievementLvl())).into(commentViewHolder.ivDislikeLevel);
            glide.load(AchievementHelper.resolveAchievementSmallIcon("comments", comment.getCommentsAchievementLvl())).into(commentViewHolder.ivCommentsLevel);
            glide.load(AchievementHelper.resolveAchievementSmallIcon("favourites", comment.getFavouritesAchievementLvl())).into(commentViewHolder.ivFavouritesLevel);
            glide.load(AchievementHelper.resolveAchievementSmallIcon("views", comment.getViewsAchievementLvl())).into(commentViewHolder.ivViewsLevel);
            if (comment.getAnswers() > 0) {
                commentViewHolder.tvAnswersCount.setVisibility(View.VISIBLE);
                commentViewHolder.tvAnswersCount.setText(String.valueOf(comment.getAnswers()));
                commentViewHolder.ivAnswers.setVisibility(View.VISIBLE);
            } else {
                commentViewHolder.tvAnswersCount.setVisibility(View.GONE);
                commentViewHolder.ivAnswers.setVisibility(View.GONE);
            }
            commentViewHolder.btnAnswer.setOnClickListener(v -> interactionListener.answerComment(comment, comment.getId()));
            if (comment.getFirstHundred()) {
                glide.load(R.drawable.ic_achievement_first100_small).into(commentViewHolder.ivFirst);
            } else if (comment.getFirstThousand()) {
                glide.load(R.drawable.ic_achievement_first1000_small).into(commentViewHolder.ivFirst);
            }

            final String monthDay = comment.getDateOfPost().substring(
                    comment.getDateOfPost().indexOf('T') - 5, comment.getDateOfPost().indexOf('T'));
            final String hourMinute = comment.getDateOfPost().substring(
                    comment.getDateOfPost().indexOf('T') + 1, comment.getDateOfPost().indexOf('T') + 6);
            commentViewHolder.tvDateStamp.setText(hourMinute + " " + monthDay);
        } else if (holder instanceof FeedFailedToLoadViewHolder) {
            FeedFailedToLoadViewHolder failed = (FeedFailedToLoadViewHolder) holder;
            failed.btnRetry.setOnClickListener(v -> interactionListener.loadCommentsBase());
        } else if (holder instanceof FeedLoadingViewHolder) {
            if (!isLoading && !commentsEnded) {
                interactionListener.loadCommentsPartial(list.size() - 1);
                isLoading = true;
            }
        }
    }

    @Override
    public long getItemId(int position) {
        final CommentEntity comment = list.get(position);
        if (comment == null) {
            return -1;
        } else {
            return comment.getId();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) == null) {
            return 1;
        } else {
            return 0;
        }
    }

    public void onStartLoading() {
        Handler handler = new Handler();
        isLoading = true;
        if (!list.contains(null)) {
            list.add(null);
            handler.postDelayed(() -> notifyItemInserted(list.size() - 1), 100);
        } else {
            handler.postDelayed(() -> notifyItemChanged(list.size() - 1), 100);
        }
    }

    public void onFailedToLoad() {
        isLoading = false;
        notifyItemChanged(list.size() - 1);
    }

    public void updateListWhole(List<CommentEntity> list) {
        this.list.clear();
        this.list.addAll(list);
        this.list.add(null);
        isLoading = false;
        commentsEnded = false;
        notifyDataSetChanged();
    }

    public void updateListAtEnding(List<CommentEntity> list) {
        if (list.size() > 0) {
            commentsEnded = false;
            int startPos = this.list.size() - 1;
            this.list.remove(this.list.size() - 1);
            isLoading = false;
            this.list.addAll(list);
            this.list.add(null);
            notifyItemRangeInserted(startPos, list.size());
        } else {
            isLoading = false;
            commentsEnded = true;
            this.list.remove(this.list.size() - 1);
            notifyItemRemoved(this.list.size() - 1);
        }
    }

    public List<CommentEntity> getList() {
        return list;
    }

    public interface ICommentInteraction {
        void loadCommentsPartial(int offset);

        void loadCommentsBase();

        void answerComment(CommentEntity comment, int commentId);

        void openAnswersForComment(CommentEntity commentEntity);

    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sdvItemCommentsUserPhoto)
        SimpleDraweeView sdvUserPhoto;
        @BindView(R.id.tvItemCommentUsername)
        TextView tvUsername;
        @BindView(R.id.tvItemCommentText)
        TextView tvText;
        @BindView(R.id.tvItemCommentDateStamp)
        TextView tvDateStamp;
        @BindView(R.id.ivLikeAchievementLevel)
        ImageView ivLikeLevel;
        @BindView(R.id.ivDislikeAchievementLevel)
        ImageView ivDislikeLevel;
        @BindView(R.id.ivCommentsAchievementLevel)
        ImageView ivCommentsLevel;
        @BindView(R.id.ivFavouritesAchievementLevel)
        ImageView ivFavouritesLevel;
        @BindView(R.id.ivViewsAchievementLevel)
        ImageView ivViewsLevel;
        @BindView(R.id.ivFirst)
        ImageView ivFirst;
        @BindView(R.id.btnRespond)
        Button btnAnswer;
        @BindView(R.id.tvAnswersCount)
        TextView tvAnswersCount;
        @BindView(R.id.expandResponds)
        ImageView ivAnswers;

        CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(CommentEntity comment) {
            tvUsername.setText(comment.getUsername());
            tvText.setText(comment.getText());
            sdvUserPhoto.setImageURI(IConstants.BASE_URL + "/feed/userPhoto?targetUsername=" + comment.getUsername());
            sdvUserPhoto.setLegacyVisibilityHandlingEnabled(true);
            final String monthDay = comment.getDateOfPost().substring(
                    comment.getDateOfPost().indexOf('T') - 5, comment.getDateOfPost().indexOf('T'));
            final String hourMinute = comment.getDateOfPost().substring(
                    comment.getDateOfPost().indexOf('T') + 1, comment.getDateOfPost().indexOf('T') + 6);
            tvDateStamp.setText(hourMinute + " " + monthDay);
        }

    }

    static class FeedLoadingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pbItemFeedLoading)
        ProgressBar pbLoading;

        FeedLoadingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            pbLoading.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        }
    }

    static class FeedFailedToLoadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btnItemFeedRetry)
        Button btnRetry;

        FeedFailedToLoadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
