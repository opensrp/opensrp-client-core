package org.smartregister.job;

import org.junit.Test;
import org.smartregister.BaseUnitTest;

import static org.junit.Assert.*;

/**
 * Created by Vincent Karuri on 17/11/2020
 */
public class ImageUploadServiceJobTest extends BaseUnitTest {

    @Test
    public void testOnRunJobShouldStartImageUploadService() {
        ImageUploadServiceJob.scheduleJobImmediately(ImageUploadServiceJob.TAG);
    }
}