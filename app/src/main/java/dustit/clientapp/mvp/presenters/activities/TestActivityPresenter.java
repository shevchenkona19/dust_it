package dustit.clientapp.mvp.presenters.activities;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.TestMemEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.ITestActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.ITestActivityView;
import rx.Subscriber;

/**
 * Created by Никита on 09.11.2017.
 */

public class TestActivityPresenter extends BasePresenter<ITestActivityView> implements ITestActivityPresenter {
    @Inject
    DataManager dataManager;

    public TestActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }
    @Override
    public String getToken() {
        return dataManager.getToken();
    }

    @Override
    public void loadTest() {
        final List<TestMemEntity> list = new ArrayList<>();
        addSubscription(dataManager.getTest()
                .subscribe(new Subscriber<TestMemEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onTestArrived(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("MY", e.getMessage());
                        getView().onErrorInLoadingTest();
                    }

                    @Override
                    public void onNext(TestMemEntity entity) {
                        list.add(entity);
                    }
                }));
    }
}
