package org.smartregister.repository;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.clientandeventmodel.processor.model.Event;

import java.util.HashMap;

/**
 * Created by kaderchowdhury on 21/11/17.
 */
public class EventRepositoryTest extends BaseUnitTest {

    public static final String ID_COLUMN = "_id";
    public static final String Relational_ID = "baseEntityId";
    public static final String obsDETAILS_COLUMN = "obsdetails";
    public static final String attributeDETAILS_COLUMN = "attributedetails";
    public String TABLE_NAME = "common";

    @Mock
    private android.database.sqlite.SQLiteDatabase sqLiteDatabase;
    private EventRepository eventRepository;
    @Mock
    private android.content.Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        eventRepository = new EventRepository(context, TABLE_NAME, new String[]{ID_COLUMN, obsDETAILS_COLUMN, attributeDETAILS_COLUMN});
    }

    @Test
    public void constructorNotNullCallsOnCreateDatabaseExec() {
        Assert.assertNotNull(eventRepository);
        eventRepository.onCreate(sqLiteDatabase);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).execSQL(Mockito.anyString());
    }

    @Test
    public void assertcreateValuesForReturnsContentValues() {
        HashMap<String, String> mockmap = new HashMap<>();
        mockmap.put("key", "value");
        Event common = new Event(Relational_ID, mockmap, mockmap, mockmap, mockmap);
        Assert.assertNotNull(eventRepository.createValuesFor(common));
    }
}
