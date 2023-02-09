package org.smartregister;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import org.smartregister.repository.AllSharedPreferences;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 18-08-2020.
 */
public class TestP2pApplication extends TestApplication {

    @Override
    public void onCreate() {
        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());

        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(context.applicationContext()));
        allSharedPreferences.updateANMUserName("demo");
        CoreLibrary.destroyInstance();
        CoreLibrary.init(context, new TestSyncConfiguration(), 1588062490000l, new P2POptions(true));
    }
}
