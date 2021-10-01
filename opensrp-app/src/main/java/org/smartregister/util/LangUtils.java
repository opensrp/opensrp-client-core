package org.smartregister.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
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

    public static String getLocaleStringResource(Locale requestedLocale, int resourceId, Context context) {
        String result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { // use latest api
            Configuration config = new Configuration(context.getResources().getConfiguration());
            config.setLocale(requestedLocale);
            result = context.createConfigurationContext(config).getText(resourceId).toString();
        }
        else { // support older android versions
            Resources resources = context.getResources();
            Configuration conf = resources.getConfiguration();
            Locale savedLocale = conf.locale;
            conf.locale = requestedLocale;
            resources.updateConfiguration(conf, null);

            // retrieve resources from desired locale
            result = resources.getString(resourceId);

            // restore original locale
            conf.locale = savedLocale;
            resources.updateConfiguration(conf, null);
        }

        return result;
    }

    public static Configuration setAppLocale(Context context, String language) {
        Resources res = context.getResources();
        Configuration configuration = res.getConfiguration();

        try {
            Locale locale = new Locale(language);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocale(locale);

                LocaleList localeList = new LocaleList(locale);
                LocaleList.setDefault(localeList);
                configuration.setLocales(localeList);

                context.createConfigurationContext(configuration);

            } else {
                configuration.locale = locale;
                res.updateConfiguration(configuration, res.getDisplayMetrics());
            }


        } catch (Exception e) {
            Timber.e(e);
        }

        return configuration;
    }

}
