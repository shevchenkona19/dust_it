package dustit.clientapp.mvp.ui.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.CommentEntity;
import dustit.clientapp.mvp.ui.activities.NewAccountActivity;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.L;

public class AnswersCommentRecyclerViewAdapter extends RecyclerView.Adapter<AnswersCommentRecyclerViewAdapter.AnswerViewHolder> {
    private List<CommentEntity> list;
    private LayoutInflater inflater;
    private Context context;
    private String myId;
    private ICommentInteraction commentInteraction;
    private String parentCommentId;
    public interface ICommentInteraction {
        void answerComment(CommentEntity commentEntity, String parentCommentId);
    }

    public AnswersCommentRecyclerViewAdapter(Context context, String myId, ICommentInteraction listener, String id) {
        list = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        commentInteraction = listener;
        this.myId = myId;
        this.context = context;
        parentCommentId = id;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_comments_comment, parent, false);
        return new AnswerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        CommentEntity comment = list.get(position);
        holder.bind(comment);
        holder.sdvUserPhoto.setOnClickListener((v) -> {
            String userId = comment.getUserId();
            Intent intent = new Intent(context, NewAccountActivity.class);
            intent.putExtra(IConstants.IBundle.IS_ME, userId.equals(myId));
            if (!userId.equals(myId)) {
                intent.putExtra(IConstants.IBundle.ID, comment.getUserId());
            }
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, holder.sdvUserPhoto, context.getString(R.string.account_photo_transition));
            context.startActivity(intent, options.toBundle());
        });
        holder.ivLikeLevel.setImageResource(resolveAchievementIcon("likes", comment.getLikeAchievementLvl()));
        holder.ivDislikeLevel.setImageResource(resolveAchievementIcon("dislikes", comment.getDislikesAchievementLvl()));
        holder.ivCommentsLevel.setImageResource(resolveAchievementIcon("comments", comment.getCommentsAchievementLvl()));
        holder.ivFavouritesLevel.setImageResource(resolveAchievementIcon("favourites", comment.getFavouritesAchievementLvl()));
        holder.ivViewsLevel.setImageResource(resolveAchievementIcon("views", comment.getViewsAchievementLvl()));
        holder.btnAnswer.setOnClickListener(v -> commentInteraction.answerComment(comment, parentCommentId));
        if (comment.getFirstHundred()) {
            holder.ivFirst.setImageResource(R.drawable.ic_achievement_first100_small);
        } else if (comment.getFirstThousand()) {
            holder.ivFirst.setImageResource(R.drawable.ic_achievement_first1000_small);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
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

    public void updateList(List<CommentEntity> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }


    static class AnswerViewHolder extends RecyclerView.ViewHolder {
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
        @BindView(R.id.rvAnswersList)
        RecyclerView rvAnswers;

        public AnswerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind(CommentEntity comment) {
            rvAnswers.setVisibility(View.GONE);
            tvAnswersCount.setVisibility(View.GONE);
            ivAnswers.setVisibility(View.GONE);
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
}
