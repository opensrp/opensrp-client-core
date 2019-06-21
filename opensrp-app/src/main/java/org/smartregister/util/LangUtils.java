package org.smartregister.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import org.smartregister.repository.AllSharedPreferences;

import java.util.Locale;

import timber.log.Timber;

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
        Locale locale = new Locale(language);

        Resources res = context.getResources();
        Configuration conf = res.getConfiguration();
        conf.setLocale(locale);
        try {
            context = context.createConfigurationContext(conf);
        } catch (Exception e) {
            Timber.e(e);
        }

        return context;
    }

}
