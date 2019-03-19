package dustit.clientapp.mvp.presenters.activities;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.ReferralInfoEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IReferralActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IReferralActivityView;
import rx.Subscriber;

public class ReferralActivityPresenter extends BasePresenter<IReferralActivityView> implements IReferralActivityPresenter {
    @Inject
    DataManager dataManager;

    public ReferralActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void getMyReferralInfo() {
        AtomicReference<ReferralInfoEntity> reference = new AtomicReference<>();
        addSubscription(dataManager.getMyReferralInfo().subscribe(new Subscriber<ReferralInfoEntity>() {
            @Override
            public void onCompleted() {
                getView().onReferralInfoLoaded(reference.get());
            }

            @Override
            public void onError(Throwable e) {
                getView().onReferralLoadFailed();
            }

            @Override
            public void onNext(ReferralInfoEntity referralInfoEntity) {
                reference.set(referralInfoEntity);
            }
        }));
    }
}
