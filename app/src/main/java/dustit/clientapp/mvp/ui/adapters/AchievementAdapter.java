package dustit.clientapp.mvp.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.Achievement;
import dustit.clientapp.utils.AchievementHelper;

public class AchievementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Achievement> achievements = new ArrayList<>();
    private boolean isHundred;
    private boolean isThousand;
    private LayoutInflater inflater;
    private Context context;

    private static int ACHIEVEMENT = 1;
    private static int HUNDRED = 2;
    private static int THOUSAND = 3;

    public AchievementAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void update(boolean isHundred, boolean isThousand, List<Achievement> items) {
        this.isHundred = isHundred;
        this.isThousand = isThousand;
        achievements.clear();
        achievements.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == HUNDRED || viewType == THOUSAND) {
            viewHolder = new HundredViewHolder(inflater.inflate(R.layout.item_hundred, parent, false));

        } else {
            viewHolder = new AchievementViewHolder(inflater.inflate(R.layout.item_achievement, parent, false));
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            if (isHundred) {
                HundredViewHolder hundredViewHolder = (HundredViewHolder) holder;
                //TODO insert real ic
                hundredViewHolder.ivIcon.setImageResource(0);
                hundredViewHolder.tvSlash.setText(context.getText(R.string.first_hundred));
                return;
            } else if (isThousand) {
                HundredViewHolder hundredViewHolder = (HundredViewHolder) holder;
                //TODO insert real ic
                hundredViewHolder.ivIcon.setImageResource(0);
                hundredViewHolder.tvSlash.setText(context.getText(R.string.first_thousand));
                return;
            }
        }
        int whatToGet = position;
        if (isThousand || isHundred) whatToGet -= 1;
        Achievement achievement = achievements.get(whatToGet);
        AchievementViewHolder achievementViewHolder = (AchievementViewHolder) holder;
        achievementViewHolder.ivIcon.setImageResource(AchievementHelper.resolveAchievementIcon(achievement.getName(), achievement.getLvl()));
        if (!achievement.isFinalLevel()) {
            achievementViewHolder.pbAchievementProgress.setMax(achievement.getNextPrice());
            achievementViewHolder.pbAchievementProgress.setProgress(achievement.getCount());
            achievementViewHolder.tvAchievementCount.setText(String.valueOf(achievement.getCount()));
            achievementViewHolder.tvMax.setText(String.valueOf(achievement.getNextPrice()));
        } else {
            achievementViewHolder.pbAchievementProgress.setMax(1);
            achievementViewHolder.pbAchievementProgress.setProgress(1);
            achievementViewHolder.tvAchievementCount.setVisibility(View.GONE);
            achievementViewHolder.tvMax.setVisibility(View.GONE);
            achievementViewHolder.tvSlash.setText(String.valueOf(achievement.getCount()));
        }
    }

    @Override
    public int getItemCount() {
        int items = 0;
        items += achievements.size();
        if (isHundred) items += 1;
        if (isThousand) items += 1;
        return items;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHundred) {
            if (position == 0) {
                return HUNDRED;
            }
        }
        if (isThousand) {
            if (position == 0) {
                return THOUSAND;
            }
        }
        return ACHIEVEMENT;
    }

    static class AchievementViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivAchievementIcon)
        ImageView ivIcon;
        @BindView(R.id.pbAchievementProgress)
        ProgressBar pbAchievementProgress;
        @BindView(R.id.tvAchievementCount)
        TextView tvAchievementCount;
        @BindView(R.id.tvAchievementSlash)
        TextView tvSlash;
        @BindView(R.id.tvAchievementMaximum)
        TextView tvMax;

        public AchievementViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class HundredViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivAchievementIcon)
        ImageView ivIcon;
        @BindView(R.id.tvAchievementSlash)
        TextView tvSlash;

        public HundredViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
