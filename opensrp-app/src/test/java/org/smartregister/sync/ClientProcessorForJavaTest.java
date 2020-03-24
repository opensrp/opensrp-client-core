package org.smartregister.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.db.Obs;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.Column;
import org.smartregister.domain.jsonmapping.ColumnType;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.shadows.ShadowAssetHandler;
import org.smartregister.util.AssetHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.smartregister.sync.ClientProcessorForJava.JSON_ARRAY;


public class ClientProcessorForJavaTest extends BaseUnitTest {
    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private org.smartregister.Context opensrpContext;


    @Mock
    private Context context;

    @Captor
    private ArgumentCaptor<String> closeCaseArgumentCaptor;

    @Captor
    private ArgumentCaptor detailsRepositoryAddArgumentCaptor;

    private ClientProcessorForJava clientProcessor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        clientProcessor = new ClientProcessorForJava(context);
    }


    @Test
    public void testGetFormattedValueDate() throws Exception {
        Column column = new Column();
        column.dataType = ColumnType.Date;
        column.saveFormat = "yyyy-MM-dd";
        column.sourceFormat = "dd-MM-yyyy";
        String columnValue = "16-04-2019";

        String res = Whitebox.invokeMethod(clientProcessor, "getFormattedValue", column, columnValue);

        assertEquals(res, "2019-04-16");
    }

    @Test
    public void testGetFormattedValueString() throws Exception {
        clientProcessor = new ClientProcessorForJava(context);

        Column column = new Column();
        column.dataType = ColumnType.String;
        column.saveFormat = "Sheila is %s";
        String columnValue = "smart";

        String res = Whitebox.invokeMethod(clientProcessor, "getFormattedValue", column, columnValue);

        assertEquals(res, "Sheila is smart");
    }


    @Test
    public void testProcessEventMarksItSaved() throws Exception {
        ClientProcessorForJava clientProcessorForJava = Mockito.spy(new ClientProcessorForJava(context));
        Event mockEvent = Mockito.mock(Event.class);
        clientProcessorForJava.processEvent(mockEvent, null, null);

        Mockito.verify(clientProcessorForJava).completeProcessing(mockEvent);
    }

    @Test
    public void testGetValuesStrShouldGetCorrectlyFormattedString() throws Exception {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);
        List<String> values = new ArrayList<>();
        Obs obs = new Obs();
        obs.withsaveObsAsArray(true);
        String valStr = Whitebox.invokeMethod(clientProcessor, "getValuesStr", obs, values, JSON_ARRAY);
        assertNull(valStr);

        values.add("val1");
        values.add("val2");
        values.add("val3");
        valStr = Whitebox.invokeMethod(clientProcessor, "getValuesStr", obs, values, null);
        assertEquals("[\"val1\",\"val2\",\"val3\"]", valStr);

        obs.setSaveObsAsArray(false);
        valStr = Whitebox.invokeMethod(clientProcessor, "getValuesStr", obs, values, JSON_ARRAY);
        assertEquals("[\"val1\",\"val2\",\"val3\"]", valStr);

        valStr = Whitebox.invokeMethod(clientProcessor, "getValuesStr", obs, values, null);
        assertEquals("val1", valStr);
    }

    @Test
    public void testProcessEventUsingMiniProcessorShouldCallMiniProcessorProcessEventClient() throws Exception {
        MiniClientProcessorForJava miniClientProcessorForJava = Mockito.mock(MiniClientProcessorForJava.class);
        String eventType = "Custom Test Event";
        HashSet<String> supportedEventTypes = new HashSet<>();
        supportedEventTypes.add(eventType);
        Mockito.doReturn(supportedEventTypes).when(miniClientProcessorForJava).getEventTypes();

        String baseEntityId = "bei";
        EventClient eventClient = new EventClient(new Event(), new Client(baseEntityId));

        ClientProcessorForJava clientProcessorForJava = Mockito.spy(new ClientProcessorForJava(context));
        clientProcessorForJava.addMiniProcessors(miniClientProcessorForJava);

        ClientClassification clientClassification = new ClientClassification();
        clientProcessorForJava.processEventUsingMiniProcessor(clientClassification, eventClient, eventType);

        Mockito.verify(miniClientProcessorForJava, Mockito.times(1)).processEventClient(Mockito.eq(eventClient), ArgumentMatchers.<List<Event>>any(), Mockito.eq(clientClassification));
    }

    @Test
    public void testGetClientAttributesShouldReturnRequiredValues() throws Exception {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("national_id", "3434-34");
        attributes.put("drivers-license", "DL-324");
        Client client = new Client("123-23");
        client.setAttributes(attributes);
        Map<String, Object> result = Whitebox.invokeMethod(clientProcessor, "getClientAttributes", client);
        assertEquals(attributes, result);
    }

    @Test
    public void testGetGenderShouldReturnCorrectValue() throws Exception {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);
        Client client = new Client("123-23");
        client.setGender("Female");
        Map<String, String> resultMap = Whitebox.invokeMethod(clientProcessor, "getGender", client);
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("gender", "Female");
        assertEquals(expectedMap, resultMap);
    }

    @Test
    public void testUpdateIdenitifierShouldRemoveHyphenFromOpenmrsId() throws Exception {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);
        ContentValues contentValues = new ContentValues();
        contentValues.put("zeir_id", "23-23");
        Whitebox.setInternalState(clientProcessor, "openmrsGenIds", new String[]{"zeir_id"});
        Whitebox.invokeMethod(clientProcessor, "updateIdenitifier", contentValues);
        assertEquals("2323", contentValues.get("zeir_id"));
    }

    @Test
    public void testCloseCaseShouldReturnFalseIfCloseCaseIsEmpty() {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);
        assertFalse(clientProcessor.closeCase(new Client("1233-2"), new ArrayList<>()));
    }

    @Test
    public void testCloseCaseShouldPassCorrectValuesToCloseCase() {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);
        CommonRepository commonRepository = Mockito.mock(CommonRepository.class);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(opensrpContext);
        PowerMockito.when(opensrpContext.commonrepository("child")).thenReturn(commonRepository);
        assertTrue(clientProcessor.closeCase(new Client("1233-2"), Arrays.asList("child")));
        Mockito.verify(commonRepository).closeCase(closeCaseArgumentCaptor.capture(), closeCaseArgumentCaptor.capture());
        assertEquals("1233-2", closeCaseArgumentCaptor.getAllValues().get(0));
        assertEquals("child", closeCaseArgumentCaptor.getAllValues().get(1));
    }

    @Test
    public void testAddContentValuesToDetailsTableShouldNotPopulateIfUpdateClientDetailsIsFalse() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        SyncConfiguration syncConfiguration = Mockito.mock(SyncConfiguration.class);
        Mockito.when(coreLibrary.context()).thenReturn(opensrpContext);
        Mockito.when(coreLibrary.getSyncConfiguration()).thenReturn(syncConfiguration);
        DetailsRepository detailsRepository = Mockito.mock(DetailsRepository.class);
        Mockito.when(opensrpContext.detailsRepository()).thenReturn(detailsRepository);
        Mockito.when(syncConfiguration.updateClientDetailsTable()).thenReturn(false);
        clientProcessor.addContentValuesToDetailsTable(new ContentValues(), new Date().getTime());
        Mockito.verify(detailsRepository, Mockito.never()).add(anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    public void testAddContentValuesToDetailsTableShouldPopulateIfUpdateClientDetailsIsTrue() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        SyncConfiguration syncConfiguration = Mockito.mock(SyncConfiguration.class);
        DetailsRepository detailsRepository = Mockito.mock(DetailsRepository.class);
        Mockito.when(coreLibrary.getSyncConfiguration()).thenReturn(syncConfiguration);
        Mockito.when(coreLibrary.context()).thenReturn(opensrpContext);
        Mockito.when(opensrpContext.detailsRepository()).thenReturn(detailsRepository);
        Mockito.when(syncConfiguration.updateClientDetailsTable()).thenReturn(true);
        ContentValues contentValues = new ContentValues();
        contentValues.put("base_entity_id", "2342-234");

        Long timestamp = new Date().getTime();
        clientProcessor.addContentValuesToDetailsTable(contentValues, timestamp);
        Mockito.verify(detailsRepository).add((String) detailsRepositoryAddArgumentCaptor.capture(), (String) detailsRepositoryAddArgumentCaptor.capture(),
                (String) detailsRepositoryAddArgumentCaptor.capture(), (Long) detailsRepositoryAddArgumentCaptor.capture());

        assertEquals("2342-234", detailsRepositoryAddArgumentCaptor.getAllValues().get(0));

        assertEquals("base_entity_id", detailsRepositoryAddArgumentCaptor.getAllValues().get(1));

        assertEquals(contentValues.getAsString("base_entity_id"), detailsRepositoryAddArgumentCaptor.getAllValues().get(2));

        assertEquals(timestamp, detailsRepositoryAddArgumentCaptor.getAllValues().get(3));

    }


    @Test
    public void testGetColumnMappingsShouldReturnTheCorrectTable() throws IOException {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        Mockito.when(coreLibrary.getEcClientFieldsFile()).thenReturn("ec_client_fields.json");
        AssetManager assetManager = Mockito.mock(AssetManager.class);
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        Mockito.when(assetManager.open("ec_client_fields.json")).thenReturn(new ByteArrayInputStream(ClientData.ec_client_fields_json.getBytes()));
        Table ecHousehold = clientProcessor.getColumnMappings("ec_household");
        assertNotNull(ecHousehold);
        assertEquals(ecHousehold.name, "ec_household");
    }

    @Test
    public void testGetColumnMappingsShouldReturnNullIfFileIsNotRead() throws IOException {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        Mockito.when(coreLibrary.getEcClientFieldsFile()).thenReturn("ec_client_fields.json");
        AssetManager assetManager = Mockito.mock(AssetManager.class);
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        Mockito.when(assetManager.open("ec_client_fields.json")).thenReturn(null);
        Table ecHousehold = clientProcessor.getColumnMappings("ec_household");
        assertNull(ecHousehold);
    }

    @Test
    public void testUpdateClientDetailsTableShouldNotSaveClientDetailsIdConfigIsFalse() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        DetailsRepository detailsRepository = Mockito.mock(DetailsRepository.class);
        SyncConfiguration syncConfiguration = Mockito.mock(SyncConfiguration.class);
        Mockito.when(coreLibrary.getSyncConfiguration()).thenReturn(syncConfiguration);
        Mockito.when(syncConfiguration.updateClientDetailsTable()).thenReturn(false);
        Mockito.when(coreLibrary.context()).thenReturn(opensrpContext);
        Mockito.when(opensrpContext.detailsRepository()).thenReturn(detailsRepository);
        Event event = new Event();
        clientProcessor.updateClientDetailsTable(event, new Client("2323-2"));
        assertTrue(Boolean.valueOf(event.getDetails().get(ClientProcessorForJava.detailsUpdated)));
        Mockito.verify(detailsRepository, Mockito.never()).add(anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    public void testUpdateClientDetailsTableShouldSaveClientDetailsIdConfigIsTrue() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        DetailsRepository detailsRepository = Mockito.mock(DetailsRepository.class);

        SyncConfiguration syncConfiguration = Mockito.mock(SyncConfiguration.class);
        Mockito.when(coreLibrary.getSyncConfiguration()).thenReturn(syncConfiguration);
        Mockito.when(syncConfiguration.updateClientDetailsTable()).thenReturn(true);

        Mockito.when(coreLibrary.context()).thenReturn(opensrpContext);
        Mockito.when(opensrpContext.detailsRepository()).thenReturn(detailsRepository);

        DateTime dateTime = new DateTime();
        Event event = new Event();
        event.setEventDate(dateTime);
        Obs obs = new Obs();
        obs.setFieldCode("12212AAAAA");
        obs.setFormSubmissionField("reminders");
        Object value = (Object) "no";
        obs.setValues(Arrays.asList(value));
        obs.setHumanReadableValue(new ArrayList<>());
        obs.setFieldDataType("text");
        event.addObs(obs);
        Client client = new Client("234-13");
        client.setGender("Female");
        Map<String, Object> attribsMap = new HashMap<>();
        attribsMap.put("national_id", "423");
        attribsMap.put("phone_number", "0707070");
        client.setAttributes(attribsMap);
        clientProcessor.updateClientDetailsTable(event, client);
        assertTrue(Boolean.valueOf(event.getDetails().get(ClientProcessorForJava.detailsUpdated)));
        Mockito.verify(detailsRepository, Mockito.atLeast(4)).add(anyString(), anyString(), anyString(), anyLong());
    }

    @Config(shadows = {ShadowAssetHandler.class})
    @Test
    public void processClientShouldCallProcessEvent() throws Exception {
        String baseEntityId = "998098s0kldsckljsd";
        Client client = new Client(baseEntityId);
        Event event = new Event(baseEntityId, "eventId", "Birth Reg", new DateTime(), "client", "anm", "location-id", "form-submission-id");
        List<EventClient> eventClients = new ArrayList<>();
        eventClients.add(new EventClient(event, client));

        ClientProcessorForJava clientProcessorForJava = Mockito.spy(clientProcessor);
        Mockito.doReturn(true).when(clientProcessorForJava).processEvent(Mockito.eq(event), Mockito.eq(client), Mockito.any(ClientClassification.class));

        clientProcessorForJava.processClient(eventClients);
        Mockito.verify(clientProcessorForJava).processEvent(Mockito.eq(event), Mockito.eq(client), Mockito.any(ClientClassification.class));
    }


    @Config(shadows = {ShadowAssetHandler.class})
    @Test
    public void processClientShouldCallProcessEventsUsingMiniProcessor() throws Exception {
        String baseEntityId = "998098s0kldsckljsd";
        Client client = new Client(baseEntityId);
        String birthRegEventType = "Birth Reg";
        Event event = new Event(baseEntityId, "eventId", birthRegEventType, new DateTime(), "client", "anm", "location-id", "form-submission-id");
        List<EventClient> eventClients = new ArrayList<>();
        EventClient eventClient = new EventClient(event, client);
        eventClients.add(eventClient);

        ClientProcessorForJava clientProcessorForJava = Mockito.spy(clientProcessor);
        HashSet<String> eventTypes = new HashSet<String>();
        eventTypes.add(birthRegEventType);

        MiniClientProcessorForJava mockMiniProcessor = Mockito.mock(MiniClientProcessorForJava.class);
        Mockito.doReturn(eventTypes).when(mockMiniProcessor).getEventTypes();
        clientProcessorForJava.addMiniProcessors(mockMiniProcessor);
        clientProcessorForJava.processClient(eventClients);
        Mockito.verify(clientProcessorForJava).processEventUsingMiniProcessor(Mockito.any(ClientClassification.class), Mockito.eq(eventClient), Mockito.eq(birthRegEventType));
    }



    @Config(shadows = {ShadowAssetHandler.class})
    @Test
    public void processEventShouldCallCompleteProcessingEventAndReturnFalse() throws Exception {
        String baseEntityId = "998098s0kldsckljsd";
        String birthRegEventType = "Birth Reg";
        Event event = new Event(baseEntityId, "eventId", birthRegEventType, new DateTime(), "client", "anm", "location-id", "form-submission-id");

        ClientProcessorForJava clientProcessorForJava = Mockito.spy(clientProcessor);

        assertFalse(clientProcessorForJava.processEvent(event, null, AssetHandler.assetJsonToJava(new HashMap<>(), RuntimeEnvironment.systemContext, "ec_client_classification.json", ClientClassification.class)));
        Mockito.verify(clientProcessorForJava).completeProcessing(Mockito.eq(event));
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
        clientProcessor = null;
    }
}
