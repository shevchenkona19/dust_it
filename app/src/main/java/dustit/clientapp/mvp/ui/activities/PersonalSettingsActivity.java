package dustit.clientapp.mvp.ui.activities;

import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.ui.fragments.ChangeCategoriesFragment;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.managers.ThemeManager;

public class PersonalSettingsActivity extends AppCompatActivity implements ChangeCategoriesFragment.IChangeCategoriesCallback {

    private static final String CATEGORIES_FRAGMENT = "categories";
    @BindView(R.id.clPersonalSettingsChangeCategories)
    ConstraintLayout clChangeCategories;
    @BindView(R.id.tbPersonalSettingsToolbar)
    Toolbar toolbar;
    @BindView(R.id.tvPersonalCategoriesChangeLabel)
    TextView tvChangeLabel;
    @BindView(R.id.ivPersonalSettingsToChangeCat)
    ImageView ivToChangeCat;

    @Inject
    UserSettingsDataManager userSettingsDataManager;
    @Inject
    ThemeManager themeManager;

    private String themeManagerSubscriberId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);
        ButterKnife.bind(this);
        App.get().getAppComponent().inject(this);
        clChangeCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userSettingsDataManager.isRegistered()) {
                    onNotRegistered();
                    return;
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(CATEGORIES_FRAGMENT)
                        .add(R.id.flPersonalSettingsContainer, ChangeCategoriesFragment.newInstance())
                        .commit();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setColors();
        themeManagerSubscriberId = themeManager.subscribeToThemeChanges(new ThemeManager.IThemable() {
            @Override
            public void notifyThemeChanged(ThemeManager.Theme t) {
                setColors();
            }
        });
    }

    private void setColors() {
        toolbar.setBackgroundResource(themeManager.getPrimaryColor());
        toolbar.getNavigationIcon().setColorFilter(getColorFromResources(themeManager.getAccentColor()), PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitleTextColor(getColorFromResources(themeManager.getMainTextToolbarColor()));
        tvChangeLabel.setTextColor(getColorFromResources(themeManager.getMainTextMainAppColor()));
        ivToChangeCat.setColorFilter(getColorFromResources(themeManager.getAccentColor()), PorterDuff.Mode.SRC);
    }

    private int getColorFromResources(int c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getColor(c);
        } else {
            return getResources().getColor(c);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void closeChangeCategoriesFragment() {
        getSupportFragmentManager().popBackStackImmediate();
    }

    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(this);
    }

    @Override
    protected void onDestroy() {
        themeManager.unsubscribe(themeManagerSubscriberId);
        super.onDestroy();
    }
}
