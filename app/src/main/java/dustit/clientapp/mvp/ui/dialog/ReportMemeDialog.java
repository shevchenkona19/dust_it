package dustit.clientapp.mvp.ui.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.UploadEntity;
import dustit.clientapp.mvp.presenters.dialogs.ReportMemeDialogPresenter;
import dustit.clientapp.mvp.ui.interfaces.IReportMemeDialogView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.ErrorCodeResolver;
import dustit.clientapp.utils.IConstants;

public class ReportMemeDialog implements IReportMemeDialogView {
    @BindView(R.id.reasons_group)
    RadioGroup reasonsGroup;
    @BindView(R.id.rbAbuseReason)
    RadioButton rbAbuse;
    @BindView(R.id.rbPornReason)
    RadioButton rbPorn;
    @BindView(R.id.rbNotMemeReason)
    RadioButton rbNotMeme;
    @BindView(R.id.rbTerrorReason)
    RadioButton rbTerror;
    @BindView(R.id.rbOwnReason)
    RadioButton rbOwn;
    @BindView(R.id.etOwnReportReason)
    EditText ownReason;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    @BindView(R.id.pbLoading)
    ProgressBar pbLoading;

    private Dialog dialog;
    private Resources res;
    private ReportMemeDialogPresenter presenter;

    private String reason = "";

    public ReportMemeDialog(Context context, MemEntity mem) {
        presenter = new ReportMemeDialogPresenter();
        dialog = new Dialog(context);
        this.res = context.getResources();
        dialog.setContentView(R.layout.dialog_report_meme);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setCancelable(true);
        dialog.setOnDismissListener(dialog1 -> {
            destroy();
        });
        dialog.setOnCancelListener(dialog1 -> {
            destroy();
        });
        dialog.setOnShowListener(dialog1 -> {
            presenter.bind(this);
        });
        ButterKnife.bind(this, dialog);
        reasonsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rbAbuseReason:
                    hideOwnReason();
                    reason = IConstants.ReportReasons.ABUSE;
                    break;
                case R.id.rbPornReason:
                    hideOwnReason();
                    reason = IConstants.ReportReasons.PORN;
                    break;
                case R.id.rbNotMemeReason:
                    hideOwnReason();
                    reason = IConstants.ReportReasons.NOT_MEME;
                    break;
                case R.id.rbTerrorReason:
                    hideOwnReason();
                    reason = IConstants.ReportReasons.TERROR;
                    break;
                case R.id.rbOwnReason:
                    showOwnReason();
                    reason = "";
                    break;
            }
        });
        btnSubmit.setOnClickListener(v -> {
            if (rbOwn.isChecked() && ownReason.getText().length() < 3) {
                showEnterReasonToast();
            } else if (rbOwn.isChecked()) {
                reason = ownReason.getText().toString();
                presenter.reportMeme(reason, mem);
            } else if (!rbOwn.isChecked() && reason.equals("")) {
                showEnterReasonToast();
            } else {
                presenter.reportMeme(reason, mem);
            }
        });
        dialog.show();
    }

    private void showEnterReasonToast() {
        Toast.makeText(dialog.getContext(), res.getText(R.string.enter_reason), Toast.LENGTH_SHORT).show();
    }

    private void hideOwnReason() {
        ownReason.setVisibility(View.GONE);
    }

    private void showOwnReason() {
        ownReason.setVisibility(View.VISIBLE);
    }

    private void destroy() {
        presenter.unbind();
    }

    private void showLoading() {
        reasonsGroup.setVisibility(View.INVISIBLE);
        ownReason.setVisibility(View.INVISIBLE);
        btnSubmit.setVisibility(View.INVISIBLE);
        pbLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        reasonsGroup.setVisibility(View.VISIBLE);
        ownReason.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStartLoading() {
        showLoading();
    }

    @Override
    public void onFailedToReport() {
        if (dialog != null)
            onError(dialog.getContext().getString(R.string.error));
    }

    @Override
    public void onFailedToReport(String reason) {
        if (dialog != null)
            onError(ErrorCodeResolver.resolveError(reason, new WeakReference<>(dialog.getContext())));
    }

    private void onError(String message) {
        if (dialog == null) return;
        Toast.makeText(dialog.getContext(), message, Toast.LENGTH_SHORT).show();
        hideLoading();
    }

    @Override
    public void onReported() {
        if (dialog == null) return;
        hideLoading();
        Context context = dialog.getContext();
        Toast.makeText(context, context.getText(R.string.thankyou_will_check_report), Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void onNotRegistered() {
        if (dialog == null) return;
        AlertBuilder.showNotRegisteredPrompt(dialog.getContext());
    }
}
