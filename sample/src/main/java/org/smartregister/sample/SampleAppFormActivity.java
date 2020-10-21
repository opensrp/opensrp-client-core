package org.smartregister.sample;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.vijay.jsonwizard.activities.JsonWizardFormActivity;

import org.smartregister.AllConstants;
import org.smartregister.util.LangUtils;

import java.util.HashMap;
import java.util.Set;

public class SampleAppFormActivity extends JsonWizardFormActivity {

    private HashMap<String, String> parcelableData = new HashMap<>();

    @Override
    protected void attachBaseContext(android.content.Context base) {

        String language = LangUtils.getLanguage(base);
        super.attachBaseContext(LangUtils.setAppLocale(base, language));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            Set<String> keySet = extras.keySet();

            for (String key : keySet) {
                if (!key.equals(AllConstants.IntentExtra.JsonForm.JSON)) {
                    Object objectValue = extras.get(key);

                    if (objectValue instanceof String) {
                        String value = (String) objectValue;
                        parcelableData.put(key, value);
                    }
                }
            }
        }
    }
/*
    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        if (toolbar != null) {
            toolbar.setContentInsetStartWithNavigation(0);
        }
        super.setSupportActionBar(toolbar);
    }*/

    @NonNull
    public HashMap<String, String> getParcelableData() {
        return parcelableData;
    }
}
