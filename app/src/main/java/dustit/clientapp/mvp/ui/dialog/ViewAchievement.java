package dustit.clientapp.mvp.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.Achievement;
import dustit.clientapp.mvp.ui.adapters.AchievementsViewAdapter;
import dustit.clientapp.utils.AchievementHelper;
import dustit.clientapp.utils.L;

public class ViewAchievement {
    @BindView(R.id.cvDialog)
    CardView card;
    @BindView(R.id.tvAchievementName)
    TextView tvAchievementName;
    @BindView(R.id.tvAchievementNextPrice)
    TextView tvAchievementNextPrice;
    @BindView(R.id.pbAchievementProgress)
    ProgressBar pbAchievementProgress;
    @BindView(R.id.tvAchievementCount)
    TextView tvAchievementCount;
    @BindView(R.id.btnOkay)
    Button btnOkay;
    @BindView(R.id.rvAchievements)
    DiscreteScrollView dsvAchievements;

    private Dialog dialog;
    private Context context;
    private AchievementsViewAdapter adapter;

    public ViewAchievement(Context context) {
        dialog = new Dialog(context);
        this.context = context;
        dialog.setContentView(R.layout.dialog_view_achievement);
        ButterKnife.bind(this, dialog);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    public Dialog bind(Achievement achievement, boolean isMe) {
        tvAchievementName.setText(achievement.getAchievementName());
        tvAchievementNextPrice.setText(String.format(context.getString(R.string.next_target_is), achievement.getNextPrice(), AchievementHelper.resolveAchievementTargetName(context.getResources(), achievement.getName())));
        pbAchievementProgress.setMax(achievement.getNextPrice());
        pbAchievementProgress.setProgress(achievement.getCount());
        tvAchievementCount.setText(String.format(context.getString(R.string.achievementProgress), achievement.getCount(), achievement.getNextPrice()));
        btnOkay.setOnClickListener(v -> dialog.dismiss());
        List<Integer> icons = AchievementHelper.getAchievementIcons(achievement.getName());
        adapter = new AchievementsViewAdapter(context, icons, achievement);
        dsvAchievements.setAdapter(adapter);
        dsvAchievements.setOffscreenItems(2);
        dsvAchievements.setOverScrollEnabled(true);
        dsvAchievements.setSlideOnFling(true);
        dsvAchievements.setItemTransformer(new ScaleTransformer.Builder()
                .setMaxScale(1.2f)
                .setMinScale(0.8f)
                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.CENTER) // CENTER is a default one
                .build());
        dsvAchievements.addOnItemChangedListener((viewHolder, adapterPosition) -> {
            if (achievement.getLvl() > 0) {
                if (adapterPosition > achievement.getLvl() - 1) {
                    tvAchievementName.setText("?");
                    tvAchievementNextPrice.setVisibility(View.INVISIBLE);
                    pbAchievementProgress.setVisibility(View.INVISIBLE);
                    tvAchievementCount.setVisibility(View.INVISIBLE);
                } else {
                    tvAchievementName.setText(achievement.getAllAchievementNames().get(adapterPosition + 1));
                    if (isMe) {
                        if (!achievement.isFinalLevel()) {
                            tvAchievementNextPrice.setVisibility(View.VISIBLE);
                            pbAchievementProgress.setVisibility(View.VISIBLE);
                            tvAchievementCount.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
        dsvAchievements.scrollToPosition(achievement.getLvl() - 1);
        ((View) card.getParent()).setOnClickListener(v -> dialog.dismiss());
        if (!isMe || achievement.isFinalLevel()) {
            pbAchievementProgress.setVisibility(View.INVISIBLE);
            tvAchievementCount.setVisibility(View.INVISIBLE);
            tvAchievementNextPrice.setVisibility(View.INVISIBLE);
        }
        return dialog;
    }
}
