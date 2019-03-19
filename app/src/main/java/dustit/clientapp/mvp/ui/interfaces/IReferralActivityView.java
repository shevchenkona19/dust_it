package dustit.clientapp.mvp.ui.interfaces;

import dustit.clientapp.mvp.model.entities.ReferralInfoEntity;

public interface IReferralActivityView extends IActivityView {
    void onReferralLoadFailed();
    void onReferralInfoLoaded(ReferralInfoEntity referralInfoEntity);
}
