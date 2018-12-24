package dustit.clientapp.mvp.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

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
    @BindView(R.id.btnChangeCategories)
    Button btnChangeCategories;
    @BindView(R.id.btnRestartTest)
    Button btnRestartTest;
    @BindView(R.id.tbPersonalSettingsToolbar)
    Toolbar toolbar;
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
        if (userSettingsDataManager.isRegistered()) {
            btnChangeCategories.setOnClickListener(view -> {
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(CATEGORIES_FRAGMENT)
                        .add(R.id.flPersonalSettingsContainer, ChangeCategoriesFragment.newInstance())
                        .commit();
                container.setVisibility(View.VISIBLE);
                btnChangeCategories.setVisibility(View.INVISIBLE);
                btnRestartTest.setVisibility(View.INVISIBLE);
            });
            btnRestartTest.setOnClickListener(view -> {
                Intent intent = new Intent(PersonalSettingsActivity.this, TestActivity.class);
                startActivity(intent);
                finish();
            });
        } else {
            btnChangeCategories.setText(R.string.register);
            btnChangeCategories.setOnClickListener(view -> AlertBuilder.showRegisterPrompt(this));
            btnRestartTest.setOnClickListener(view -> AlertBuilder.showNotRegisteredPrompt(this));
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                int flags = 0;
                window.getDecorView().setSystemUiVisibility(flags);
            } else {
                int flags = window.getDecorView().getSystemUiVisibility();
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.getDecorView().setSystemUiVisibility(flags);
                window.setStatusBarColor(Color.WHITE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
            container.setVisibility(View.GONE);
            btnChangeCategories.setVisibility(View.VISIBLE);
            btnRestartTest.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void closeChangeCategoriesFragment() {
        getSupportFragmentManager().popBackStackImmediate();
        container.setVisibility(View.GONE);
        btnChangeCategories.setVisibility(View.VISIBLE);
        btnRestartTest.setVisibility(View.VISIBLE);
    }

    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(this);
    }
}
