package dustit.clientapp.mvp.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.ReferralInfoEntity;
import dustit.clientapp.mvp.presenters.activities.ReferralActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IReferralActivityView;
import dustit.clientapp.utils.AlertBuilder;

public class ReferralFragment extends Fragment implements IReferralActivityView {

    @BindView(R.id.tvRefCode)
    TextView tvRefCode;
    @BindView(R.id.ivArrowDown)
    View ivScrollDown;
    @BindView(R.id.ivShareCodeBtn)
    View shareCode;
    @BindView(R.id.svRefCodeWrapper)
    NestedScrollView svCodeWrapper;

    String myCode = "";

    ReferralActivityPresenter presenter = new ReferralActivityPresenter();

    public static ReferralFragment newInstance() {
        return new ReferralFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.bind(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_referral, container, false);
        ButterKnife.bind(this, view);
        presenter.getMyReferralInfo();
        ivScrollDown.setOnClickListener(v -> svCodeWrapper.fling(3000));
        shareCode.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.referral_share_subject) + " " + myCode);
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_referral)));
        });
        return view;
    }

    @Override
    public void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }

    @Override
    public void onReferralLoadFailed() {
        if (getContext() != null)
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReferralInfoLoaded(ReferralInfoEntity referralInfoEntity) {
        myCode = referralInfoEntity.getMyCode();
        tvRefCode.setText(myCode);
    }

    @Override
    public void onNotRegistered() {
        if (getContext() != null)
            AlertBuilder.showNotRegisteredPrompt(getContext());
    }
}
