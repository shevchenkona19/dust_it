package dustit.moderatorapp.mvp.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.moderatorapp.R;
import dustit.moderatorapp.mvp.presenter.activities.ChooserActivityPresenter;
import dustit.moderatorapp.mvp.ui.interfaces.IChooserActivityView;

public class ChooserActivity extends AppCompatActivity implements IChooserActivityView{
    @BindView(R.id.btnChooseLogin)
    Button btnLogin;
    @BindView(R.id.btnChooseRegister)
    Button btnRegister;
    private ChooserActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ChooserActivityPresenter();
        presenter.bind(this);
        if (presenter.isFirstTime()) {
            setContentView(R.layout.activity_chooser);
            ButterKnife.bind(this);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ChooserActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ChooserActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }
}
