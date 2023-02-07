package org.smartregister;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;

import org.json.JSONObject;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.repository.Repository;
import org.smartregister.sync.P2PClassifier;
import org.smartregister.sync.intent.SyncIntentService;
import org.smartregister.view.activity.DrishtiApplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * Created by samuelgithengi on 12/30/19.
 */
public class TestApplication extends DrishtiApplication {

    private P2PClassifier<JSONObject> p2PClassifier;

    @Override
    public void onCreate() {
        mInstance = this;
        initCoreLibrary();
        setTheme(R.style.Theme_AppCompat_NoActionBar); //or just R.style.Theme_AppCompat

        // Init Job Creator
        JobManager.create(this).addJobCreator(new TestJobCreator());
    }

    public void initCoreLibrary() {
        context = Context.setInstance(new Context());
        context.updateApplicationContext(getApplicationContext());
        CoreLibrary.init(context, new TestSyncConfiguration(), 1588062490000l);
    }

    @Override
    public void logoutCurrentUser() {
    }

    @Override
    public Repository getRepository() {
        if (repository == null) {
            Repository mockRepository = mock(Repository.class);
            SQLiteDatabase mockSqLiteDatabase = mock(SQLiteDatabase.class);
            doReturn(mockSqLiteDatabase).when(mockRepository).getReadableDatabase();
            doReturn(1).when(mockSqLiteDatabase).getVersion();
            repository = mockRepository;
        }
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

    public static TestApplication getInstance() {
        return (TestApplication) mInstance;
    }
}
