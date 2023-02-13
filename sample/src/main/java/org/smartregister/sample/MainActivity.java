package org.smartregister.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.smartregister.cryptography.CryptographicHelper;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.sample.fragment.ReportFragment;
import org.smartregister.util.AppHealthUtils;
import org.smartregister.util.DateUtil;
import org.smartregister.util.LangUtils;
import org.smartregister.util.PermissionUtils;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.activity.MultiLanguageActivity;
import org.smartregister.view.activity.StatsActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class MainActivity extends MultiLanguageActivity implements AppHealthUtils.HealthStatsView {

    DatePicker picker;
    Button btnGet;
    TextView tvw;
    CryptographicHelper cryptographicHelper = null;
    TextView encDecTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.app_name);

        Activity activity = this;
        tvw = (TextView) findViewById(R.id.textView1);
        picker = (DatePicker) findViewById(R.id.datePicker1);
        encDecTextView = (TextView) findViewById(R.id.encrypt_decrypt_tv);

        picker.setMinDate(new LocalDate().minusYears(2).toDate().getTime());
        picker.setMaxDate(new LocalDate().plusYears(3).toDate().getTime());
        picker.updateDate(2019, 5, 22);

        btnGet = (Button) findViewById(R.id.button1);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvw.setText(getString(R.string.selected_date_format, picker.getDayOfMonth(), (picker.getMonth() + 1), picker.getYear()));
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }
        });
        SmartRegisterQueryBuilder srqb = new SmartRegisterQueryBuilder();
        String query = srqb.searchQueryFts("ec_household", new String[]{"ec_woman", "ec_child", "ec_member"}, "date_removed IS NULL ", "ali", "", 20, 0);
        System.out.println(query);

        // lang spinner
        Spinner languageSpinner = findViewById(R.id.lang_spinner);
        List<String> langArray = new ArrayList<>();
        langArray.add("English");
        langArray.add("Français");
        langArray.add("العربية");
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, langArray);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(dataAdapter);

        // set language from preferences
        String langPref = LangUtils.getLanguage(activity.getApplicationContext());
        for (int i = 0; i < langArray.size(); i++) {
            if (langPref != null && langArray.get(i).toLowerCase().startsWith(langPref)) {
                languageSpinner.setSelection(i);
                break;
            } else {
                languageSpinner.setSelection(2);
            }
        }

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int count = 0;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (count >= 1) {
                    String lang = dataAdapter.getItem(position).toLowerCase();
                    Locale LOCALE;
                    switch (lang) {
                        case "english":
                            LOCALE = Locale.ENGLISH;
                            break;
                        case "français":
                            LOCALE = Locale.FRENCH;
                            break;
                        case "العربية":
                            LOCALE = new Locale("ar");
                            break;
                        default:
                            LOCALE = Locale.ENGLISH;
                            break;
                    }
                    // save language
                    LangUtils.saveLanguage(getApplicationContext(), LOCALE.getLanguage());

                    // refresh activity
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    getApplicationContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                }
                count++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // sometimes you need nothing here
            }
        });

        ((TextView) findViewById(R.id.time)).setText(DateUtil.getDuration(new DateTime().minusYears(4).minusMonths(3).minusWeeks(2).minusDays(1)));

        new AppHealthUtils(findViewById(R.id.show_sync_stats));

        // File encryption example section
        ToggleButton toggle = (ToggleButton) findViewById(R.id.encrypt_decrypt_toggle);
        cryptographicHelper = CryptographicHelper.getInstance(this);
        String filename = "test.txt";
        String contents = getString(R.string.encrypt_decrypt_string);
        // Create a file with the contents above
        try (FileOutputStream fos = MainActivity.this.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(contents.getBytes());
            fos.flush();
        } catch (FileNotFoundException e) {
            Timber.e(e);
        } catch (IOException e) {
            Timber.e(e);
        }

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String keyAlias = "sample";
                // Get or create a key with the Alias name sample or create one if id does not exist
                if (cryptographicHelper.getKey(keyAlias) == null) {
                    cryptographicHelper.generateKey(keyAlias);
                    Timber.i("key with alias %s generated", keyAlias);
                }

                FileInputStream inputStream = null;
                FileOutputStream fileOutputStream = null;

                if (isChecked) {
                    try {
                        // read the text.txt while it is in plain text and write to file
                        inputStream = openFileInput(filename);
                        byte[] inputBytes = new byte[inputStream.available()];
                        inputStream.read(inputBytes);
                        byte[] encryptedContents = CryptographicHelper.encrypt(inputBytes, keyAlias);

                        Timber.i("encrypted stuff to write %S ", new String(encryptedContents));

                        encDecTextView.setText(new String(encryptedContents));

                        fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        fileOutputStream.write((encryptedContents));
                        fileOutputStream.flush();
                    } catch (IOException e) {
                        Timber.e(e);
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                Timber.e(e);
                            }
                        }

                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException e) {
                                Timber.e(e);
                            }
                        }
                    }
                } else {
                    try {
                        inputStream = openFileInput(filename);
                        byte[] inputBytes = new byte[inputStream.available()];
                        inputStream.read(inputBytes);

                        Timber.i("before decryption %s", new String(inputBytes));

                        byte[] decryptedStuff = CryptographicHelper.decrypt(inputBytes, keyAlias);
                        encDecTextView.setText(new String(decryptedStuff));

                        Timber.i("decrypted content %s", new String(decryptedStuff));

                        fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        fileOutputStream.write((decryptedStuff));
                        fileOutputStream.flush();
                    } catch (IOException e) {
                        Timber.e(e);
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                Timber.e(e);
                            }
                        }

                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException e) {
                                Timber.e(e);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_report_sample) {
            BasicActivity.startFragment(this, ReportFragment.TAG);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cryptographicHelper = null;
    }

    @Override
    public void performDatabaseDownload() {
        if (PermissionUtils.isPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionUtils.WRITE_EXTERNAL_STORAGE_REQUEST_CODE)) {
            try {
                AppHealthUtils.triggerDBCopying(this);
            } catch (SecurityException e) {
                Utils.showToast(this, this.getString(org.smartregister.R.string.permission_write_external_storage_rationale));
            }
        }
    }

    @Override
    public void showSyncStats() {
        DrishtiApplication.getInstance().setPassword("123".getBytes());
        Intent statsActivityIntent = new Intent(this, StatsActivity.class);
        this.startActivity(statsActivityIntent);
    }
}
