package org.smartregister.sync;

import android.os.AsyncTask;

import org.smartregister.repository.AllSettings;

import timber.log.Timber;

/**
 * Created by Dimas Ciputra on 3/24/15.
 */
public class SaveUserInfoTask extends AsyncTask<String, Void, String> {
    private final AllSettings allSettings;

    public SaveUserInfoTask(AllSettings allSettings) {
        this.allSettings = allSettings;
    }

    @Override
    protected String doInBackground(String... strings) {
        allSettings.saveUserInformation(strings[0]);
        return strings[0];
    }

    @Override
    protected void onPostExecute(String s) {
        Timber.i("SaveUserInfoTask executed successfully");
        super.onPostExecute(s);
    }
}
