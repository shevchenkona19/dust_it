package dustit.clientapp.mvp.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.Achievement;

public class ViewFirstMenDialog {
    @BindView(R.id.cvDialog)
    CardView card;
    @BindView(R.id.tvAchievementName)
    TextView tvAchievementName;
    @BindView(R.id.ivAchievementIcon)
    ImageView ivIcon;
    @BindView(R.id.tvAchievementDescription)
    TextView tvAchievementDescription;
    @BindView(R.id.btnOkay)
    Button btnOkay;

    private Dialog dialog;
    private Context context;

    private boolean firstHundred;
    private boolean firstThousand;

    public ViewFirstMenDialog(Context context, boolean isHundred, boolean isThousand) {
        dialog = new Dialog(context);
        this.context = context;
        firstHundred = isHundred;
        firstThousand = isThousand;
        dialog.setContentView(R.layout.dialog_first_men);
        ButterKnife.bind(this, dialog);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    public Dialog bind(boolean isMe) {
        if (firstThousand) {
            ivIcon.setImageResource(R.drawable.ic_achievement_first1000_big);
            if (isMe) {
                tvAchievementDescription.setText(context.getText(R.string.first_thousand_description_forme));
            } else {
                tvAchievementDescription.setText(context.getText(R.string.first_thousand_description_forall));
            }
            tvAchievementName.setText(context.getText(R.string.first_thousand));
        }
        if (firstHundred) {
            ivIcon.setImageResource(R.drawable.ic_achievement_first100_big);
            if (isMe) {
                tvAchievementDescription.setText(context.getText(R.string.first_hundred_description_forme));
            } else {
                tvAchievementDescription.setText(context.getText(R.string.first_hundred_description_forall));
            }
            tvAchievementName.setText(context.getText(R.string.first_hundred));
        }
        ((View) card.getParent()).setOnClickListener(v -> dialog.dismiss());
        btnOkay.setOnClickListener(v -> dialog.dismiss());
        return dialog;
    }
}
