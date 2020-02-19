package dustit.clientapp.mvp.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.Achievement;

public class AchievementsViewAdapter extends RecyclerView.Adapter<AchievementsViewAdapter.AchievementViewHolder> {
    private List<Integer> icons;
    private Achievement achievement;
    private LayoutInflater inflater;

    public AchievementsViewAdapter(Context context, List<Integer> icons, Achievement achievement) {
        this.icons = icons;
        this.achievement = achievement;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AchievementViewHolder(inflater.inflate(R.layout.item_achievement_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        holder.ivAchievementIcon.setImageResource(icons.get(position));
        if (position > achievement.getLvl() - 1) {
            holder.ivAchievementIcon.setColorFilter(Color.rgb(50, 50, 50));
        }
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    static class AchievementViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivAchievementView)
        ImageView ivAchievementIcon;

        public AchievementViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
