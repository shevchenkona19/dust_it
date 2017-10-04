package dustit.clientapp.mvp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.ThemeEntity;
import dustit.clientapp.mvp.ui.adapters.ResultRecyclerViewAdapter;

public class ResultActivity extends AppCompatActivity {
    @BindView(R.id.ivResultIcon)
    ImageView ivIcon;
    @BindView(R.id.rvResultThemes)
    RecyclerView rvThemes;
    @BindView(R.id.btnResultGo)
    Button btnGo;

    private ResultRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        List<ThemeEntity> themeEntities = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            themeEntities.add(new ThemeEntity("Тема" + i, i%2==0));
        }
        rvThemes.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ResultRecyclerViewAdapter(this, themeEntities);
        rvThemes.setAdapter(adapter);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultActivity.this, FeedActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
