package dustit.clientapp.di.component;

import javax.inject.Singleton;

import dagger.Component;
import dustit.clientapp.App;
import dustit.clientapp.di.modules.AppModule;
import dustit.clientapp.di.modules.ServerModule;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.FeedbackManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.repositories.ServerRepository;
import dustit.clientapp.mvp.model.repositories.SharedPreferencesRepository;
import dustit.clientapp.mvp.presenters.activities.AnswersActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.ChooserActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.FeedActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.LoginActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.MemViewPresenter;
import dustit.clientapp.mvp.presenters.activities.NewAccountActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.ReferralActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.RegisterActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.ResultActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.SettingsActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.TestActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.UploadActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.UserFeedbackPresenter;
import dustit.clientapp.mvp.presenters.fragments.CategoriesFragmentPresenter;
import dustit.clientapp.mvp.presenters.fragments.CategoriesStepFragmentPresenter;
import dustit.clientapp.mvp.presenters.fragments.ChangeCategoriesFragmentPresenter;
import dustit.clientapp.mvp.presenters.fragments.FeedFragmentPresenter;
import dustit.clientapp.mvp.presenters.fragments.HotFragmentPresenter;
import dustit.clientapp.mvp.presenters.fragments.UserFavouritesListFragmentPresenter;
import dustit.clientapp.mvp.presenters.fragments.UserPhotoListFragmentPresenter;
import dustit.clientapp.mvp.ui.activities.AccountActivity;
import dustit.clientapp.mvp.ui.activities.ChooserActivity;
import dustit.clientapp.mvp.ui.activities.NewFeedActivity;
import dustit.clientapp.mvp.ui.activities.PersonalSettingsActivity;
import dustit.clientapp.mvp.ui.activities.SettingsActivity;
import dustit.clientapp.mvp.ui.activities.SplashActivity;
import dustit.clientapp.mvp.ui.adapters.CommentsRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.adapters.FeedRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.base.BaseFeedFragment;
import dustit.clientapp.mvp.ui.fragments.CategoriesFragment;
import dustit.clientapp.mvp.ui.fragments.FeedFragment;
import dustit.clientapp.mvp.ui.fragments.MemViewFragment;
import dustit.clientapp.utils.managers.ReviewManager;
import dustit.clientapp.utils.receivers.NotificationService;

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

    void inject(ChooserActivityPresenter chooserActivityPresenter);

    void inject(SettingsActivityPresenter settingsActivityPresenter);

    void inject(HotFragmentPresenter hotFragmentPresenter);

    void inject(CategoriesFragmentPresenter categoriesFragmentPresenter);

    void inject(TestActivityPresenter testActivityPresenter);

    void inject(ResultActivityPresenter resultActivityPresenter);

    void inject(CommentsRecyclerViewAdapter commentsRecyclerViewAdapter);

    void inject(MemViewPresenter memViewPresenter);



    void inject(ChangeCategoriesFragmentPresenter changeCategoriesFragmentPresenter);

    void inject(UserSettingsDataManager userSettingsDataManager);

    void inject(ChooserActivity chooserActivity);

    void inject(SettingsActivity settingsActivity);

    void inject(App app);

    void inject(PersonalSettingsActivity personalSettingsActivity);

    void inject(CategoriesFragment categoriesFragment);

    void inject(MemViewFragment memViewFragment);


    void inject(FeedbackManager feedbackManager);

    void inject(FeedFragment feedFragment);

    void inject(BaseFeedFragment baseFeedFragment);

    void inject(UserFeedbackPresenter userFeedbackPresenter);

    void inject(ReviewManager reviewManager);

    void inject(NewAccountActivityPresenter newAccountActivityPresenter);

    void inject(SplashActivity splashActivity);

    void inject(NotificationService notificationService);

    void inject(NewFeedActivity newFeedActivity);

    void inject(AnswersActivityPresenter answersActivityPresenter);

    void inject(ReferralActivityPresenter referralActivityPresenter);

    void inject(AccountActivity accountActivity);

    void inject(UserPhotoListFragmentPresenter userPhotoListFragmentPresenter);

    void inject(CategoriesStepFragmentPresenter categoriesStepFragmentPresenter);

    void inject(UploadActivityPresenter uploadActivityPresenter);

    void inject(UserFavouritesListFragmentPresenter userFavouritesListFragmentPresenter);

}
