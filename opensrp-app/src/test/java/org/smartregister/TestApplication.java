package org.smartregister;

import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import static org.mockito.Mockito.mock;

/**
 * Created by samuelgithengi on 12/30/19.
 */
public class TestApplication extends DrishtiApplication {

    @Override
    public void onCreate() {
        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        CoreLibrary.init(context);

        setTheme(R.style.Theme_AppCompat); //or just R.style.Theme_AppCompat
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
}
