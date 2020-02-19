package dustit.clientapp.mvp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;

public class PreTestActivity extends AppCompatActivity {
    @BindView(R.id.btnPretestSkip)
    Button btnSkip;
    @BindView(R.id.btnPretestSubmit)
    Button btnSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_test);
        ButterKnife.bind(this);
        btnSkip.setOnClickListener((View) -> startActivity(new Intent(this, ResultActivity.class)));
        btnSubmit.setOnClickListener((View) -> startActivity(new Intent(this, TestActivity.class)));
    }
}
