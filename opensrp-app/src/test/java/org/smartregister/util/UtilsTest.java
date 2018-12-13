package org.smartregister.util;

import android.content.Intent;
import android.widget.TableRow;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by kaderchowdhury on 12/11/17.
 */

public class UtilsTest extends BaseUnitTest {


    @Test
    public void assertConvertDateFormatTestReturnsDate() throws Exception {
        org.junit.Assert.assertEquals("20-10-2017", Utils.convertDateFormat("2017-10-20", true));
        org.junit.Assert.assertEquals("", Utils.convertDateFormat("20171020", true));
    }

    @Test
    public void assertToDateReturnsDate() throws Exception {
        SimpleDateFormat DB_DF = new SimpleDateFormat("yyyy-MM-dd");
        Date date = DB_DF.parse("2017-10-20");
        org.junit.Assert.assertNotNull(Utils.toDate("2017-10-20", true));
        org.junit.Assert.assertNull(Utils.toDate("20171020", true));
        org.junit.Assert.assertEquals(date, Utils.toDate("2017-10-20", true));
    }

    @Test
    public void assertConvertDateFormat() throws Exception {
        org.junit.Assert.assertEquals("20-10-2017", Utils.convertDateFormat("2017-10-20", "abcdxyz", true));
        org.junit.Assert.assertEquals("abcdxyz", Utils.convertDateFormat("20171020", "abcdxyz", true));
        org.junit.Assert.assertEquals("", Utils.convertDateFormat("20171020", "", true));
    }

    @Test
    public void assertConvertDateFormatReturnsDate() throws Exception {
        DateTime dateTime = new DateTime(0l);
        org.junit.Assert.assertEquals("01-01-1970", Utils.convertDateFormat(dateTime));
    }

    @Test
    public void assertConvertDateTimeFormatReturnsDate() throws Exception {
        org.junit.Assert.assertEquals("24-07-1985 00:00:00", Utils.convertDateTimeFormat("1985-07-24T00:00:00.000Z", true));
//        org.junit.Assert.assertEquals("", Utils.convertDateTimeFormat("19850724", true));
    }

    @Test
    public void assertFillValueFromHashMapReturnsValue() {
        android.widget.TextView view = new android.widget.TextView(RuntimeEnvironment.application);
        HashMap<String, String> map = new HashMap<String, String>();
        String field = "field";
        map.put(field, "2017-10-20");
        Utils.fillValue(view, map, field, true);
        org.junit.Assert.assertEquals("2017-10-20", view.getText());
        map.put(field, "");
        Utils.fillValue(view, map, field, "default", true);
        org.junit.Assert.assertEquals("default", view.getText());
    }

    @Test
    public void assertFillValueFromCommonPersonObjectClientReturnsValue() {
        android.widget.TextView view = new android.widget.TextView(RuntimeEnvironment.application);

        String field = "field";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(field, "2017-10-20");
        CommonPersonObjectClient cm = new CommonPersonObjectClient("", map, "NAME");
        Utils.fillValue(view, cm, field, true);
        org.junit.Assert.assertEquals("2017-10-20", view.getText());
        map.put(field, "");
        cm.setDetails(map);
        Utils.fillValue(view, cm, field, "default", true);
        org.junit.Assert.assertEquals("default", view.getText());
    }

    @Test
    public void assertFillValueTextFieldReturnsValue() throws Exception {
        android.widget.TextView view = new android.widget.TextView(RuntimeEnvironment.application);
        String value = "value";
        view.setText(value);
        Utils.fillValue(view, value);
        Assert.assertEquals(value, view.getText());
    }

    @Test
    public void assertFormatValueReturnsValue() {
        Assert.assertEquals("", Utils.formatValue(null, true));
        Assert.assertEquals("Abc-def", Utils.formatValue("abc-def", true));
        Assert.assertEquals("abc-def", Utils.formatValue("abc-def", false));
    }

    @Test
    public void assertFormatValueObjectReturnsValue() {
        Assert.assertEquals("", Utils.formatValue((Object) null, true));
        Assert.assertEquals("Abc-def", Utils.formatValue((Object) new String("abc-def"), true));
        Assert.assertEquals("abc-def", Utils.formatValue((Object) new String("abc-def"), false));
    }

    @Test
    public void assertGetValueReturnsValue() {
        String field = "field";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(field, "2017-10-20");
        CommonPersonObjectClient cm = new CommonPersonObjectClient("", map, "NAME");
        Assert.assertEquals(Utils.getValue(cm, field, "default", true), "2017-10-20");
        map.put(field, "");
        Assert.assertEquals(Utils.getValue(cm, field, "default", true), "default");
    }

    @Test
    public void assertGetValueMapReturnsValue() {
        String field = "field";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(field, "2017-10-20");
        Assert.assertEquals(Utils.getValue(map, field, "default", true), "2017-10-20");
        map.put(field, "");
        Assert.assertEquals(Utils.getValue(map, field, "default", true), "default");
    }

    @Test
    public void assertNotEmptyValueReturnsValue() {
        String field = "field";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(field, "2017-10-20");
        Assert.assertEquals(Utils.nonEmptyValue(map, false, true, new String[]{field}), "2017-10-20");
        map.put(field, "");
        Assert.assertEquals(Utils.nonEmptyValue(map, false, true, new String[]{field}), "");
    }

    @Test
    public void assertHasEmptyValueReturnsBoolean() throws Exception {
        String field = "field";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(field, "");
        Assert.assertEquals(Utils.hasAnyEmptyValue(map, "", new String[]{field}), true);
        map.put(field, "2017-10-20");
        Assert.assertEquals(Utils.hasAnyEmptyValue(map, "x", new String[]{field}), false);
    }

    @Test
    public void assertAddToIntReturnsSum() {
        Assert.assertEquals(Utils.addAsInts(true, new String[]{""}), 0);
        Assert.assertEquals(Utils.addAsInts(true, new String[]{"1", "1", "1"}), 3);
    }

    @Test
    public void assertAddToRowReturnsTableRow() {
        TableRow mockRow = new TableRow(RuntimeEnvironment.application);
        TableRow row = Utils.addToRow(RuntimeEnvironment.application, "hello world", mockRow);
        Assert.assertEquals(mockRow, row);
        android.widget.TextView view = (android.widget.TextView) row.getChildAt(0);
        Assert.assertEquals(view.getText().toString(), "hello world");
    }

    @Test
    public void assertAddToRowWeihtReturnsTableRow() {
        TableRow mockRow = new TableRow(RuntimeEnvironment.application);
        TableRow row = Utils.addToRow(RuntimeEnvironment.application, "hello world", mockRow, 25);
        Assert.assertEquals(mockRow, row);
        android.widget.TextView view = (android.widget.TextView) row.getChildAt(0);
        Assert.assertEquals(view.getText().toString(), "hello world");
    }

    @Test
    public void assertAddToRowcompatReturnsTableRow() {
        TableRow mockRow = new TableRow(RuntimeEnvironment.application);
        TableRow row = Utils.addToRow(RuntimeEnvironment.application, "hello world", mockRow, true);
        Assert.assertEquals(mockRow, row);
        android.widget.TextView view = (android.widget.TextView) row.getChildAt(0);
        Assert.assertEquals(view.getText().toString(), "hello world");
    }

    @Test
    public void assertAddToRowWeightCompatReturnsTableRow() {
        TableRow mockRow = new TableRow(RuntimeEnvironment.application);
        TableRow row = Utils.addToRow(RuntimeEnvironment.application, "<b>hello world</b>", mockRow, true, 25);
        Assert.assertEquals(mockRow, row);
        android.widget.TextView view = (android.widget.TextView) row.getChildAt(0);
        Assert.assertEquals(view.getText().toString(), "hello world");
    }

    @Test
    public void testCompleteSyncIntent() throws Exception {
        Intent intent = new Intent();
        Assert.assertEquals(intent.getExtras(), null);
        intent.setAction(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, FetchStatus.fetched);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_COMPLETE_STATUS, true);

        Intent UtilIntent = Utils.completeSync(FetchStatus.fetched);

        Assert.assertSame(intent.getExtras().get("complete_status"), UtilIntent.getExtras().get("complete_status"));
        Assert.assertSame(intent.getExtras().get("fetch_status"), UtilIntent.getExtras().get("fetch_status"));
        Assert.assertEquals(FetchStatus.fetched, UtilIntent.getExtras().get("fetch_status"));
        Assert.assertSame(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS, UtilIntent.getAction());
        Assert.assertSame(0, Utils.completeSync(FetchStatus.fetched).getFlags());

    }

}
