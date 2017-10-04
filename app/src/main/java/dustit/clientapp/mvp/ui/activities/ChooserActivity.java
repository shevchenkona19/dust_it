package dustit.clientapp.mvp.ui.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;

public class ChooserActivity extends AppCompatActivity {
    @BindView(R.id.btnChooserLogin)
    Button btnLogin;
    @BindView(R.id.btnChooserRegister)
    Button btnRegister;
    @BindView(R.id.ivChooserIcon)
    ImageView ivIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        ButterKnife.bind(this);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Explode fade = new Explode();
            fade.excludeTarget(R.id.clChooserLayout, true);
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);
            fade.excludeTarget(android.R.id.background, true);
            getWindow().setExitTransition(fade);
            getWindow().setEnterTransition(fade);
        }*/
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Pair pair1 = Pair.create(btnLogin, "btnLogin");
                    Pair pair2 = Pair.create(ivIcon, "icon");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ChooserActivity.this, pair1, pair2);
                    Intent intent = new Intent(ChooserActivity.this, LoginActivity.class);
                    startActivity(intent, options.toBundle());
                    finish();
                    return;
                }*/
                Intent intent = new Intent(ChooserActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooserActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
