package dustit.clientapp.mvp.presenters.fragments;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IFeedFragmentPresenter;
import dustit.clientapp.mvp.ui.interfaces.IFeedFragmentView;
import rx.Subscriber;

/**
 * Created by shevc on 05.10.2017.
 * Let's GO!
 */

public class FeedFragmentPresenter extends BasePresenter<IFeedFragmentView> implements IFeedFragmentPresenter {
    @Inject
    DataManager dataManager;

    public FeedFragmentPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void loadBase() {
        getView().onStartLoading();
        final List<MemEntity> list = new ArrayList<>();
        dataManager.getFeed(20, 0)
                .subscribe(new Subscriber<MemEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onBaseUpdated(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("MY", "Error in loading base: " + e.getMessage());
                        getView().onErrorInLoading();
                    }

                    @Override
                    public void onNext(MemEntity memEntity) {
                        list.add(memEntity);
                    }
                });
    }

    @Override
    public void loadWithOffset(int offset) {
        getView().onStartLoading();
        final List<MemEntity> list = new ArrayList<>();
        dataManager.getFeed(20, offset)
                .subscribe(new Subscriber<MemEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onPartialUpdate(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("MY", "Error in loading partial: " + e.getMessage());
                        getView().onErrorInLoading();
                    }

                    @Override
                    public void onNext(MemEntity memEntity) {
                        list.add(memEntity);
                    }
                });
    }

    @Override
    public void postLike(String id) {
        dataManager.postLike(id);
    }
}
