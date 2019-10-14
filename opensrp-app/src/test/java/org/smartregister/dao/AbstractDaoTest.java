package org.smartregister.dao;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Alert;
import org.smartregister.repository.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AbstractDaoTest extends BaseUnitTest {

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.doReturn(sqLiteDatabase).when(repository).getReadableDatabase();
        Mockito.doReturn(sqLiteDatabase).when(repository).getWritableDatabase();
    }

    @Test
    public void testGetDateFormat() {
        // validate the date date format returned is of type 'yyyy-MM-dd'
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        Assert.assertEquals(sdf.format(now), AbstractDao.getDobDateFormat().format(now));
    }

    @Test
    public void testGetNativeFormsDateFormat() {
        // validate the date date format returned is of type 'yyyy-MM-dd'
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date now = new Date();
        Assert.assertEquals(sdf.format(now), AbstractDao.getNativeFormsDateFormat().format(now));
    }

    @Test
    public void testObjectsConstructedEqualsCursorSize() {
        MatrixCursor cursor = new MatrixCursor(new String[]{"count"});
        cursor.addRow(new Object[]{"1"});
        Mockito.doReturn(cursor).when(sqLiteDatabase).rawQuery(Mockito.anyString(), Mockito.any(String[].class));

        SampleAbstractDaoImp.setRepository(repository);
        int count = SampleAbstractDaoImp.getCountOfEvents();
        Assert.assertEquals(count, 1);
    }

    @Test
    public void testDataTypeConversion() {
        MatrixCursor cursor = new MatrixCursor(new String[]{"int", "date", "long", "string", "sample_long_date", "sample_string_value"});
        cursor.addRow(new Object[]{"1", "2019-01-01", "123123123", "Greatest", "0", null});
        cursor.addRow(new Object[]{"1", "2019-01-01", "123123123", "Greatest", "0", null});
        Mockito.doReturn(cursor).when(sqLiteDatabase).rawQuery(Mockito.anyString(), Mockito.any(String[].class));

        SampleAbstractDaoImp.setRepository(repository);
        List<SampleAbstractDaoImp.SampleObject> res = SampleAbstractDaoImp.getSamples();

        // verify 2 objects where constructed
        Assert.assertEquals(res.size(), 2);

        SampleAbstractDaoImp.SampleObject obj = res.get(0);

        Assert.assertEquals(1, (int) obj.getSampleInt());
        Assert.assertEquals(123123123L, obj.getSampleLong());
        Assert.assertEquals("2019-01-01", AbstractDao.getDobDateFormat().format(obj.getSampleDate()));
        Assert.assertEquals("1970-01-01", AbstractDao.getDobDateFormat().format(obj.getSampleLongDate()));
        Assert.assertEquals("Greatest", obj.getSampleString());
        Assert.assertEquals("default", obj.getSampleStringWithDefault());
    }

    @Test
    public void testErrorInSerializationReturnsNull() {
        MatrixCursor cursor = new MatrixCursor(new String[]{"count"});
        cursor.addRow(new Object[]{"1"});
        Mockito.doReturn(cursor).when(sqLiteDatabase).rawQuery(Mockito.anyString(), Mockito.any(String[].class));

        SampleAbstractDaoImp.setRepository(repository);
        List<Alert> alerts = SampleAbstractDaoImp.getAllAlerts();
        Assert.assertNull(alerts);
    }

    @Test
    public void testUpdateDB() {
        String sql = "update table set col1 = 'value' where id = x";
        AbstractDao.setRepository(repository);
        AbstractDao.updateDB(sql);
        Mockito.verify(sqLiteDatabase).rawExecSQL(sql);
    }

    @Test
    public void testReadValue() {
        MatrixCursor cursor = new MatrixCursor(new String[]{"name", "age"});
        cursor.addRow(new Object[]{"Tony", "25"});
        cursor.addRow(new Object[]{"Jessica", "22"});
        Mockito.doReturn(cursor).when(sqLiteDatabase).rawQuery(Mockito.anyString(), Mockito.any(String[].class));

        AbstractDao.setRepository(repository);
        List<Map<String, String>> vals = AbstractDao.readData("select * from people", new String[]{"1234"});
        Assert.assertEquals(vals.size(), 2);
        Assert.assertEquals(vals.get(0).get("name"), "Tony");
        Assert.assertEquals(vals.get(0).get("age"), "25");
        Assert.assertEquals(vals.get(1).get("name"), "Jessica");
        Assert.assertEquals(vals.get(1).get("age"), "22");
    }
}
