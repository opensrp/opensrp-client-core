package org.smartregister.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import org.smartregister.util.LangUtils;


/** Use this class to ensure your activities have multi-language support
 * @author Rodgers Andati
 * @since 2019-05-03
 */
public class MultiLanguageActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(android.content.Context base) {
        // get language from prefs
        String lang = LangUtils.getLanguage(base.getApplicationContext());
        super.attachBaseContext(LangUtils.setAppLocale(base, lang));
    }

    //solution borrowed from
    // https://stackoverflow.com/questions/55265834/change-locale-not-work-after-migrate-to-androidx/61420643#61420643
    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // add this to fix androidx.appcompat:appcompat 1.1.0 bug
            // which happens on Android 6.x ~ 7.x
            getResources();
        }
        super.applyOverrideConfiguration(overrideConfiguration);
    }

}
