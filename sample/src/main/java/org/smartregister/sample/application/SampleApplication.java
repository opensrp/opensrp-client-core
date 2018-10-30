package org.smartregister.sample.application;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.view.activity.DrishtiApplication;

/**
 * Created by keyman on 14/08/2017.
 */
public class SampleApplication extends DrishtiApplication {


    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());

        //Initialize Modules
        CoreLibrary.init(context,null);
    }

    public static synchronized SampleApplication getInstance() {
        return (SampleApplication) mInstance;
    }


    @Override
    public void logoutCurrentUser() {

    }


}
