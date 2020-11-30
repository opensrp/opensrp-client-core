package org.smartregister.configuration;


import android.content.Context;
import android.view.MenuItem;

import org.smartregister.commonregistry.CommonPersonObjectClient;

public interface BaseMemberProfileOptions {

    int getMenuLayoutId();

    boolean onMenuOptionsItemSelected(MenuItem menuItem, Context context, CommonPersonObjectClient client);

    Class<? extends ConfigurableMemberProfileRowDataProvider> getMemberProfileDataProvider();

}
