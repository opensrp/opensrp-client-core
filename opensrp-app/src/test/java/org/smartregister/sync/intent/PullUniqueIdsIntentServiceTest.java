package org.smartregister.sync.intent;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.AllConstants;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.service.HTTPAgent;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * Created by Richard Kareko on 10/27/20.
 */

public class PullUniqueIdsIntentServiceTest extends BaseUnitTest {

    @Mock
    private UniqueIdRepository uniqueIdRepo;

    @Mock
    private SyncConfiguration syncConfiguration;

    @Mock
    private HTTPAgent httpAgent;

    @Captor
    private ArgumentCaptor<List<String>> listArgumentCaptor;

    private PullUniqueIdsIntentService pullUniqueIdsIntentService;

    private String identifiers = "{\n" +
            "    \"identifiers\": [\n" +
            "        \"1780900-5\",\n" +
            "        \"1780901-3\"\n" +
            "    ]\n" +
            "}\n";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(CoreLibrary.getInstance(), "syncConfiguration", syncConfiguration);
        CoreLibrary.getInstance().context().allSharedPreferences().savePreference(AllConstants.DRISHTI_BASE_URL, "https://sample-stage.smartregister.org/opensrp");
        pullUniqueIdsIntentService = Mockito.spy(PullUniqueIdsIntentService.class);
        Whitebox.setInternalState(pullUniqueIdsIntentService, "mBase", RuntimeEnvironment.application);
        Whitebox.setInternalState(pullUniqueIdsIntentService, "uniqueIdRepo", uniqueIdRepo);
        Mockito.doReturn(httpAgent).when(pullUniqueIdsIntentService).getHttpAgent();
    }

    @Test
    public void testParseResponse() throws Exception {

        JSONObject identifiersJson = new JSONObject(identifiers);
        Whitebox.invokeMethod(pullUniqueIdsIntentService, "parseResponse", identifiersJson);

        verify(uniqueIdRepo).bulkInsertOpenmrsIds(listArgumentCaptor.capture());
        List<String> actualIdentifierList = listArgumentCaptor.getAllValues().get(0);
        assertNotNull(actualIdentifierList);
        assertEquals(2, actualIdentifierList.size());
        assertTrue(actualIdentifierList.contains("1780900-5"));
        assertTrue(actualIdentifierList.contains("1780901-3"));
    }

    @Test
    public void onHandleIntent() {
        //TODO implement this
    }

}
