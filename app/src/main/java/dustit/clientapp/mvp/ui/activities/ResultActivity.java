package dustit.clientapp.mvp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.presenters.activities.ResultActivityPresenter;
import dustit.clientapp.mvp.ui.adapters.ResultRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.interfaces.IResultActivityView;

public class ResultActivity extends AppCompatActivity implements IResultActivityView{
    @BindView(R.id.ivResultIcon)
    ImageView ivIcon;
    @BindView(R.id.rvResultThemes)
    RecyclerView rvThemes;
    @BindView(R.id.btnResultGo)
    Button btnGo;
    @BindView(R.id.tvResultTextDescription)
    TextView tvDescription;
    @BindView(R.id.tvResultLoadingText)
    TextView tvLoading;
    @BindView(R.id.pbResultLoading)
    ProgressBar pbLoading;
    @BindView(R.id.btnResultReloadCategories)
    Button btnReloadCategories;
    @BindView(R.id.tvResultFailedToLoad)
    TextView tvResultFailedToLoadCategories;
    @BindView(R.id.btnResultRetrySendingCategories)
    Button btnRetrySending;
    private String[] interestedCategoriesIds;
    private ResultRecyclerViewAdapter adapter;
    private final ResultActivityPresenter presenter = new ResultActivityPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        presenter.bind(this);
        interestedCategoriesIds = getIntent().getExtras().getStringArray(TestActivity.CATEGORY_LIST_KEY);
        rvThemes.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new ResultRecyclerViewAdapter(this);
        rvThemes.setAdapter(adapter);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvThemes.setVisibility(View.GONE);
                tvDescription.setVisibility(View.GONE);
                pbLoading.setVisibility(View.VISIBLE);
             presenter.toMemes(adapter.getChecked());
            }
        });
        btnReloadCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvResultFailedToLoadCategories.setVisibility(View.GONE);
                btnReloadCategories.setVisibility(View.GONE);
                pbLoading.setVisibility(View.VISIBLE);
                presenter.loadCategories();
            }
        });
        btnRetrySending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvResultFailedToLoadCategories.setVisibility(View.GONE);
                btnRetrySending.setVisibility(View.GONE);
                pbLoading.setVisibility(View.VISIBLE);
                presenter.toMemes(adapter.getChecked());
            }
        });
        presenter.loadCategories();
    }

    @Override
    public void onCategoriesLoaded(List<Category> list) {
        adapter.updateItems(list);
        adapter.setChecks(interestedCategoriesIds);
        pbLoading.setVisibility(View.GONE);
        tvLoading.setVisibility(View.GONE);
        tvDescription.setVisibility(View.VISIBLE);
        rvThemes.setVisibility(View.VISIBLE);
        btnGo.setVisibility(View.VISIBLE);
    }

    @Override
    public void onErrorInLoadingCategories() {
        pbLoading.setVisibility(View.GONE);
        tvLoading.setVisibility(View.GONE);
        tvResultFailedToLoadCategories.setVisibility(View.VISIBLE);
        btnReloadCategories.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailedToSendCategories() {
        pbLoading.setVisibility(View.GONE);
        btnRetrySending.setVisibility(View.VISIBLE);
        tvResultFailedToLoadCategories.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFinishedResultActivity() {
        Intent intent = new Intent(ResultActivity.this, FeedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }
}
