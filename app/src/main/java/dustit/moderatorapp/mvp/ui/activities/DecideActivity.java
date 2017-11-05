package dustit.moderatorapp.mvp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.moderatorapp.R;
import dustit.moderatorapp.mvp.model.entities.CategoriesIdEntity;
import dustit.moderatorapp.mvp.model.entities.Category;
import dustit.moderatorapp.mvp.model.entities.MemIdEntity;
import dustit.moderatorapp.mvp.presenter.activities.DecideActivityPresenter;
import dustit.moderatorapp.mvp.ui.adapters.DecideCategoriesRecyclerViewAdapter;
import dustit.moderatorapp.mvp.ui.interfaces.IDecideActivityView;
import dustit.moderatorapp.utils.IConstants;

public class DecideActivity extends AppCompatActivity implements IDecideActivityView {

    private final DecideActivityPresenter presenter = new DecideActivityPresenter();
    @BindView(R.id.sdvDecideMem)
    SimpleDraweeView sdvMemImage;
    @BindView(R.id.rvDecideCategories)
    RecyclerView rvCategories;
    @BindView(R.id.btnDecideSend)
    Button btnSend;
    @BindView(R.id.btnDecideDiscard)
    Button btnDiscard;
    @BindView(R.id.pbDecideLoading)
    ProgressBar pbLoading;
    @BindView(R.id.btnDecideRetryMem)
    Button btnRetryMem;
    @BindView(R.id.btnDecideRetryCategories)
    Button btnRetryCategories;
    private DecideCategoriesRecyclerViewAdapter adapter;
    private MemIdEntity currentMem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decide);
        ButterKnife.bind(this);
        pbLoading.setVisibility(View.VISIBLE);
        presenter.bind(this);
        adapter = new DecideCategoriesRecyclerViewAdapter(this);
        rvCategories.setLayoutManager(new GridLayoutManager(this, 3));
        rvCategories.setAdapter(adapter);
        presenter.getCategories();
        presenter.getNewMem();
        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.discardMem(String.valueOf(currentMem.getId()));
                pbLoading.setVisibility(View.VISIBLE);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoriesIdEntity entity = adapter.getCheckedCategories();
                if (entity.getIds().length() < 1) {
                    Toast.makeText(DecideActivity.this, "Выберите хотя-бы одну категорию!", Toast.LENGTH_SHORT).show();
                } else {
                    pbLoading.setVisibility(View.VISIBLE);
                    presenter.postMem(String.valueOf(currentMem.getId()), entity);
                }
            }
        });

        btnRetryMem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.getNewMem();
            }
        });

        btnRetryCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.getCategories();
            }
        });
    }

    @Override
    public void onCategoriesLoaded(List<Category> categories) {
        pbLoading.setVisibility(View.GONE);
        btnRetryCategories.setVisibility(View.GONE);
        rvCategories.setVisibility(View.VISIBLE);
        adapter.updateList(categories);
    }

    @Override
    public void onErrorInLoadingCategories() {
        pbLoading.setVisibility(View.GONE);
        rvCategories.setVisibility(View.GONE);
        btnRetryCategories.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Ошибка в загрузке категорий", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMemPosted() {
        pbLoading.setVisibility(View.GONE);
        presenter.getNewMem();
        pbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onErrorPostingMem() {
        pbLoading.setVisibility(View.GONE);
        Toast.makeText(this, "Ошибка в отправке мема", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNewMemArrived(MemIdEntity memIdEntity) {
        btnRetryMem.setVisibility(View.GONE);
        sdvMemImage.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
        currentMem = memIdEntity;
        sdvMemImage.setImageURI(IConstants.BASE_URL + "/moderator/imgs?token="
                + presenter.getToken() + "&id=" + memIdEntity.getId());
        adapter.resetChecked();
    }

    @Override
    public void onErrorInGettingNewMem() {
        pbLoading.setVisibility(View.GONE);
        sdvMemImage.setVisibility(View.GONE);
        btnRetryMem.setVisibility(View.GONE);
        Toast.makeText(this, "Не удается получить мем", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMemDiscarded() {
        pbLoading.setVisibility(View.GONE);
        presenter.getNewMem();
        adapter.resetChecked();
    }

    @Override
    public void onErrorInDiscardingMem() {
        pbLoading.setVisibility(View.GONE);
        Toast.makeText(this, "Ошибка в удалении мема", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Настройки");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }
}
