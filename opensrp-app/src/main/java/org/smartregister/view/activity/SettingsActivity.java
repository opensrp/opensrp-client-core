package org.smartregister.view.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import org.smartregister.R;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.LangUtils;
import org.smartregister.util.UrlUtil;

import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;

public class SettingsActivity extends PreferenceActivity {


    @Override
    protected void attachBaseContext(android.content.Context base) {
        // get language from prefs
        String lang = LangUtils.getLanguage(base.getApplicationContext());
        super.attachBaseContext(LangUtils.setAppLocale(base, lang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }


    public static class MyPreferenceFragment extends PreferenceFragment {
        private static String TAG = MyPreferenceFragment.class.getName();
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference baseUrlPreference = findPreference("DRISHTI_BASE_URL");
            if (baseUrlPreference != null) {
                final EditTextPreference baseUrlEditTextPreference = (EditTextPreference) baseUrlPreference;
                baseUrlEditTextPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        final Dialog dialog = baseUrlEditTextPreference.getDialog();
                        ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String newValue = baseUrlEditTextPreference.getEditText().getText().toString();
                                if (newValue != null && UrlUtil.isValidUrl(newValue)) {
                                    baseUrlEditTextPreference.onClick(null, DialogInterface.BUTTON_POSITIVE);
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getActivity(), R.string.invalid_url_massage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        return false;
                    }
                });
                baseUrlEditTextPreference.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        Timber.i("baseUrl before text change");
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        Timber.i("baseUrl on text change");
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String text = editable.toString();
                        boolean validUrl = UrlUtil.isValidUrl(text);
                        if(!validUrl) {
                            if(text.isEmpty()){
                                baseUrlEditTextPreference.getEditText().setError(getString(R.string.msg_empty_url));
                            }
                            else{
                                baseUrlEditTextPreference.getEditText().setError(getString(R.string.invalid_url_massage));
                            }
                        }
                        else{
                            baseUrlEditTextPreference.getEditText().setError(null);
                        }

                    }
                });

                baseUrlEditTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue != null) {
                            updateUrl(newValue.toString());
                        }
                        return true;
                    }
                });
            }
        }

        private void updateUrl(String baseUrl) {
            try {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);

                URL url = new URL(baseUrl);

                String base = url.getProtocol() + "://" + url.getHost();
                int port = url.getPort();

                Timber.i("Base URL: %s", base);
                Timber.i("Port: %s", port);

                allSharedPreferences.saveHost(base);
                allSharedPreferences.savePort(port);

                Timber.i("Saved URL: %s", allSharedPreferences.fetchHost(""));
                Timber.i("Port: %s", allSharedPreferences.fetchPort(0));
            } catch (MalformedURLException e) {
                Timber.e("Malformed Url: %s", baseUrl);
            }
        }
    }

}