package dustit.clientapp.mvp.presenters.base;


import dustit.clientapp.mvp.ui.interfaces.IView;

/**
 * Created by shevc on 15.09.2017.
 * Let's GO!
 */

public abstract class BasePresenter<V extends IView> {
    private V v;

    public V getView() {
        return v;
    }

    public void bind(V v) {
        this.v = v;
    }

    public void unbind() {
        v = null;
    }
}
