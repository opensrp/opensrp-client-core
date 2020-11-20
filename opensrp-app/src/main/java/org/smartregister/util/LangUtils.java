package org.smartregister.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.preference.PreferenceManager;

import androidx.annotation.RequiresApi;

import org.smartregister.repository.AllSharedPreferences;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class LangUtils {

    public static void saveLanguage(Context ctx, String language) {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(ctx));
        allSharedPreferences.saveLanguagePreference(language);
        // update context
        setAppLocale(ctx, language);
    }

    public static String getLanguage(Context ctx) {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(ctx));
        return allSharedPreferences.fetchLanguagePreference();
    }

    public static Context setAppLocale(Context context, String language) {
        // This has been copied directly from
        // https://stackoverflow.com/a/59950631/5588557 & tested to work with android 7
        return updateResources(context, language);
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setLocaleForApi24(config, locale);
            context = context.createConfigurationContext(config);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void setLocaleForApi24(Configuration config, Locale target) {
        Set<Locale> set = new LinkedHashSet<>();
        // bring the target locale to the front of the list
        set.add(target);

        LocaleList all = LocaleList.getDefault();
        for (int i = 0; i < all.size(); i++) {
            // append other locales supported by the user
            set.add(all.get(i));
        }

        Locale[] locales = set.toArray(new Locale[0]);
        config.setLocales(new LocaleList(locales));
    }

}
