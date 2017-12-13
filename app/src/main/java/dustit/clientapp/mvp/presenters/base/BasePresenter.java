package dustit.clientapp.mvp.presenters.base;


import dustit.clientapp.mvp.ui.interfaces.IView;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by shevc on 15.09.2017.
 * Let's GO!
 */

public abstract class BasePresenter<V extends IView> {
    private V v;
    private CompositeSubscription compositeSubscription;

    public BasePresenter() {
        compositeSubscription = new CompositeSubscription();
    }

    public V getView() {
        return v;
    }

    public void bind(V v) {
        this.v = v;
    }

    protected void addSubscription(Subscription subscription) {
        compositeSubscription.add(subscription);
    }

    public void unbind() {
        v = null;
        compositeSubscription.unsubscribe();
        compositeSubscription.clear();
    }
}
