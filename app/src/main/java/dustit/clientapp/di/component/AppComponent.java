package dustit.clientapp.di.component;

import javax.inject.Singleton;

import dagger.Component;
import dustit.clientapp.di.modules.AppModule;
import dustit.clientapp.di.modules.ServerModule;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.repositories.ServerRepository;
import dustit.clientapp.mvp.presenters.activities.LoginActivityPresenter;
import dustit.clientapp.mvp.presenters.activities.RegisterActivityPresenter;

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
}
