package dustit.moderatorapp.mvp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import dustit.moderatorapp.R;
import dustit.moderatorapp.mvp.presenter.activities.SettingsActivityPresenter;
import dustit.moderatorapp.mvp.ui.interfaces.ISettingsActivityView;

public class SettingsActivity extends AppCompatActivity implements ISettingsActivityView {
    private final SettingsActivityPresenter presenter = new SettingsActivityPresenter();
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        presenter.bind(this);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.logout();
            }
        });
    }

    @Override
    public void onSuccess() {
        startActivity(new Intent(this, ChooserActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    @Override
    public void onError() {
        Toast.makeText(this, "Errr...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }
}
