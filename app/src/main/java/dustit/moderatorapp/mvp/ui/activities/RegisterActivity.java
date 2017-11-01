package dustit.moderatorapp.mvp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.moderatorapp.R;
import dustit.moderatorapp.mvp.presenter.activities.RegisterActivityPresenter;
import dustit.moderatorapp.mvp.ui.interfaces.IRegisterActivityView;

public class RegisterActivity extends AppCompatActivity implements IRegisterActivityView {
    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.tvRegisterToLogin)
    TextView tvToLogin;

    private RegisterActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new RegisterActivityPresenter();
        presenter.bind(this);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.register(etUsername.getText().toString(),
                        etPassword.getText().toString());
            }
        });
        tvToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Settings");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            Intent intent = new Intent(this, ChangeIpActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }

    @Override
    public void onRegistered() {
        startActivity(new Intent(this, DecideActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    public void onError() {
        Toast.makeText(this, "Errr...", Toast.LENGTH_SHORT).show();
    }
}
