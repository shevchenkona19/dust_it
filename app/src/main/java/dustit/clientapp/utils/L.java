package dustit.clientapp.utils;

import android.util.Log;

public class L {

    private static boolean debug = false;

    public static void print(String message) {
        if (debug) Log.d("MY", message);
    }

    public static void print(String tag, String message) {
        if (debug) Log.d(tag, message);
    }

    public static void print(int a) {
        if (debug) print("" + a);
    }

}
