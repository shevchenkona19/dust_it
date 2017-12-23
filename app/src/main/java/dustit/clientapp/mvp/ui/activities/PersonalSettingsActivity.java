package dustit.clientapp.mvp.ui.activities;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.ui.fragments.ChangeCategoriesFragment;

public class PersonalSettingsActivity extends AppCompatActivity implements ChangeCategoriesFragment.IChangeCategoriesCallback{

    private static final String CATEGORIES_FRAGMENT = "categories";
    @BindView(R.id.clPersonalSettingsChangeCategories)
    ConstraintLayout clChangeCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);
        ButterKnife.bind(this);
        clChangeCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(CATEGORIES_FRAGMENT)
                        .add(R.id.flPersonalSettingsContainer, ChangeCategoriesFragment.newInstance())
                        .commit();
            }
        });
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
}
