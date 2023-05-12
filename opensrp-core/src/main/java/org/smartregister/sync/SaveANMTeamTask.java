package org.smartregister.sync;

import android.os.AsyncTask;

import org.smartregister.repository.AllSettings;

import timber.log.Timber;

/**
 * Created by samuelgithengi on 1/2/18.
 */

public class SaveANMTeamTask extends AsyncTask<String, Void, String> {
    private final AllSettings allSettings;

    public SaveANMTeamTask(AllSettings allSettings) {
        this.allSettings = allSettings;
    }

    @Override
    protected String doInBackground(String... strings) {
        String anmTeam = strings[0];
        allSettings.saveANMTeam(anmTeam);
        return anmTeam;
    }

    @Override
    protected void onPostExecute(String s) {
        Timber.i("SaveANMTeamTask executed successfully");
        super.onPostExecute(s);
    }
}
