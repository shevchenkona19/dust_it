package dustit.clientapp.utils.bus;

import java.lang.ref.WeakReference;

public class FavouritesBus {

    private FavouritesBus() {
    }

    private static FavouritesBus favouritesBus;

    public static FavouritesBus getInstance() {
        if (favouritesBus == null) favouritesBus = new FavouritesBus();
        return favouritesBus;
    }

    public interface IConsumer {
        void consumeRemoved(String id);

        void consumeAdded(String id);
    }

    private WeakReference<IConsumer> mainConsumer;
    private WeakReference<IConsumer> additionalConsumer;

    public void removed(String id) {
        if (mainConsumer != null && mainConsumer.get() != null)
            mainConsumer.get().consumeRemoved(id);
        if (additionalConsumer != null && additionalConsumer.get() != null)
            additionalConsumer.get().consumeRemoved(id);
    }

    public void added(String id) {
        if (mainConsumer != null)
            mainConsumer.get().consumeAdded(id);
        if (additionalConsumer != null)
            additionalConsumer.get().consumeAdded(id);
    }

    public void setMainConsumer(IConsumer mainConsumer) {
        this.mainConsumer = new WeakReference<>(mainConsumer);
    }

    public void setAdditionalConsumer(IConsumer additionalConsumer) {
        this.additionalConsumer = new WeakReference<>(additionalConsumer);
    }

    public static void destroy() {
        favouritesBus = null;
    }
}
