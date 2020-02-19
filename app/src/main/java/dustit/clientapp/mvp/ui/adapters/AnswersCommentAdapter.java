package dustit.clientapp.mvp.ui.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.CommentEntity;
import dustit.clientapp.mvp.ui.activities.AccountActivity;
import dustit.clientapp.utils.AchievementHelper;
import dustit.clientapp.utils.GlideApp;
import dustit.clientapp.utils.GlideRequests;
import dustit.clientapp.utils.IConstants;

public class AnswersCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private List<CommentEntity> list;
    private CommentEntity baseComment;

    private boolean isLoading = true;
    private int myId;
    private IAnswersInteraction interaction;
    private GlideRequests glide;

    public AnswersCommentAdapter(Context context, IAnswersInteraction interaction, CommentEntity baseComment, int myId) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.interaction = interaction;
        list = new ArrayList<>();
        list.add(null);
        list.add(null);
        this.myId = myId;
        glide = GlideApp.with(context);
        this.baseComment = baseComment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ViewTypes.BASE_COMMENT:
                return new BaseComment(inflater.inflate(R.layout.item_answers_base_comment, parent, false));
            case ViewTypes.ANSWER:
                return new Answer(inflater.inflate(R.layout.item_answers_comment, parent, false));
            default:
                return new Loading(inflater.inflate(R.layout.item_loading, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BaseComment) {
            BaseComment baseComment = (BaseComment) holder;
            baseComment.bind(this.baseComment);
            baseComment.btnAnswer.setOnClickListener((v) -> interaction.onAnswerClicked(this.baseComment));
            baseComment.sdvUser.setOnClickListener((v) -> {
                int userId = this.baseComment.getUserId();
                Intent intent = new Intent(context, AccountActivity.class);
                intent.putExtra(IConstants.IBundle.IS_ME, userId == myId);
                intent.putExtra(IConstants.IBundle.USER_ID, userId);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, baseComment.sdvUser, context.getString(R.string.account_photo_transition));
                context.startActivity(intent, options.toBundle());
            });
            glide.load(AchievementHelper.resolveAchievementSmallIcon("likes", this.baseComment.getLikeAchievementLvl())).into(baseComment.ivLikeLevel);
            glide.load(AchievementHelper.resolveAchievementSmallIcon("dislikes", this.baseComment.getDislikesAchievementLvl())).into(baseComment.ivDislikeLevel);
            glide.load(AchievementHelper.resolveAchievementSmallIcon("comments", this.baseComment.getCommentsAchievementLvl())).into(baseComment.ivCommentsLevel);
            glide.load(AchievementHelper.resolveAchievementSmallIcon("favourites", this.baseComment.getFavouritesAchievementLvl())).into(baseComment.ivFavouritesLevel);
            glide.load(AchievementHelper.resolveAchievementSmallIcon("views", this.baseComment.getViewsAchievementLvl())).into(baseComment.ivViewsLevel);
            int firstWhat = 0;
            if (this.baseComment.getFirstHundred()) {
                firstWhat = R.drawable.ic_achievement_first100_small;
                glide.load(R.drawable.ic_achievement_first100_small).into(baseComment.ivFirst);
            } else if (this.baseComment.getFirstThousand()) {
                firstWhat = R.drawable.ic_achievement_first1000_small;
            }
            glide.load(firstWhat).into(baseComment.ivFirst);
        } else if (holder instanceof Answer) {
            Answer answer = (Answer) holder;
            CommentEntity comment = list.get(position);
            answer.bind(comment);
            answer.btnAnswer.setOnClickListener((v) -> interaction.onAnswerClicked(comment));
            glide.load(AchievementHelper.resolveAchievementSmallIcon("likes", this.baseComment.getLikeAchievementLvl())).into(answer.ivLikeLevel);
            glide.load(AchievementHelper.resolveAchievementSmallIcon("dislikes", this.baseComment.getDislikesAchievementLvl())).into(answer.ivDislikeLevel);
            glide.load(AchievementHelper.resolveAchievementSmallIcon("comments", this.baseComment.getCommentsAchievementLvl())).into(answer.ivCommentsLevel);
            glide.load(AchievementHelper.resolveAchievementSmallIcon("favourites", this.baseComment.getFavouritesAchievementLvl())).into(answer.ivFavouritesLevel);
            glide.load(AchievementHelper.resolveAchievementSmallIcon("views", this.baseComment.getViewsAchievementLvl())).into(answer.ivViewsLevel);
            answer.sdvUser.setOnClickListener((v) -> {
                int userId = comment.getUserId();
                Intent intent = new Intent(context, AccountActivity.class);
                intent.putExtra(IConstants.IBundle.IS_ME, userId == myId);
                if (userId != myId) {
                    intent.putExtra(IConstants.IBundle.USER_ID, comment.getUserId());
                }
                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) context).toBundle());
            });
        } else {
            if (!isLoading) {
                interaction.onLoadMore(list.size() - 2);
                isLoading = true;
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ViewTypes.BASE_COMMENT;
        } else if (list.get(position) != null) {
            return ViewTypes.ANSWER;
        } else {
            return ViewTypes.LOADING;
        }
    }

    @Override
    public long getItemId(int position) {
        if (position == 0) {
            return -1;
        } else if (list.get(position) != null) {
            return list.get(position).getId();
        } else {
            return -2;
        }
    }

    public void onBaseUpdate(List<CommentEntity> list) {
        this.list.clear();
        this.list.add(null);
        this.list.addAll(list);
        this.list.add(null);
        isLoading = false;
        notifyDataSetChanged();
    }

    public void onPartialUpdate(List<CommentEntity> list) {
        int startPos = this.list.size() - 1;
        this.list.remove(this.list.size() - 1);
        isLoading = false;
        if (list.size() > 0) {
            this.list.addAll(list);
            this.list.add(null);
            notifyItemRangeInserted(startPos, list.size() + 1);
        } else {
            notifyItemChanged(this.list.size() - 1);
        }
    }

    private int resolveAchievementIcon(String name, int lvl) {
        switch (name) {
            case "likes":
                switch (lvl) {
                    case 1:
                        return R.drawable.ic_achievement_like_1_small;
                    case 2:
                        return R.drawable.ic_achievement_like_2_small;
                    case 3:
                        return R.drawable.ic_achievement_like_3_small;
                    case 4:
                        return R.drawable.ic_achievement_like_4_small;
                    case 5:
                        return R.drawable.ic_achievement_like_5_small;
                    case 6:
                        return R.drawable.ic_achievement_like_6_small;
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

    private interface ViewTypes {
        int BASE_COMMENT = 1;
        int ANSWER = 2;
        int LOADING = 3;
    }

    public interface IAnswersInteraction {
        void onAnswerClicked(CommentEntity comment);

        void onLoadMore(int offset);
    }

    static class BaseComment extends RecyclerView.ViewHolder {
        @BindView(R.id.sdvItemCommentsUserPhoto)
        SimpleDraweeView sdvUser;
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

        public BaseComment(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(CommentEntity baseComment) {
            sdvUser.setImageURI(IConstants.BASE_URL + "/feed/userPhoto?targetUsername=" + baseComment.getUsername());
            tvUsername.setText(baseComment.getUsername());
            tvText.setText(baseComment.getText());
            sdvUser.setLegacyVisibilityHandlingEnabled(true);
            final String monthDay = baseComment.getDateOfPost().substring(
                    baseComment.getDateOfPost().indexOf('T') - 5, baseComment.getDateOfPost().indexOf('T'));
            final String hourMinute = baseComment.getDateOfPost().substring(
                    baseComment.getDateOfPost().indexOf('T') + 1, baseComment.getDateOfPost().indexOf('T') + 6);
            tvDateStamp.setText(hourMinute + " " + monthDay);
        }
    }

    static class Answer extends RecyclerView.ViewHolder {
        @BindView(R.id.sdvItemCommentsUserPhoto)
        SimpleDraweeView sdvUser;
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

        public Answer(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(CommentEntity comment) {
            sdvUser.setImageURI(IConstants.BASE_URL + "/feed/userPhoto?targetUsername=" + comment.getUsername());
            tvUsername.setText(comment.getUsername());
            tvText.setText(comment.getText());
            sdvUser.setLegacyVisibilityHandlingEnabled(true);
            final String monthDay = comment.getDateOfPost().substring(
                    comment.getDateOfPost().indexOf('T') - 5, comment.getDateOfPost().indexOf('T'));
            final String hourMinute = comment.getDateOfPost().substring(
                    comment.getDateOfPost().indexOf('T') + 1, comment.getDateOfPost().indexOf('T') + 6);
            tvDateStamp.setText(hourMinute + " " + monthDay);
        }
    }

    static class Loading extends RecyclerView.ViewHolder {

        public Loading(View itemView) {
            super(itemView);
        }
    }
}
