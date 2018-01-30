package dustit.clientapp.mvp.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.presenters.activities.ChooserActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IChooserActivityView;

public class ChooserActivity extends AppCompatActivity implements IChooserActivityView {
    @BindView(R.id.btnChooserLogin)
    Button btnLogin;
    @BindView(R.id.btnChooserRegister)
    Button btnRegister;
    @BindView(R.id.ivChooserIcon)
    ImageView ivIcon;
    @BindView(R.id.ibChooserChangeLanguage)
    ImageButton ibChangeLanguage;

    @Inject
    UserSettingsDataManager userSettingsDataManager;

    private final ChooserActivityPresenter mPresenter = new ChooserActivityPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get().getAppComponent().inject(this);
        if (!userSettingsDataManager.loadLanguage().equals(getResources().getConfiguration().locale.getLanguage())) {
            Locale locale = new Locale(userSettingsDataManager.loadLanguage());
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
            restartActivity();
        }
        setContentView(R.layout.activity_chooser);
        ButterKnife.bind(this);
        mPresenter.bind(this);
        mPresenter.checkIfRegistered();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooserActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooserActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        ibChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChooserActivity.this);
                builder.setTitle(getString(R.string.pick_language));
                builder.setItems(new String[]{"English", "Українська", "Русский"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String langToLoad = "";
                        switch (which) {
                            case 0:
                                langToLoad = "en";
                                break;
                            case 1:
                                langToLoad = "uk";
                                break;
                            case 2:
                                langToLoad = "ru";
                                break;
                        }
                        Locale locale = new Locale(langToLoad);
                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        getBaseContext().getResources().updateConfiguration(config,
                                getBaseContext().getResources().getDisplayMetrics());
                        userSettingsDataManager.saveNewLanguagePref(langToLoad);
                        restartActivity();
                    }
                });
                builder.create().show();
            }
        });
    }

    public void restartActivity() {
        Intent intent = new Intent(ChooserActivity.this, ChooserActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        mPresenter.unbind();
        super.onDestroy();
    }

    @Override
    public void userAlreadyRegistered() {
        startActivity(new Intent(this, FeedActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
