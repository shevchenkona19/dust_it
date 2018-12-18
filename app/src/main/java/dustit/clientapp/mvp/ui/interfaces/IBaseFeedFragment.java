package dustit.clientapp.mvp.ui.interfaces;

public interface IBaseFeedFragment extends IFragmentView {
    void onAddedToFavourites(int position);
    void onRemovedFromFavourites(int position);

    void onErrorRemovingFromFavorites();

    void onErrorAddingToFavorites();
}
