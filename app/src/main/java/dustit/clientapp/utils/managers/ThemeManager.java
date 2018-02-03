package dustit.clientapp.utils.managers;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import dustit.clientapp.R;

public class ThemeManager {

    public enum Theme {
        LIGHT,
        DARK,
        DEFAULT
    }

    public ThemeManager(Context context) {
        this.context = context;
    }

    private Context context;

    private Theme currentTheme = Theme.DEFAULT;

    private int prevPrimaryColor = R.color.colorPrimaryDefault;
    private int prevPrimaryDarkColor = R.color.colorPrimaryDarkDefault;
    private int prevAccentColor = R.color.colorAccentDefault;
    private int prevMainTextHelloScreenColor = R.color.colorMainTextHelloDefault;
    private int prevSecondaryTextHelloScreenColor = R.color.colorSecondaryTextHelloDefault;
    private int prevMainTextMainAppColor = R.color.colorMainTextMainAppDefault;
    private int prevSecondaryTextMainAppColor = R.color.secondaryTextMainAppColorDefault;
    private int prevBackgroundMainColor = R.color.colorBakgroundMainDefault;
    private int prevCardBackgroundColor = R.color.colorBackgroundCardDefault;

    private int primaryColor = R.color.colorPrimaryDefault;
    private int primaryDarkColor = R.color.colorPrimaryDarkDefault;
    private int accentColor = R.color.colorAccentDefault;
    private int mainTextHelloScreenColor = R.color.colorMainTextHelloDefault;
    private int secondaryTextHelloScreenColor = R.color.colorSecondaryTextHelloDefault;
    private int mainTextMainAppColor = R.color.colorMainTextMainAppDefault;
    private int secondaryTextMainAppColor = R.color.secondaryTextMainAppColorDefault;
    private int backgroundMainColor = R.color.colorBakgroundMainDefault;
    private int cardBackgroundColor = R.color.colorBackgroundCardDefault;

    public interface IThemable {
        void notifyThemeChanged(Theme t);
    }

    private final Map<String, IThemable> subscribers = new HashMap<>();

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public String[] getThemeList() {
        return new String[]{
                context.getString(R.string.theme_light),
                context.getString(R.string.theme_default),
                context.getString(R.string.theme_dark)
        };
    }

    public void setCurrentTheme(Theme t) {
        currentTheme = t;
        switch (currentTheme) {
            case DEFAULT:
                setDefaultColors();
                break;
            case LIGHT:
                setLightColors();
                break;
            case DARK:
                setDarkColors();
                break;
        }
        notifyThemeChanged();
    }

    private void setDefaultColors() {
        prevPrimaryColor = primaryColor;
        prevPrimaryDarkColor = primaryDarkColor;
        prevAccentColor = accentColor;
        prevMainTextHelloScreenColor = mainTextHelloScreenColor;
        prevSecondaryTextHelloScreenColor = secondaryTextHelloScreenColor;
        prevMainTextMainAppColor = mainTextMainAppColor;
        prevSecondaryTextMainAppColor = secondaryTextMainAppColor;
        prevBackgroundMainColor = backgroundMainColor;
        prevCardBackgroundColor = cardBackgroundColor;
        primaryColor = R.color.colorPrimaryDefault;
        primaryDarkColor = R.color.colorPrimaryDarkDefault;
        accentColor = R.color.colorAccentDefault;
        mainTextHelloScreenColor = R.color.colorMainTextHelloDefault;
        secondaryTextHelloScreenColor = R.color.colorSecondaryTextHelloDefault;
        mainTextMainAppColor = R.color.colorMainTextMainAppDefault;
        secondaryTextMainAppColor = R.color.secondaryTextMainAppColorDefault;
        backgroundMainColor = R.color.colorBakgroundMainDefault;
        cardBackgroundColor = R.color.colorBackgroundCardDefault;
    }

    private void setLightColors() {
        prevPrimaryColor = primaryColor;
        prevPrimaryDarkColor = primaryDarkColor;
        prevAccentColor = accentColor;
        prevMainTextHelloScreenColor = mainTextHelloScreenColor;
        prevSecondaryTextHelloScreenColor = secondaryTextHelloScreenColor;
        prevMainTextMainAppColor = mainTextMainAppColor;
        prevSecondaryTextMainAppColor = secondaryTextMainAppColor;
        prevBackgroundMainColor = backgroundMainColor;
        prevCardBackgroundColor = cardBackgroundColor;
        primaryColor = R.color.colorPrimaryLight;
        primaryDarkColor = R.color.colorPrimaryDarkLight;
        accentColor = R.color.colorAccentLight;
        mainTextHelloScreenColor = R.color.colorMainTextHelloLight;
        secondaryTextHelloScreenColor = R.color.colorSecondaryTextHelloLight;
        mainTextMainAppColor = R.color.colorMainTextMainAppLight;
        secondaryTextMainAppColor = R.color.secondaryTextMainAppColorLight;
        backgroundMainColor = R.color.colorBakgroundMainLight;
        cardBackgroundColor = R.color.colorBackgroundCardLight;
    }

    private void setDarkColors() {
        prevPrimaryColor = primaryColor;
        prevPrimaryDarkColor = primaryDarkColor;
        prevAccentColor = accentColor;
        prevMainTextHelloScreenColor = mainTextHelloScreenColor;
        prevSecondaryTextHelloScreenColor = secondaryTextHelloScreenColor;
        prevMainTextMainAppColor = mainTextMainAppColor;
        prevSecondaryTextMainAppColor = secondaryTextMainAppColor;
        prevBackgroundMainColor = backgroundMainColor;
        prevCardBackgroundColor = cardBackgroundColor;
        primaryColor = R.color.colorPrimaryDark;
        primaryDarkColor = R.color.colorPrimaryDarkDark;
        accentColor = R.color.colorAccentDark;
        mainTextHelloScreenColor = R.color.colorMainTextHelloDark;
        secondaryTextHelloScreenColor = R.color.colorSecondaryTextHelloDark;
        mainTextMainAppColor = R.color.colorMainTextMainAppDark;
        secondaryTextMainAppColor = R.color.secondaryTextMainAppColorDark;
        backgroundMainColor = R.color.colorBakgroundMainDark;
        cardBackgroundColor = R.color.colorBackgroundCardDark;
    }

    private void notifyThemeChanged() {
        final Set<String> keys = subscribers.keySet();
        for (String key : keys) {
            subscribers.get(key).notifyThemeChanged(currentTheme);
        }
    }

    public String subscribeToThemeChanges(IThemable subscriber) {
        final String key = "imakey" + Math.random() + Math.random() + Math.random();
        subscribers.put(key, subscriber);
        return key;
    }

    public void unsubscribe(String id) {
        subscribers.remove(id);
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public int getPrimaryDarkColor() {
        return primaryDarkColor;
    }

    public int getAccentColor() {
        return accentColor;
    }

    public int getMainTextHelloScreenColor() {
        return mainTextHelloScreenColor;
    }

    public int getSecondaryTextHelloScreenColor() {
        return secondaryTextHelloScreenColor;
    }

    public int getMainTextMainAppColor() {
        return mainTextMainAppColor;
    }

    public int getSecondaryTextMainAppColor() {
        return secondaryTextMainAppColor;
    }

    public int getBackgroundMainColor() {
        return backgroundMainColor;
    }

    public int getCardBackgroundColor() {
        return cardBackgroundColor;
    }

    public int getPrevPrimaryColor() {
        return prevPrimaryColor;
    }

    public int getPrevPrimaryDarkColor() {
        return prevPrimaryDarkColor;
    }

    public int getPrevAccentColor() {
        return prevAccentColor;
    }

    public int getPrevMainTextHelloScreenColor() {
        return prevMainTextHelloScreenColor;
    }

    public int getPrevSecondaryTextHelloScreenColor() {
        return prevSecondaryTextHelloScreenColor;
    }

    public int getPrevMainTextMainAppColor() {
        return prevMainTextMainAppColor;
    }

    public int getPrevSecondaryTextMainAppColor() {
        return prevSecondaryTextMainAppColor;
    }

    public int getPrevBackgroundMainColor() {
        return prevBackgroundMainColor;
    }

    public int getPrevCardBackgroundColor() {
        return prevCardBackgroundColor;
    }
}
