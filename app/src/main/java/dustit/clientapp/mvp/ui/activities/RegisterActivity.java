package dustit.clientapp.mvp.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.presenters.activities.RegisterActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IRegisterActivityView;

public class RegisterActivity extends AppCompatActivity implements IRegisterActivityView{
    @BindView(R.id.etRegisterUsername)
    EditText etUsername;
    @BindView(R.id.etRegisterEmail)
    EditText etEmail;
    @BindView(R.id.etRegisterPassword)
    EditText etPassword;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    private final RegisterActivityPresenter presenter = new RegisterActivityPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        presenter.bind(this);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*presenter.registerUser(etUsername.getText().toString(),
                        etPassword.getText().toString(), etEmail.getText().toString());*/
                onRegisteredSuccessfully();
            }
        });
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }

    @Override
    public void onRegisteredSuccessfully() {
        Intent intent = new Intent(this, PreTestActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, "ERROR\n" + message, Toast.LENGTH_SHORT).show();
    }
}
