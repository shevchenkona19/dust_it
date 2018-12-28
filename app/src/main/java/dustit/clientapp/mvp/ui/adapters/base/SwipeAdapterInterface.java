package dustit.clientapp.mvp.ui.adapters.base;

public interface SwipeAdapterInterface {
    int getSwipeLayoutResourceId(int position);

    void notifyDatasetChanged();
}
