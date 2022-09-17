package org.smartregister.task;

import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.AppExecutors;

/**
 * Created by ndegwamartin on 26/06/2018.
 */

public class SaveTeamLocationsTask {

    public void execute() {
        AppExecutors appExecutors = new AppExecutors();
        appExecutors.diskIO().execute(() -> LocationHelper.getInstance().locationIdsFromHierarchy());
    }
}