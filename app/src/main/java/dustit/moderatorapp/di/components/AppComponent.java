package dustit.moderatorapp.di.components;

import javax.inject.Singleton;

import dagger.Component;
import dustit.moderatorapp.di.modules.AppModule;
import dustit.moderatorapp.di.modules.ServerModule;
import dustit.moderatorapp.mvp.datamanager.DataManager;
import dustit.moderatorapp.mvp.model.repositories.ServerRepository;
import dustit.moderatorapp.mvp.presenter.activities.ChooserActivityPresenter;
import dustit.moderatorapp.mvp.presenter.activities.LoginActivityPresenter;
import dustit.moderatorapp.mvp.presenter.activities.RegisterActivityPresenter;

/**
 * Created by shevc on 15.09.2017.
 * Let's GO!
 */
@Component(modules = {AppModule.class, ServerModule.class})
@Singleton
public interface AppComponent {

    void inject(DataManager dataManager);
    void inject(ServerRepository repository);
    void inject(RegisterActivityPresenter presenter);

    void inject(ChooserActivityPresenter chooserActivityPresenter);

    void inject(LoginActivityPresenter loginActivityPresenter);
}
