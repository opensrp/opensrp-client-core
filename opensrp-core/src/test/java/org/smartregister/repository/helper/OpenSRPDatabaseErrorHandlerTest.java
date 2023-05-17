package org.smartregister.repository.helper;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.repository.helper.OpenSRPDatabaseErrorHandler;

import java.io.File;
import java.io.IOException;

public class OpenSRPDatabaseErrorHandlerTest extends BaseUnitTest {

    private OpenSRPDatabaseErrorHandler databaseErrorHandler;

    @Before
    public void setUp() {
        databaseErrorHandler = new OpenSRPDatabaseErrorHandler();
    }

    @Test
    public void testOnCorruptionCallShouldCloseDatabaseObjectIfOpen(){
        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);
        Mockito.doReturn(true).when(database).isOpen();
        ReflectionHelpers.setField(database, "mPath", "");
        Mockito.doNothing().when(database).close();
        databaseErrorHandler.onCorruption(database);
        Mockito.verify(database).close();
    }

    @Test
    public void testOnCorruptionShouldDeleteCurrentDBFile() throws IOException {
        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);
        Mockito.doReturn(true).when(database).isOpen();

        String dbPath  = "src/test/assets/drishti.db";
        File dbfile = new File(dbPath);
        if (!dbfile.exists())
            dbfile.createNewFile();
        ReflectionHelpers.setField(database, "mPath", dbPath);
        Mockito.doNothing().when(database).close();
        databaseErrorHandler.onCorruption(database);
        Mockito.verify(database).close();
        Assert.assertFalse(dbfile.exists());
    }

}
