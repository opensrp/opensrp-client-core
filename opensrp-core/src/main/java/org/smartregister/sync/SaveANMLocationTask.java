package org.smartregister.sync;

import android.os.AsyncTask;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.repository.AllSettings;

import timber.log.Timber;

public class SaveANMLocationTask extends AsyncTask<String, Void, String> {
    private final AllSettings allSettings;

    public SaveANMLocationTask(AllSettings allSettings) {
        this.allSettings = allSettings;
    }

    @Override
    protected String doInBackground(String... anmLocation) {
        if (StringUtils.isNotBlank(anmLocation[0])) {
            allSettings.saveANMLocation(anmLocation[0]);
        } else {
            Timber.e("Unable to save ANM Location. String is NULL or EMPTY");
        }
        return anmLocation[0];
    }

    @Override
    protected void onPostExecute(String anmLocation) {
        Timber.i("Successfully executed SaveANMLocationTask for: %s", anmLocation);
        super.onPostExecute(anmLocation);
    }
}
