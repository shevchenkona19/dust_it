package dustit.clientapp.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by shevc on 22.09.2017.
 * Let's GO!
 */

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    public interface IGestureListener {
        void onSwipeUp();
    }

    private IGestureListener gestureListener;

    public GestureListener(IGestureListener gestureListener) {
        super();
        this.gestureListener = gestureListener;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float x1 = e1.getX();
        float y1 = e1.getY();

        float x2 = e2.getX();
        float y2 = e2.getY();

        Direction direction = getDirection(x1, y1, x2, y2);
        return onSwipe(direction);
    }


    public boolean onSwipe(Direction direction) {
        switch (direction) {
            case up:
                gestureListener.onSwipeUp();
                return true;
            default:
                return false;
        }
    }

    public Direction getDirection(float x1, float y1, float x2, float y2) {
        double angle = getAngle(x1, y1, x2, y2);
        return Direction.get(angle);
    }

    public double getAngle(float x1, float y1, float x2, float y2) {

        double rad = Math.atan2(y1 - y2, x2 - x1) + Math.PI;
        return (rad * 180 / Math.PI + 180) % 360;
    }


    public enum Direction {
        up,
        down,
        left,
        right;

        public static Direction get(double angle) {
            if (inRange(angle, 45, 135)) {
                return Direction.up;
            } else if (inRange(angle, 0, 45) || inRange(angle, 315, 360)) {
                return Direction.right;
            } else if (inRange(angle, 225, 315)) {
                return Direction.down;
            } else {
                return Direction.left;
            }

        }

        private static boolean inRange(double angle, float init, float end) {
            return (angle >= init) && (angle < end);
        }
    }
}
