package dustit.clientapp.utils.managers;

import android.content.Context;
import android.content.res.Resources;

import java.util.Locale;

import dustit.clientapp.R;

public class ThemeManager {
    private Context context;

    public enum Theme {
        LIGHT,
        NIGHT,
    }

    private Theme currentTheme = Theme.LIGHT;

    public ThemeManager(Context bundle) {
        context = bundle;
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public String[] getThemeList() {
        return new String[]{
                context.getString(R.string.theme_light),
                context.getString(R.string.theme_dark)
        };
    }

    public void setCurrentTheme(Theme t) {
        currentTheme = t;
    }
}
