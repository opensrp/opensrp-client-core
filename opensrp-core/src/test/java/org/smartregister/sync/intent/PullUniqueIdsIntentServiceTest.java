package org.smartregister.sync.intent;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import androidx.test.core.app.ApplicationProvider;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
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

public class PullUniqueIdsIntentServiceTest extends BaseRobolectricUnitTest {

    @Mock
    private UniqueIdRepository uniqueIdRepo;

    @Mock
    private SyncConfiguration syncConfiguration;

    @Mock
    private HTTPAgent httpAgent;

    @Captor
    private ArgumentCaptor<List<String>> listArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    private PullUniqueIdsIntentService pullUniqueIdsIntentService;

    private String identifiersJsonString = "{\n" +
            "    \"identifiers\": [\n" +
            "        \"1780900-5\",\n" +
            "        \"1780901-3\"\n" +
            "    ]\n" +
            "}\n";

    @Before
    public void setUp() {
        
        Whitebox.setInternalState(CoreLibrary.getInstance(), "syncConfiguration", syncConfiguration);
        CoreLibrary.getInstance().context().allSharedPreferences().savePreference(AllConstants.DRISHTI_BASE_URL, "https://sample-stage.smartregister.org/opensrp");
        pullUniqueIdsIntentService = Mockito.spy(PullUniqueIdsIntentService.class);
        Whitebox.setInternalState(pullUniqueIdsIntentService, "mBase", ApplicationProvider.getApplicationContext());
        Whitebox.setInternalState(pullUniqueIdsIntentService, "uniqueIdRepo", uniqueIdRepo);
        Mockito.doReturn(httpAgent).when(pullUniqueIdsIntentService).getHttpAgent();
    }

    @Test
    public void testParseResponse() throws Exception {

        JSONObject identifiersJson = new JSONObject(identifiersJsonString);
        Whitebox.invokeMethod(pullUniqueIdsIntentService, "parseResponse", identifiersJson);

        verify(uniqueIdRepo).bulkInsertOpenmrsIds(listArgumentCaptor.capture());
        List<String> actualIdentifierList = listArgumentCaptor.getValue();
        assertNotNull(actualIdentifierList);
        assertEquals(2, actualIdentifierList.size());
        assertTrue(actualIdentifierList.contains("1780900-5"));
        assertTrue(actualIdentifierList.contains("1780901-3"));
    }

    @Test
    public void testFetchOpenMRSIds() throws Exception {

        int source = 2;
        int numberTogenerate = 2;

        Mockito.doReturn(new Response<>(ResponseStatus.success, identifiersJsonString))
                .when(httpAgent).fetch(stringArgumentCaptor.capture());

        JSONObject actualIdentifiersJson = Whitebox.invokeMethod(pullUniqueIdsIntentService, "fetchOpenMRSIds", source, numberTogenerate);
        assertEquals("https://sample-stage.smartregister.org/opensrp/uniqueids/get?source=2&numberToGenerate=2", stringArgumentCaptor.getValue());
        assertNotNull(actualIdentifiersJson);
        assertEquals(new JSONArray("[\"1780900-5\",\"1780901-3\"]"), actualIdentifiersJson.get("identifiers"));
    }

}
