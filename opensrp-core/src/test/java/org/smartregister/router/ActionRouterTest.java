package org.smartregister.router;

import org.ei.drishti.dto.Action;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.service.AlertService;
import org.smartregister.service.MotherService;

/**
 * Created by kaderchowdhury on 12/11/17.
 */
@PrepareForTest({CoreLibrary.class})
public class ActionRouterTest extends BaseUnitTest {

    @Mock
    private Context context;
    @Mock
    private AlertService alertService;
    @Mock
    private MotherService motherService;
    @Mock
    private CoreLibrary coreLibrary;
    private ActionRouter actionRouter;

    @Before
    public void setUp() throws Exception {
        try (MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
        }
        CoreLibrary.init(context);
        Mockito.when(coreLibrary.context()).thenReturn(context);
        Mockito.when(context.alertService()).thenReturn(alertService);
        Mockito.when(context.motherService()).thenReturn(motherService);
        Mockito.doNothing().when(motherService).close(Mockito.anyString(), Mockito.anyString());

        actionRouter = new ActionRouter();
        Assert.assertNotNull(actionRouter);
    }

    @Test
    public void assertDirectAlertActionTest() throws Exception {
        Action action = new Action("", "", "createAlert", null, "", false, null);
        actionRouter.directAlertAction(action);//void nothing to test
        Assert.assertNotNull(actionRouter);
    }

    @Test
    public void assertDirectMotherActionTest() throws Exception {
        Action action = new Action("", "", "close", null, "", false, null);
//        actionRouter.directMotherAction(action);//void nothing to test
        Assert.assertNotNull(actionRouter);
    }

}
