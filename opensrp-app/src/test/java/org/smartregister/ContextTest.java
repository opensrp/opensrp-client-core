package org.smartregister;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.repository.AllAlerts;
import org.smartregister.repository.CampaignRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.ImageRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.service.ANMService;
import org.smartregister.service.HTTPAgent;
import org.smartregister.service.UserService;
import org.smartregister.view.controller.ANMController;
import org.smartregister.view.controller.ANMLocationController;

public class ContextTest extends BaseUnitTest {

    private Context context;

    @Before
    public void setUp() throws Exception {
        context = Context.getInstance();
    }

    @Test
    public void testUserService() {
        UserService userService = context.userService();
        Assert.assertNotNull(userService);
    }

    @Test
    public void testImageRepository() {
        ImageRepository imageRepository = context.imageRepository();
        Assert.assertNotNull(imageRepository);
    }

    @Test
    public void testAllAlerts() {
        AllAlerts allAlerts = context.allAlerts();
        Assert.assertNotNull(allAlerts);
    }

    @Test
    public void testHttpAgent() {
        HTTPAgent httpAgent = context.httpAgent();
        Assert.assertNotNull(httpAgent);
    }

    @Test
    public void testAnmService() {
        ANMService anmService = context.anmService();
        Assert.assertNotNull(anmService);
    }

    @Test
    public void testConfiguration() {
        DristhiConfiguration dristhiConfiguration = context.configuration();
        Assert.assertNotNull(dristhiConfiguration);
    }

    @Test
    public void testAnmController() {
        ANMController anmController = context.anmController();
        Assert.assertNotNull(anmController);
    }

    @Test
    public void testAnmLocationController() {
        ANMLocationController anmLocationController = context.anmLocationController();
        Assert.assertNotNull(anmLocationController);
    }

    @Test
    public void testGetHttpAgent() {
        HTTPAgent httpAgent = context.getHttpAgent();
        Assert.assertNotNull(httpAgent);
    }

    @Test
    public void testGetEventClientRepository() {
        EventClientRepository eventClientRepository = context.getEventClientRepository();
        Assert.assertNotNull(eventClientRepository);
    }

    @Test
    public void testGetUniqueIdRepository() {
        UniqueIdRepository uniqueIdRepository = context.getUniqueIdRepository();
        Assert.assertNotNull(uniqueIdRepository);
    }

    @Test
    public void testGetCampaignRepository() {
        CampaignRepository campaignRepository = context.getCampaignRepository();
        Assert.assertNotNull(campaignRepository);
    }

    @Test
    public void testgetTaskRepository() {
        TaskRepository taskRepository = context.getTaskRepository();
        Assert.assertNotNull(taskRepository);
    }

}
