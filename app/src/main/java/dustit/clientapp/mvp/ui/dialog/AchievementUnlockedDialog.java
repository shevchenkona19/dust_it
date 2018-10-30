package dustit.clientapp.mvp.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.NewAchievementEntity;
import dustit.clientapp.utils.AchievementHelper;

public class AchievementUnlockedDialog {
    private Dialog dialog;
    private Resources res;

    @BindView(R.id.tvHeader)
    TextView tvHeader;
    @BindView(R.id.ivDialogAchievementIcon)
    ImageView ivIcon;
    @BindView(R.id.tvAchievementName)
    TextView tvAchievementName;
    @BindView(R.id.tvAchievementNextPrice)
    TextView tvNextPrice;
    @BindView(R.id.btnOkay)
    Button btnOkay;


    public AchievementUnlockedDialog(Context context, boolean isFinalLevel) {
        dialog = new Dialog(context);
        this.res = context.getResources();
        if (isFinalLevel) {
            dialog.setContentView(R.layout.dialog_final_achievement_unlock);
        } else {
            dialog.setContentView(R.layout.dialog_new_achievement);
        }
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        ButterKnife.bind(this, dialog);
    }

    public Dialog bind(NewAchievementEntity achievement) {
        if (!achievement.isFinalLevel()) {
            tvNextPrice.setText(String.format(res.getString(R.string.next_target_is), achievement.getNextPrice(), achievement.getName()));
        }
        ivIcon.setImageResource(AchievementHelper.resolveAchievementIcon(achievement.getName(), achievement.getNewLevel()));
        tvAchievementName.setText(achievement.getAchievementName());
        btnOkay.setOnClickListener(v -> dialog.dismiss());
        return dialog;
    }

}
