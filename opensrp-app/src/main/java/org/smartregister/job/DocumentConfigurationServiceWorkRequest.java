package org.smartregister.job;

import org.smartregister.sync.intent.DocumentConfigurationIntentWorker;

/**
 * Created by cozej4 on 2020-04-08.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class DocumentConfigurationServiceWorkRequest extends BaseWorkRequest {
    public static final String TAG = "DocumentConfigurationServiceJob";

    private Class<? extends DocumentConfigurationIntentWorker> serviceClass;

}
