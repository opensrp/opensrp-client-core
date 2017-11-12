package org.smartregister.util;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by kaderchowdhury on 12/11/17.
 */

public class UtilsTest extends BaseUnitTest {


    @Test
    public void assertConvertDateFormatTestReturnsDate() throws Exception {
        org.junit.Assert.assertEquals("20-10-2017",Utils.convertDateFormat("2017-10-20",true));
        org.junit.Assert.assertEquals("",Utils.convertDateFormat("20171020",true));
    }

    @Test
    public void assertToDateReturnsDate() throws Exception {
        SimpleDateFormat DB_DF = new SimpleDateFormat("yyyy-MM-dd");
        Date date = DB_DF.parse("2017-10-20");
        org.junit.Assert.assertNotNull(Utils.toDate("2017-10-20",true));
        org.junit.Assert.assertNull(Utils.toDate("20171020",true));
        org.junit.Assert.assertEquals(date,Utils.toDate("2017-10-20",true));
    }

    @Test
    public void assertConvertDateFormat() throws Exception {
        org.junit.Assert.assertEquals("20-10-2017",Utils.convertDateFormat("2017-10-20","abcdxyz",true));
        org.junit.Assert.assertEquals("abcdxyz",Utils.convertDateFormat("20171020","abcdxyz",true));
        org.junit.Assert.assertEquals("",Utils.convertDateFormat("20171020","",true));
    }

    @Test
    public void assertConvertDateFormatReturnsDate() throws Exception {
        DateTime dateTime = new DateTime(0l);
        org.junit.Assert.assertEquals("01-01-1970",Utils.convertDateFormat(dateTime));
    }

    @Test
    public void assertConvertDateTimeFormatReturnsDate() throws Exception {
        org.junit.Assert.assertEquals("24-07-1985",Utils.convertDateFormat("1985-07-24T00:00:00.000Z",true));
        org.junit.Assert.assertEquals("",Utils.convertDateFormat("19850724",true));
    }

    @Test
    public void assertFillValueFromHashMapReturnsValue(){
        android.widget.TextView view = new android.widget.TextView(RuntimeEnvironment.application);
        HashMap<String,String>map = new HashMap<String,String>();
        String field = "field";
        map.put(field,"2017-10-20");
        Utils.fillValue(view,map,field,true);
        org.junit.Assert.assertEquals("2017-10-20",view.getText());
        map.put(field,"");
        Utils.fillValue(view,map,field,"default",true);
        org.junit.Assert.assertEquals("default",view.getText());
    }

    @Test
    public void assertFillValueFromCommonPersonObjectClientReturnsValue(){
        android.widget.TextView view = new android.widget.TextView(RuntimeEnvironment.application);

        String field = "field";
        HashMap<String,String>map = new HashMap<String,String>();
        map.put(field,"2017-10-20");
        CommonPersonObjectClient cm = new CommonPersonObjectClient("",map,"NAME");
        Utils.fillValue(view,cm,field,true);
        org.junit.Assert.assertEquals("2017-10-20",view.getText());
        map.put(field,"");
        cm.setDetails(map);
        Utils.fillValue(view,cm,field,"default",true);
        org.junit.Assert.assertEquals("default",view.getText());
    }

}
