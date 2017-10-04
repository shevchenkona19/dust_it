package dustit.clientapp.mvp.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.presenters.activities.LoginActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.ILoginActivityView;

public class LoginActivity extends AppCompatActivity implements ILoginActivityView {
    @BindView(R.id.etLoginPassword)
    EditText etPassword;
    @BindView(R.id.etLoginUsername)
    EditText etUsername;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    private final LoginActivityPresenter loginActivityPresenter = new LoginActivityPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Explode fade = new Explode();
            fade.excludeTarget(R.id.clLoginLayout, true);
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);
            fade.excludeTarget(android.R.id.background, true);
            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }*/
        loginActivityPresenter.bind(this);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*loginActivityPresenter.loginUser(etUsername.getText().toString(),
                        etPassword.getText().toString());*/
                onLoggedSuccessfully();
            }
        });
    }

    @Override
    protected void onDestroy() {
        loginActivityPresenter.unbind();
        super.onDestroy();
    }

    @Override
    public void onLoggedSuccessfully() {
        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, "ERROR\n" + message, Toast.LENGTH_SHORT).show();
    }

}
