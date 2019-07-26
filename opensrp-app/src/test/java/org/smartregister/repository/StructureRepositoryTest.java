package org.smartregister.repository;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationTest;
import org.smartregister.util.DateTimeTypeConverter;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.domain.LocationTest.stripTimezone;
import static org.smartregister.repository.StructureRepository.STRUCTURE_TABLE;

/**
 * Created by samuelgithengi on 11/26/18.
 */

public class StructureRepositoryTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private StructureRepository structureRepository;

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Captor
    private ArgumentCaptor<ContentValues> contentValuesArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    private ArgumentCaptor<String[]> argsCaptor;

    private String locationJson = LocationTest.structureJson;

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();


    @Before
    public void setUp() {
        structureRepository = new StructureRepository(repository);
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void testAddOrUpdateShouldAdd() {

        Location location = gson.fromJson(locationJson, Location.class);
        structureRepository.addOrUpdate(location);

        verify(sqLiteDatabase).replace(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture());
        assertEquals(2, stringArgumentCaptor.getAllValues().size());

        Iterator<String> iterator = stringArgumentCaptor.getAllValues().iterator();
        assertEquals(STRUCTURE_TABLE, iterator.next());
        assertNull(iterator.next());

        ContentValues contentValues = contentValuesArgumentCaptor.getValue();
        assertEquals(6, contentValues.size());

        assertEquals("90397", contentValues.getAsString("_id"));
        assertEquals("41587456-b7c8-4c4e-b433-23a786f742fc", contentValues.getAsString("uuid"));
        assertEquals("3734", contentValues.getAsString("parent_id"));
        assertEquals(locationJson, stripTimezone(contentValues.getAsString("geojson")));


    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddOrUpdateShouldThrowException() {
        Location location = new Location();
        structureRepository.addOrUpdate(location);

    }

    @Test
    public void tesGetLocationsByParentId() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM structure WHERE parent_id =?", new String[]{"21"})).thenReturn(getCursor());
        List<Location> allLocations = structureRepository.getLocationsByParentId("21");
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM structure WHERE parent_id =?", stringArgumentCaptor.getValue());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals("21", argsCaptor.getValue()[0]);

        assertEquals(1, allLocations.size());
        Location location = allLocations.get(0);
        assertEquals(locationJson, stripTimezone(gson.toJson(location)));

    }

    @Test
    public void tesGetLocationById() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM structure WHERE _id =?", new String[]{"3734"})).thenReturn(getCursor());
        Location location = structureRepository.getLocationById("3734");
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM structure WHERE _id =?", stringArgumentCaptor.getValue());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals("3734", argsCaptor.getValue()[0]);

        assertEquals(locationJson, stripTimezone(gson.toJson(location)));

    }

    @Test
    public void tesGetLocationByUUID() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM structure WHERE uuid =?",
                new String[]{"41587456-b7c8-4c4e-b433-23a786f742fc"})).thenReturn(getCursor());
        Location location = structureRepository.getLocationByUUId("41587456-b7c8-4c4e-b433-23a786f742fc");
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM structure WHERE uuid =?", stringArgumentCaptor.getValue());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals("41587456-b7c8-4c4e-b433-23a786f742fc", argsCaptor.getValue()[0]);

        assertEquals(locationJson, stripTimezone(gson.toJson(location)));

    }


    public MatrixCursor getCursor() {
        MatrixCursor cursor = new MatrixCursor(LocationRepository.COLUMNS);
        Location location = gson.fromJson(locationJson, Location.class);
        cursor.addRow(new Object[]{location.getId(), location.getProperties().getUid(),
                location.getProperties().getParentId(), location.getProperties().getName(), locationJson});
        return cursor;
    }

}
