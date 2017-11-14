package org.smartregister.util;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.SyncStatus;
import org.smartregister.sync.CloudantDataHandler;

/**
 * Created by kaderchowdhury on 14/11/17.
 */
@PrepareForTest({CoreLibrary.class, CloudantDataHandler.class})
public class FormUtilsTest extends BaseUnitTest {

    FormUtils formUtils;

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    @Mock
    CloudantDataHandler cloudantDataHandler;
    @Mock
    CoreLibrary coreLibrary;
    @Mock
    Context context;
    @Mock
    android.content.Context context_;
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.mockStatic(CloudantDataHandler.class);
        PowerMockito.when(CloudantDataHandler.getInstance(Mockito.any(android.content.Context.class))).thenReturn(cloudantDataHandler);
//        android.content.Context ctx = context.applicationContext();
        formUtils = FormUtils.getInstance(context_);
    }

    @Test
    public void assertWithEntityIdReturnsFormSubmissionBuilder(){
        FormSubmissionBuilder builder= new FormSubmissionBuilder();
        Assert.assertNotNull(builder.withEntityId("baseEntityId"));
    }
    @Test
    public void assertWithSyncStatusReturnsFormSubmissionBuilder(){
        FormSubmissionBuilder builder= new FormSubmissionBuilder();
        SyncStatus syncStatus = null;
        Assert.assertNotNull(builder.withSyncStatus(syncStatus));
    }

    @Test
    public void assertConstructorInitializationNotNull() throws Exception {
        Assert.assertNotNull(new FormUtils(context_));
    }
}
