package dustit.clientapp.utils.behaviour;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class BottomNavigationBehaviour<V extends View> extends CoordinatorLayout.Behavior<V> {
    public BottomNavigationBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @ViewCompat.NestedScrollType
    private int lastStartedType = 0;
    private ValueAnimator offsetAnimator = null;
    boolean isSnappingEnabled = true;

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        if (axes != View.SCROLL_AXIS_VERTICAL) {
            return false;
        }
        lastStartedType = type;
        offsetAnimator.cancel();
        return true;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        child.setTranslationY(Math.max(0f, Math.min(child.getHeight(), child.getTranslationY() + dy)));
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int type) {
        if (!isSnappingEnabled) return;
        if (lastStartedType == ViewCompat.TYPE_TOUCH || type == ViewCompat.TYPE_NON_TOUCH) {
            float currTranslation = child.getTranslationY();
            float childHalfHeight = child.getHeight() * 0.5f;

            if (currTranslation >= childHalfHeight) {
                animateBarVisibility(child, false);
            } else {
                animateBarVisibility(child, true);
            }
        }
    }

    private void animateBarVisibility(View child, boolean isVisible) {
        if (offsetAnimator == null) {
            offsetAnimator = new ValueAnimator();
            offsetAnimator.setInterpolator(new DecelerateInterpolator());
            offsetAnimator.setDuration(150L);
            offsetAnimator.addUpdateListener(animation -> child.setTranslationY((Float) animation.getAnimatedValue()));
        } else {
            offsetAnimator.cancel();
        }
        float targetTranslation = isVisible ? 0f : child.getHeight();
        offsetAnimator.setFloatValues(child.getTranslationY(), targetTranslation);
        offsetAnimator.start();

    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, V child, View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout) {
            updateSnackbar(child, (Snackbar.SnackbarLayout) dependency);
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    private void updateSnackbar(V child, Snackbar.SnackbarLayout snackbarLayout) {
        if (snackbarLayout.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackbarLayout.getLayoutParams();

            params.setAnchorId(child.getId());
            params.anchorGravity = Gravity.TOP;
            params.gravity = Gravity.TOP;
            snackbarLayout.setLayoutParams(params);
        }
    }
}
