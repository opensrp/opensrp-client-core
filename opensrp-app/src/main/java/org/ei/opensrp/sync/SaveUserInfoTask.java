package org.ei.opensrp.sync;

import org.ei.opensrp.repository.AllSettings;
import org.ei.opensrp.util.Log;
import org.ei.opensrp.view.BackgroundAction;
import org.ei.opensrp.view.LockingBackgroundTask;
import org.ei.opensrp.view.ProgressIndicator;

/**
 * Created by Dimas Ciputra on 3/24/15.
 */
public class SaveUserInfoTask {

    private LockingBackgroundTask lockingBackgroundTask;
    private AllSettings allSettings;

    public SaveUserInfoTask(AllSettings allSettings) {
        this.allSettings = allSettings;
        lockingBackgroundTask = new LockingBackgroundTask(new ProgressIndicator() {
            @Override
            public void setVisible() {
            }

            @Override
            public void setInvisible() {
                Log.logInfo("Successfully saved User information");
            }
        });
    }

    public void save(final String userInfo) {
        lockingBackgroundTask.doActionInBackground(new BackgroundAction<Object>() {
            @Override
            public Object actionToDoInBackgroundThread() {
                allSettings.saveUserInformation(userInfo);
                return userInfo;
            }

            @Override
            public void postExecuteInUIThread(Object result) {

            }
        });
    }
}
