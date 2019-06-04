package org.smartregister.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.util.LangUtils;
import org.smartregister.view.activity.LanguageActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends LanguageActivity {
    private Spinner languageSpinner;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activity = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        SmartRegisterQueryBuilder srqb = new SmartRegisterQueryBuilder();
        String query = srqb.searchQueryFts("ec_household",new String[]{"ec_woman","ec_child","ec_member"},"date_removed IS NULL ","ali","",20,0);
        System.out.println(query);

        // lang spinner
        languageSpinner = findViewById(R.id.lang_spinner);
        List<String> langArray = new ArrayList<>();
        langArray.add("English");
        langArray.add("Français");
        langArray.add("عربى");
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
                        case "عربى":
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
        }

        return super.onOptionsItemSelected(item);
    }
}
