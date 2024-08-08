package org.smartregister.sample.interactor;

import static org.smartregister.job.SyncSettingsServiceWorker.*;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import org.smartregister.domain.Setting;
import org.smartregister.job.DuplicateCleanerWorker;
import org.smartregister.job.SyncSettingsServiceWorker;
import org.smartregister.login.interactor.BaseLoginInteractor;
import org.smartregister.sync.intent.SettingsSyncIntentService;
import org.smartregister.view.contract.BaseLoginContract;

import java.util.concurrent.TimeUnit;

/**
 * Created by ndegwamartin on 08/05/2020.
 */
public class LoginInteractor extends BaseLoginInteractor {

    public LoginInteractor(BaseLoginContract.Presenter loginPresenter) {
        super(loginPresenter);
    }

    @Override
    protected void scheduleJobsPeriodically() {
        //Schedule your jobs here
        WorkRequest  cleanZeirIdsWorkRequest = new PeriodicWorkRequest.Builder(DuplicateCleanerWorker.class, 15, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance(this.getApplicationContext()).enqueue(cleanZeirIdsWorkRequest);

        enqueuePeriodicSettingsSyncIntentService(getApplicationContext());
    }
}
