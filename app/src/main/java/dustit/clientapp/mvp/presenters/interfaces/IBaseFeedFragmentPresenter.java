package dustit.clientapp.mvp.presenters.interfaces;

public interface IBaseFeedFragmentPresenter {
    void addToFavourites(String id, int position);
    void removeFromFavourites(String id, int position);
}
