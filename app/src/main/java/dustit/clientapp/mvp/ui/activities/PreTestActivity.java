package dustit.clientapp.mvp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import dustit.clientapp.R;
import dustit.clientapp.utils.GestureListener;

public class PreTestActivity extends AppCompatActivity implements GestureListener.IGestureListener {
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_test);
        GestureListener gestureListener = new GestureListener(this);
        mGestureDetector = new GestureDetector(this, gestureListener);

    }

    @Override
    public void onSwipeUp() {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

}
