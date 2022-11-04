package org.smartregister.service.intentservices;

import org.junit.Assert;

import org.junit.Test;
import org.smartregister.BaseUnitTest;

/**
 * Created by kaderchowdhury on 12/11/17.
 */

public class ReplicationIntentServiceTest extends BaseUnitTest {

    @Test
    public void assertReplicationIntentServiceInitializationTest() {
        ReplicationIntentService replicationIntentService = new ReplicationIntentService();
        Assert.assertNotNull(replicationIntentService);
        replicationIntentService.onHandleIntent(null);
    }

    @Test
    public void assertReplicationIntentServiceInitializationTest2() {
        ReplicationIntentService replicationIntentService = new ReplicationIntentService("service_name");
        Assert.assertNotNull(replicationIntentService);
        replicationIntentService.onHandleIntent(null);
    }

}
