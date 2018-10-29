package dustit.clientapp.mvp.ui.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.CommentEntity;
import dustit.clientapp.mvp.ui.activities.NewAccountActivity;
import dustit.clientapp.utils.IConstants;

/**
 * Created by Никита on 11.11.2017.
 */

public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CommentEntity> list;
    private LayoutInflater inflater;
    private boolean isLoading = false;
    private Context context;
    private String myId;

    private boolean sent = false;
    private int offset = 6;
    private int lastPos;

    @Inject
    DataManager dataManager;

    public CommentsRecyclerViewAdapter(Context context, String myId, ICommentInteraction listener) {
        list = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        interactionListener = listener;
        this.myId = myId;
        this.context = context;
        App.get().getAppComponent().inject(this);
    }

    public interface ICommentInteraction {
        void loadCommentsPartial(int offset);

        void loadCommentsBase();
    }

    private ICommentInteraction interactionListener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View v = inflater.inflate(R.layout.item_comments_comment, parent, false);
                return new CommentViewHolder(v);
            case 1:
                if (isLoading) {
                    View v1 = inflater.inflate(R.layout.item_feed_loading, parent, false);
                    return new FeedRecyclerViewAdapter.LoadingViewHolder(v1);
                } else {
                    View v2 = inflater.inflate(R.layout.item_feed_failed_to_load, parent, false);
                    return new FeedRecyclerViewAdapter.FailedViewHolder(v2);
                }
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        int pos = holder.getAdapterPosition();
        if (pos % 5 == 0 && pos != 0) {
            if (pos > lastPos) {
                if (sent && pos == 5) {
                    sent = false;
                } else {
                    sent = true;
                    interactionListener.loadCommentsPartial(offset);
                    offset += 5;
                    lastPos = pos;
                }
            }
        }
        if (holder instanceof CommentViewHolder) {
            final CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            CommentEntity comment = list.get(pos);
            commentViewHolder.bind(comment);
            commentViewHolder.sdvUserPhoto.setOnClickListener((v) -> {
                String userId = comment.getUserId();
                Intent intent = new Intent(context, NewAccountActivity.class);
                intent.putExtra(IConstants.IBundle.IS_ME, userId.equals(myId));
                if (!userId.equals(myId)) {
                    intent.putExtra(IConstants.IBundle.ID, comment.getUserId());
                }
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, commentViewHolder.sdvUserPhoto, context.getString(R.string.account_photo_transition));
                context.startActivity(intent, options.toBundle());
            });
            commentViewHolder.ivLikeLevel.setImageResource(resolveAchievementIcon("likes", comment.getLikeAchievementLvl()));
            commentViewHolder.ivDislikeLevel.setImageResource(resolveAchievementIcon("dislikes", comment.getDislikesAchievementLvl()));
            commentViewHolder.ivCommentsLevel.setImageResource(resolveAchievementIcon("comments", comment.getCommentsAchievementLvl()));
            commentViewHolder.ivFavouritesLevel.setImageResource(resolveAchievementIcon("favourites", comment.getFavouritesAchievementLvl()));
            commentViewHolder.ivViewsLevel.setImageResource(resolveAchievementIcon("views", comment.getViewsAchievementLvl()));

//            commentViewHolder.ivFirst.setImageResource();

            final String monthDay = comment.getDateOfPost().substring(
                    comment.getDateOfPost().indexOf('T') - 5, comment.getDateOfPost().indexOf('T'));
            final String hourMinute = comment.getDateOfPost().substring(
                    comment.getDateOfPost().indexOf('T') + 1, comment.getDateOfPost().indexOf('T') + 6);
            commentViewHolder.tvDateStamp.setText(hourMinute + " " + monthDay);
        } else if (holder instanceof CommentsRecyclerViewAdapter.FeedFailedToLoadViewHolder) {
            final CommentsRecyclerViewAdapter.FeedFailedToLoadViewHolder failedToLoadViewHolder = (CommentsRecyclerViewAdapter.FeedFailedToLoadViewHolder) holder;
            failedToLoadViewHolder.btnRetry.setOnClickListener(view -> {
                if (list.size() > 1) {
                    interactionListener.loadCommentsPartial(list.size());
                } else {
                    interactionListener.loadCommentsBase();
                }
            });
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
        if (isLoading) {
            isLoading = false;
        }
        this.list.clear();
        this.list.addAll(list);
        lastPos = 0;
        offset = 6;
        notifyDataSetChanged();
    }

    public void updateListAtEnding(List<CommentEntity> list) {
        if (isLoading) {
            isLoading = false;
            int lastPos = this.list.size() - 1;
            this.list.remove(null);
            notifyItemChanged(lastPos);
        }
        int currPos = this.list.size() - 1;
        this.list.addAll(list);
        notifyItemRangeInserted(currPos, list.size());
    }

    public List<CommentEntity> getList() {
        return list;
    }

    private int resolveAchievementIcon(String name, int lvl) {
        switch (name) {
            case "likes":
                switch (lvl) {
                    case 1:
                        return R.drawable.ic_achievement_like_1_small;
                    case 2:
                        return R.drawable.ic_achievement_comment_2_small;
                    case 3:
                        return R.drawable.ic_achievement_comment_3_small;
                    case 4:
                        return R.drawable.ic_achievement_comment_4_small;
                    case 5:
                        return R.drawable.ic_achievement_comment_5_small;
                    case 6:
                        return R.drawable.ic_achievement_comment_6_small;
                }
            case "dislikes":
                switch (lvl) {
                    case 1:
                        return R.drawable.ic_achievement_dislike_1_small;
                    case 2:
                        return R.drawable.ic_achievement_dislike_2_small;
                    case 3:
                        return R.drawable.ic_achievement_dislike_3_small;
                    case 4:
                        return R.drawable.ic_achievement_dislike_4_small;
                    case 5:
                        return R.drawable.ic_achievement_dislike_5_small;
                    case 6:
                        return R.drawable.ic_achievement_dislike_6_small;
                }
            case "comments":
                switch (lvl) {
                    case 1:
                        return R.drawable.ic_achievement_comment_1_small;
                    case 2:
                        return R.drawable.ic_achievement_comment_2_small;
                    case 3:
                        return R.drawable.ic_achievement_comment_3_small;
                    case 4:
                        return R.drawable.ic_achievement_comment_4_small;
                    case 5:
                        return R.drawable.ic_achievement_comment_5_small;
                    case 6:
                        return R.drawable.ic_achievement_comment_6_small;
                }
            case "views":
                switch (lvl) {
                    case 1:
                        return R.drawable.ic_achievement_views_1_small;
                    case 2:
                        return R.drawable.ic_achievement_views_2_small;
                    case 3:
                        return R.drawable.ic_achievement_views_3_small;
                    case 4:
                        return R.drawable.ic_achievement_views_4_small;
                    case 5:
                        return R.drawable.ic_achievement_views_5_small;
                    case 6:
                        return R.drawable.ic_achievement_views_6_small;
                    case 7:
                        return R.drawable.ic_achievement_views_7_small;
                    case 8:
                        return R.drawable.ic_achievement_views_8_small;
                }
            case "favourites":
                switch (lvl) {
                    case 1:
                        return R.drawable.ic_achievement_fav_1_small;
                    case 2:
                        return R.drawable.ic_achievement_fav_2_small;
                    case 3:
                        return R.drawable.ic_achievement_fav_3_small;
                    case 4:
                        return R.drawable.ic_achievement_fav_4_small;
                    case 5:
                        return R.drawable.ic_achievement_fav_5_small;
                    case 6:
                        return R.drawable.ic_achievement_fav_6_small;
                }
            default:
                return 0;
        }
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
