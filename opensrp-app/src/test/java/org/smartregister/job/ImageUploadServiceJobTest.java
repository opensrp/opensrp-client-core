package org.smartregister.job;

import android.content.ContextWrapper;
import android.content.Intent;

import org.junit.Assert;
import org.junit.Test;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowLooper;
import org.smartregister.BaseUnitTest;
import org.smartregister.TestApplication;
import org.smartregister.service.ImageUploadSyncService;

/**
 * Created by Vincent Karuri on 17/11/2020
 */
public class ImageUploadServiceJobTest extends ServiceJobTest {

    @Override
    protected String getServiceId() {
        return "org.smartregister.service.ImageUploadSyncService";
    }

    @Override
    protected BaseJob getJob() {
        return new ImageUploadServiceJob();
    }
}