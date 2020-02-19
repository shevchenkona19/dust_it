package dustit.clientapp.mvp.ui.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.customviews.WrapperLinearLayoutManager;
import dustit.clientapp.mvp.model.entities.UserEntity;
import dustit.clientapp.mvp.presenters.activities.SearchActivityPresenter;
import dustit.clientapp.mvp.ui.adapters.UserSearchAdapter;
import dustit.clientapp.mvp.ui.interfaces.ISearchActivityView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;

public class SearchActivity extends AppCompatActivity implements ISearchActivityView, UserSearchAdapter.IUserSearchInteraction {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search)
    SearchView searchView;
    @BindView(R.id.rvUsersList)
    RecyclerView rvUsers;
    @BindView(R.id.loading)
    ProgressBar pbLoading;

    private SearchActivityPresenter presenter = new SearchActivityPresenter();
    private UserSearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        final Transition slide = new Slide();
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        final Window window = getWindow();
        window.setEnterTransition(slide);
        window.setReturnTransition(slide);
        window.setExitTransition(slide);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        presenter.bind(this);
        adapter = new UserSearchAdapter(this);
        adapter.setHasStableIds(true);
        rvUsers.setLayoutManager(new WrapperLinearLayoutManager(this));
        rvUsers.setAdapter(adapter);
        rvUsers.setHasFixedSize(true);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 3)
                    presenter.searchUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 3)
                    presenter.searchUsers(newText);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
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
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }

    @Override
    public void onSearchResultsArrived(List<UserEntity> list) {
        finishLoading(true);
        adapter.updateList(list);
    }

    private void finishLoading(boolean showList) {
        if (showList) {
            rvUsers.setVisibility(View.VISIBLE);
        }
        pbLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStartLoading() {
        rvUsers.setVisibility(View.INVISIBLE);
        pbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError() {
        finishLoading(false);
    }

    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(this);
    }

    @Override
    public void onUserSelected(UserEntity user) {
        Intent intent = new Intent(this, AccountActivity.class);
        intent.putExtra(IConstants.IBundle.IS_ME, false);
        intent.putExtra(IConstants.IBundle.USER_ID, user.getUserId());
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

    }
}
