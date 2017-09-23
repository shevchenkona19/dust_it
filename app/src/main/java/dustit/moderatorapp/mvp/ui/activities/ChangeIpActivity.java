package dustit.moderatorapp.mvp.ui.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.moderatorapp.R;
import dustit.moderatorapp.utils.IConstants;

public class ChangeIpActivity extends AppCompatActivity {
    @BindView(R.id.btnChangeIp)
    Button btnChangeIp;
    @BindView(R.id.etChangeIP)
    EditText etChangeIp;
    @BindView(R.id.clChangeIp)
    ConstraintLayout clChangeIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_ip);
        ButterKnife.bind(this);
        btnChangeIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = etChangeIp.getText().toString();
                if (ip.equals("")) {
                    showMessage("IP не может быть пустым");
                    return;
                }
                if (ip.contains("http://")){
                    IConstants.BASE_URL = ip;
                } else {
                    IConstants.BASE_URL = "http://" + ip;
                }
                showMessage("IP успешно сохранено. При перезапуске приложения, IP надо будет установить снова");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ChangeIpActivity.this, RegisterActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 5000);
            }
        });
    }

    private void showMessage(String message){
        Snackbar.make(clChangeIp, message, Snackbar.LENGTH_LONG).show();
    }
}
