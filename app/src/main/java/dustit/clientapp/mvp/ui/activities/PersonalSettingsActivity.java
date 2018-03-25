package dustit.clientapp.mvp.ui.activities;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
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
    @BindView(R.id.flPersonalSettingsContainer)
    ViewGroup container;

    @Inject
    ThemeManager themeManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get().getAppComponent().inject(this);
        setContentView(R.layout.activity_personal_settings);
        ButterKnife.bind(this);
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
                container.setVisibility(View.VISIBLE);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
            container.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void closeChangeCategoriesFragment() {
        getSupportFragmentManager().popBackStackImmediate();
        container.setVisibility(View.GONE);
    }

    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
