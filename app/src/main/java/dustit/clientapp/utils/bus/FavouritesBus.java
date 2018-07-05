package dustit.clientapp.utils.bus;

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

    private IConsumer mainConsumer;
    private IConsumer additionalConsumer;

    public void removed(String id) {
        if (mainConsumer != null)
            mainConsumer.consumeRemoved(id);
        if (additionalConsumer != null)
            additionalConsumer.consumeRemoved(id);
    }

    public void added(String id) {
        if (mainConsumer != null)
            mainConsumer.consumeAdded(id);
        if (additionalConsumer != null)
            additionalConsumer.consumeAdded(id);
    }

    public void setMainConsumer(IConsumer mainConsumer) {
        this.mainConsumer = mainConsumer;
    }

    public void setAdditionalConsumer(IConsumer additionalConsumer) {
        this.additionalConsumer = additionalConsumer;
    }

    public static void destroy() {
        favouritesBus = null;
    }
}
