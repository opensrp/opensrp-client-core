package org.smartregister.task;

import org.smartregister.location.helper.LocationHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ndegwamartin on 26/06/2018.
 */

public class SaveTeamLocationsTask {

    public void execute() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> LocationHelper.getInstance().locationIdsFromHierarchy());
    }
}