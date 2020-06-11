package org.smartregister.sync.helper;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.SyncConfiguration;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationTag;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.exception.NoHttpResponseException;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.LocationTagRepository;
import org.smartregister.repository.Repository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Collections;

import static org.mockito.Mockito.when;

public class LocationServiceHelperTest extends BaseUnitTest {

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

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);
        Whitebox.setInternalState(locationServiceHelper, "locationTagRepository", locationTagRepository);
        Whitebox.setInternalState(locationServiceHelper, "locationRepository", locationRepository);
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
}