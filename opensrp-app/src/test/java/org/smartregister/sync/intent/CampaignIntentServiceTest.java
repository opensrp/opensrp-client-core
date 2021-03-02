package org.smartregister.sync.intent;

import android.content.Intent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.service.HTTPAgent;

/**
 * Created by Vincent Karuri on 02/03/2021
 */
public class CampaignIntentServiceTest extends BaseUnitTest {

    @Captor
    private ArgumentCaptor<Intent> intentArgumentCaptor;

    private CampaignIntentService campaignIntentService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        campaignIntentService = Mockito.spy(new CampaignIntentService());
        Mockito.doNothing().when(campaignIntentService).sendBroadcast(ArgumentMatchers.any(Intent.class));
    }

    // TODO: complete this test
    @Test
    public void testOnHandleIntentShouldShowErrorMessageIfFetchFails() throws Exception {
        Context openSRPContext = CoreLibrary.getInstance().context();
        openSRPContext.allSharedPreferences().savePreference(AllConstants.DRISHTI_BASE_URL, "http//:dummy-url");

        HTTPAgent httpAgent = Mockito.mock(HTTPAgent.class);
        Mockito.doReturn(new Response<String>(ResponseStatus.failure, null)).when(httpAgent).fetch(ArgumentMatchers.anyString());
        ReflectionHelpers.setField(openSRPContext, "httpAgent", httpAgent);

        Whitebox.invokeMethod(campaignIntentService, "onHandleIntent", null);

        Mockito.verify(campaignIntentService).sendBroadcast(intentArgumentCaptor.capture());
        Assert.assertEquals(FetchStatus.nothingFetched,
                intentArgumentCaptor.getValue()
                        .getSerializableExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS));
    }
}