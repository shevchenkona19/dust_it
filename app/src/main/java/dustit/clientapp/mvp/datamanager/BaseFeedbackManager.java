package dustit.clientapp.mvp.datamanager;

import dustit.clientapp.mvp.ui.interfaces.IView;

public abstract class BaseFeedbackManager<t extends IView> {
    private t T;

    public t getView() {
        return T;
    }

    public void bind(t T) {
        this.T = T;
    }

    public void unbind() {
        this.T = null;
    }
}
