package org.smartregister;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;

import org.json.JSONObject;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.repository.Repository;
import org.smartregister.sync.P2PClassifier;
import org.smartregister.sync.intent.SyncIntentService;
import org.smartregister.view.activity.DrishtiApplication;

import static org.mockito.Mockito.mock;

/**
 * Created by samuelgithengi on 12/30/19.
 */
public class TestApplication extends DrishtiApplication {

    private P2PClassifier<JSONObject> p2PClassifier;

    @Override
    public void onCreate() {
        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        CoreLibrary.init(context, new TestSyncConfiguration(), 1588062490000l);

        setTheme(R.style.Theme_AppCompat_NoActionBar); //or just R.style.Theme_AppCompat

        // Init Job Creator
        JobManager.create(this).addJobCreator(new TestJobCreator());
    }

    @Override
    public void logoutCurrentUser() {
    }

    @Override
    public Repository getRepository() {
        if (repository == null)
            repository = mock(Repository.class);
        return repository;
    }


    @Override
    public void onTerminate() {//do nothing
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public P2PClassifier<JSONObject> getP2PClassifier() {
        return p2PClassifier;
    }

    public void setP2PClassifier(P2PClassifier<JSONObject> p2PClassifier) {
        this.p2PClassifier = p2PClassifier;
    }

    static class TestJobCreator implements JobCreator {

        @Nullable
        @Override
        public Job create(@NonNull String tag) {
            switch (tag) {
                case SyncServiceJob.TAG:
                    return new SyncServiceJob(SyncIntentService.class);

                default:
                    break;
            }

            return null;
        }

    }
}
