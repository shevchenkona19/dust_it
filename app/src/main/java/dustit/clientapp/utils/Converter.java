package dustit.clientapp.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class Converter {
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
