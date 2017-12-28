package dustit.clientapp.di.component;

import javax.inject.Singleton;

import dagger.Component;
import dustit.clientapp.di.modules.AppModule;
import dustit.clientapp.di.modules.ServerModule;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.repositories.ServerRepository;
import dustit.clientapp.mvp.model.repositories.SharedPreferencesRepository;
import dustit.clientapp.mvp.presenters.activities.AccountActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.ChooserActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.FavoriteViewActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.FavoritesActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.FeedActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.LoginActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.MemViewPresenter;
import dustit.clientapp.mvp.presenters.activities.RegisterActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.ResultActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.SettingsActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.TestActivityPresenter;
import dustit.clientapp.mvp.presenters.fragments.CategoriesFragmentPresenter;
import dustit.clientapp.mvp.presenters.fragments.ChangeCategoriesFragmentPresenter;
import dustit.clientapp.mvp.presenters.fragments.FeedFragmentPresenter;
import dustit.clientapp.mvp.presenters.fragments.HotFragmentPresenter;
import dustit.clientapp.mvp.ui.activities.MemViewActivity;
import dustit.clientapp.mvp.ui.adapters.CommentsRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.adapters.FeedRecyclerViewAdapter;
import dustit.clientapp.utils.FavoritesUtils;

/**
 * Created by shevc on 22.09.2017.
 * Let's GO!
 */
@Component(modules = {AppModule.class, ServerModule.class})
@Singleton
public interface AppComponent {
    void inject(ServerRepository repository);

    void inject(DataManager dataManager);

    void inject(LoginActivityPresenter presenter);

    void inject(RegisterActivityPresenter presenter);

    void inject(FeedFragmentPresenter feedFragmentPresenter);

    void inject(FeedActivityPresenter feedActivityPresenter);

    void inject(SharedPreferencesRepository sharedPreferencesRepository);

    void inject(FeedRecyclerViewAdapter feedRecyclerViewAdapter);

    void inject(MemViewActivity memViewActivity);

    void inject(ChooserActivityPresenter chooserActivityPresenter);

    void inject(SettingsActivityPresenter settingsActivityPresenter);

    void inject(HotFragmentPresenter hotFragmentPresenter);

    void inject(CategoriesFragmentPresenter categoriesFragmentPresenter);

    void inject(TestActivityPresenter testActivityPresenter);

    void inject(ResultActivityPresenter resultActivityPresenter);

    void inject(AccountActivityPresenter accountActivityPresenter);

    void inject(CommentsRecyclerViewAdapter commentsRecyclerViewAdapter);

    void inject(MemViewPresenter memViewPresenter);

    void inject(FavoritesActivityPresenter favoritesActivityPresenter);

    void inject(FavoritesUtils favoritesUtils);

    void inject(FavoriteViewActivityPresenter favoriteViewActivityPresenter);

    void inject(ChangeCategoriesFragmentPresenter changeCategoriesFragmentPresenter);
}