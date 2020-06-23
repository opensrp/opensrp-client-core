package org.smartregister.sync.helper;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationTag;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.exception.NoHttpResponseException;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.LocationTagRepository;
import org.smartregister.repository.Repository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.smartregister.AllConstants.OPERATIONAL_AREAS;

public class LocationServiceHelperTest extends BaseRobolectricUnitTest {

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    private LocationServiceHelper locationServiceHelper = Mockito.spy(LocationServiceHelper.getInstance());

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private LocationTagRepository locationTagRepository;

    @Mock
    private HTTPAgent httpAgent;

    @Mock
    private SyncConfiguration syncConfiguration;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    private String locationJSon = "{\"id\": \"3537\", \"type\": \"Feature\", \"geometry\": {\"type\": \"MultiPolygon\", \"coordinates\": [[[[32.64555352892119, -14.15491759447286], [32.64526263744511, -14.154844278278059], [32.64536132720689, -14.154861856643318], [32.645458459831154, -14.154886337918807], [32.64555352892119, -14.15491759447286]]]]}, \"properties\": {\"name\": \"MTI_13\", \"status\": \"Active\", \"version\": 0, \"parentId\": \"2953\", \"geographicLevel\": 2}, \"serverVersion\": 1542965231622}";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        AllSharedPreferences allSharedPreferences = Mockito.spy(CoreLibrary.getInstance().context().allSharedPreferences());
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);
        Whitebox.setInternalState(locationServiceHelper, "locationTagRepository", locationTagRepository);
        Whitebox.setInternalState(locationServiceHelper, "locationRepository", locationRepository);
        Whitebox.setInternalState(locationServiceHelper, "allSharedPreferences", allSharedPreferences);
        Mockito.doReturn("anm").when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn("fb7ed5db-138d-4e6f-94d8-bc443b58dadb").when(allSharedPreferences).fetchDefaultLocalityId("anm");
        Mockito.doReturn("https://sample-stage.smartregister.org/opensrp/").when(locationServiceHelper).getFormattedBaseUrl();
        Mockito.doReturn(httpAgent).when(locationServiceHelper).getHttpAgent();
        Mockito.doReturn(syncConfiguration).when(locationServiceHelper).getSyncConfiguration();
        Mockito.doReturn("Council").when(syncConfiguration).getTopAllowedLocationLevel();
        Mockito.doReturn(Collections.singletonList("Facility")).when(syncConfiguration).getSynchronizedLocationTags();
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void fetchOpenMrsLocationsByTeamIds() throws JSONException, NoHttpResponseException {

        Mockito.doReturn(new Response<>(ResponseStatus.success,
                "[{\"locations\":[{\"display\":\"Tabata Dampo - Unified\",\"uuid\":\"fb7ed5db-138d-4e6f-94d8-bc443b58dadb\"}]," +
                        "\"team\":{\"location\":{\"display\":\"Madona - Unified\",\"uuid\":\"bcf5a36d-fb53-4de9-9813-01f1d480e3fe\"}}}]"))
                .when(httpAgent).post(Mockito.anyString(), Mockito.anyString());
        locationServiceHelper.fetchOpenMrsLocationsByTeamIds();
        Mockito.verify(locationRepository, Mockito.atLeastOnce()).addOrUpdate(Mockito.any(Location.class));
        Mockito.verify(locationTagRepository, Mockito.atLeastOnce()).addOrUpdate(Mockito.any(LocationTag.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenHttpAgentIsNull() throws JSONException, NoHttpResponseException {
        Mockito.doReturn(null).when(locationServiceHelper).getHttpAgent();
        locationServiceHelper.fetchOpenMrsLocationsByTeamIds();
        Mockito.verify(locationRepository, Mockito.atLeastOnce()).addOrUpdate(Mockito.any(Location.class));
        Mockito.verify(locationTagRepository, Mockito.atLeastOnce()).addOrUpdate(Mockito.any(LocationTag.class));
    }

    @Test(expected = NoHttpResponseException.class)
    public void shouldThrowExceptionWhenThereIsFailureInResponse() throws JSONException, NoHttpResponseException {
        Mockito.doReturn(new Response<>(ResponseStatus.failure, "error"))
                .when(httpAgent).post(Mockito.anyString(), Mockito.anyString());
        locationServiceHelper.fetchOpenMrsLocationsByTeamIds();
        Mockito.verify(locationRepository, Mockito.atLeastOnce()).addOrUpdate(Mockito.any(Location.class));
        Mockito.verify(locationTagRepository, Mockito.atLeastOnce()).addOrUpdate(Mockito.any(LocationTag.class));
    }

    @Test
    public void fetchLocationsByLevelAndTags() throws Exception {
        Mockito.doReturn(new Response<>(ResponseStatus.success,
                "[{\"locationId\":\"b949c2b5-d5f6-4a1b-ad03-e82e7abbd47c\",\"name\":\"Ebrahim Haji - Unified\"," +
                "\"parentLocation\":{\"locationId\":\"620e3393-38aa-4797-85c4-3427cc882e00\",\"name\":\"Ilala MC - Unified\"," +
                        "\"voided\":false},\"tags\":[\"Facility\"],\"voided\":false},{\"locationId\":\"bcf5a36d-fb53-4de9-9813-01f1d480e3fe\"," +
                        "\"name\":\"Madona - Unified\",\"parentLocation\":{\"locationId\":\"620e3393-38aa-4797-85c4-3427cc882e00\"," +
                        "\"name\":\"Ilala MC - Unified\",\"voided\":false},\"tags\":[\"Facility\"],\"voided\":false}]"))
                .when(httpAgent).post(Mockito.anyString(), Mockito.anyString());
        locationServiceHelper.fetchLocationsByLevelAndTags();
        Mockito.verify(locationRepository, Mockito.atLeastOnce()).addOrUpdate(Mockito.any(Location.class));
        Mockito.verify(locationTagRepository, Mockito.atLeastOnce()).addOrUpdate(Mockito.any(LocationTag.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionFetchLocationsByTagsHttpAgentIsNull() throws Exception {
        Mockito.doReturn(null).when(locationServiceHelper).getHttpAgent();
        locationServiceHelper.fetchLocationsByLevelAndTags();
        Mockito.verify(locationRepository, Mockito.atLeastOnce()).addOrUpdate(Mockito.any(Location.class));
        Mockito.verify(locationTagRepository, Mockito.atLeastOnce()).addOrUpdate(Mockito.any(LocationTag.class));
    }

    @Test(expected = NoHttpResponseException.class)
    public void shouldThrowExceptionWhenFetchLocationsByLevelAndTagsReturnsFailedResponse() throws Exception {
        Mockito.doReturn(new Response<>(ResponseStatus.failure, "error"))
                .when(httpAgent).post(Mockito.anyString(), Mockito.anyString());
        locationServiceHelper.fetchLocationsByLevelAndTags();
        Mockito.verify(locationRepository, Mockito.atLeastOnce()).addOrUpdate(Mockito.any(Location.class));
        Mockito.verify(locationTagRepository, Mockito.atLeastOnce()).addOrUpdate(Mockito.any(LocationTag.class));
    }

    @Test
    public void testSyncLocations() {
        CoreLibrary.getInstance().context().allSharedPreferences().savePreference(OPERATIONAL_AREAS, "MTI_13");
        when(locationRepository.getAllLocationIds()).thenReturn(Collections.singletonList("2953"));

        JSONArray locationsJSONArray = new JSONArray();
        locationsJSONArray.put(locationJSon);
        Mockito.doReturn(new Response<>(ResponseStatus.success,
                locationsJSONArray))
                .when(httpAgent).post(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());
        locationServiceHelper.syncLocationsStructures(true);

        String syncUrl = stringArgumentCaptor.getAllValues().get(0);
        assertEquals("https://sample-stage.smartregister.org/opensrp//rest/location/sync", syncUrl);
        String requestString = stringArgumentCaptor.getAllValues().get(1);
        assertEquals("{\"is_jurisdiction\":true,\"location_names\":[\"MTI_13\"],\"serverVersion\":0}", requestString);

        // TODO verify returned location is correct
        // TODO verify location is added/updated to db

    }
}