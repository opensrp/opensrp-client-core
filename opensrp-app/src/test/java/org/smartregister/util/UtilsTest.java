package org.smartregister.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.TableRow;

import com.google.common.collect.ImmutableMap;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;
import org.smartregister.TestApplication;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.AllSettings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.smartregister.AllConstants.FORCED_LOGOUT.IS_APP_VERSION_ALLOWED;

/**
 * Created by kaderchowdhury on 12/11/17.
 */

public class UtilsTest extends BaseUnitTest {


    @Test
    public void assertConvertDateFormatTestReturnsDate() throws Exception {
        assertEquals("20-10-2017", Utils.convertDateFormat("2017-10-20", true));
        assertEquals("", Utils.convertDateFormat("20171020", true));
    }

    @Test
    public void assertToDateReturnsDate() throws Exception {
        SimpleDateFormat DB_DF = new SimpleDateFormat("yyyy-MM-dd");
        Date date = DB_DF.parse("2017-10-20");
        org.junit.Assert.assertNotNull(Utils.toDate("2017-10-20", true));
        org.junit.Assert.assertNull(Utils.toDate("20171020", true));
        assertEquals(date, Utils.toDate("2017-10-20", true));
    }

    @Test
    public void assertConvertDateFormat() throws Exception {
        assertEquals("20-10-2017", Utils.convertDateFormat("2017-10-20", "abcdxyz", true));
        assertEquals("abcdxyz", Utils.convertDateFormat("20171020", "abcdxyz", true));
        assertEquals("", Utils.convertDateFormat("20171020", "", true));
    }

    @Test
    public void assertConvertDateFormatReturnsDate() throws Exception {
        DateTime dateTime = new DateTime(0l);
        assertEquals("01-01-1970", Utils.convertDateFormat(dateTime));
    }

    @Test
    public void assertConvertDateTimeFormatReturnsDate() throws Exception {
        assertEquals("24-07-1985 00:00:00", Utils.convertDateTimeFormat("1985-07-24T00:00:00.000Z", true));
//        org.junit.Assert.assertEquals("", Utils.convertDateTimeFormat("19850724", true));
    }

    @Test(expected = RuntimeException.class)
    public void assertConvertDateTimeFormatThrowsExceptionIfUnpasrsableDate() {
        Utils.convertDateTimeFormat("1985-07-24XXXYYY", false);
    }

    @Test
    public void assertFillValueFromHashMapReturnsValue() {
        android.widget.TextView view = new android.widget.TextView(RuntimeEnvironment.application);
        HashMap<String, String> map = new HashMap<String, String>();
        String field = "field";
        map.put(field, "2017-10-20");
        Utils.fillValue(view, map, field, true);
        assertEquals("2017-10-20", view.getText());
        map.put(field, "");
        Utils.fillValue(view, map, field, "default", true);
        assertEquals("default", view.getText());
    }

    @Test
    public void assertFillValueFromCommonPersonObjectClientReturnsValue() {
        android.widget.TextView view = new android.widget.TextView(RuntimeEnvironment.application);

        String field = "field";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(field, "2017-10-20");
        CommonPersonObjectClient cm = new CommonPersonObjectClient("", map, "NAME");
        Utils.fillValue(view, cm, field, true);
        assertEquals("2017-10-20", view.getText());
        map.put(field, "");
        cm.setDetails(map);
        Utils.fillValue(view, cm, field, "default", true);
        assertEquals("default", view.getText());
    }

    @Test
    public void assertFillValueTextFieldReturnsValue() throws Exception {
        android.widget.TextView view = new android.widget.TextView(RuntimeEnvironment.application);
        String value = "value";
        view.setText(value);
        Utils.fillValue(view, value);
        assertEquals(value, view.getText());
    }

    @Test
    public void assertFormatValueReturnsValue() {
        assertEquals("", Utils.formatValue(null, true));
        assertEquals("Abc-def", Utils.formatValue("abc-def", true));
        assertEquals("abc-def", Utils.formatValue("abc-def", false));
    }

    @Test
    public void assertFormatValueObjectReturnsValue() {
        assertEquals("", Utils.formatValue((Object) null, true));
        assertEquals("Abc-def", Utils.formatValue((Object) new String("abc-def"), true));
        assertEquals("abc-def", Utils.formatValue((Object) new String("abc-def"), false));
    }

    @Test
    public void assertGetValueReturnsValue() {
        String field = "field";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(field, "2017-10-20");
        CommonPersonObjectClient cm = new CommonPersonObjectClient("", map, "NAME");
        assertEquals(Utils.getValue(cm, field, "default", true), "2017-10-20");
        map.put(field, "");
        assertEquals(Utils.getValue(cm, field, "default", true), "default");
    }

    @Test
    public void assertGetValueMapReturnsValue() {
        String field = "field";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(field, "2017-10-20");
        assertEquals(Utils.getValue(map, field, "default", true), "2017-10-20");
        map.put(field, "");
        assertEquals(Utils.getValue(map, field, "default", true), "default");
    }

    @Test
    public void assertNotEmptyValueReturnsValue() {
        String field = "field";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(field, "2017-10-20");
        assertEquals(Utils.nonEmptyValue(map, false, true, new String[]{field}), "2017-10-20");
        map.put(field, "");
        assertEquals(Utils.nonEmptyValue(map, false, true, new String[]{field}), "");
    }

    @Test
    public void assertHasEmptyValueReturnsBoolean() throws Exception {
        String field = "field";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(field, "");
        assertEquals(Utils.hasAnyEmptyValue(map, "", new String[]{field}), true);
        map.put(field, "2017-10-20");
        assertEquals(Utils.hasAnyEmptyValue(map, "x", new String[]{field}), false);
    }

    @Test
    public void assertAddToIntReturnsSum() {
        assertEquals(Utils.addAsInts(true, new String[]{""}), 0);
        assertEquals(Utils.addAsInts(true, new String[]{"1", "1", "1"}), 3);
    }

    @Test
    public void assertAddToRowReturnsTableRow() {
        TableRow mockRow = new TableRow(RuntimeEnvironment.application);
        TableRow row = Utils.addToRow(RuntimeEnvironment.application, "hello world", mockRow);
        assertEquals(mockRow, row);
        android.widget.TextView view = (android.widget.TextView) row.getChildAt(0);
        assertEquals(view.getText().toString(), "hello world");
    }

    @Test
    public void assertAddToRowWeihtReturnsTableRow() {
        TableRow mockRow = new TableRow(RuntimeEnvironment.application);
        TableRow row = Utils.addToRow(RuntimeEnvironment.application, "hello world", mockRow, 25);
        assertEquals(mockRow, row);
        android.widget.TextView view = (android.widget.TextView) row.getChildAt(0);
        assertEquals(view.getText().toString(), "hello world");
    }

    @Test
    public void assertAddToRowcompatReturnsTableRow() {
        TableRow mockRow = new TableRow(RuntimeEnvironment.application);
        TableRow row = Utils.addToRow(RuntimeEnvironment.application, "hello world", mockRow, true);
        assertEquals(mockRow, row);
        android.widget.TextView view = (android.widget.TextView) row.getChildAt(0);
        assertEquals(view.getText().toString(), "hello world");
    }

    @Test
    public void assertAddToRowWeightCompatReturnsTableRow() {
        TableRow mockRow = new TableRow(RuntimeEnvironment.application);
        TableRow row = Utils.addToRow(RuntimeEnvironment.application, "<b>hello world</b>", mockRow, true, 25);
        assertEquals(mockRow, row);
        android.widget.TextView view = (android.widget.TextView) row.getChildAt(0);
        assertEquals(view.getText().toString(), "hello world");
    }

    @Test
    public void testCompleteSyncIntent() throws Exception {
        Intent intent = new Intent();
        assertEquals(intent.getExtras(), null);
        intent.setAction(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, FetchStatus.fetched);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_COMPLETE_STATUS, true);

        Intent UtilIntent = Utils.completeSync(FetchStatus.fetched);

        Assert.assertSame(intent.getExtras().get("complete_status"), UtilIntent.getExtras().get("complete_status"));
        Assert.assertSame(intent.getExtras().get("fetch_status"), UtilIntent.getExtras().get("fetch_status"));
        assertEquals(FetchStatus.fetched, UtilIntent.getExtras().get("fetch_status"));
        Assert.assertSame(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS, UtilIntent.getAction());
        Assert.assertSame(0, Utils.completeSync(FetchStatus.fetched).getFlags());

    }

    @Test
    public void testKgStringSuffixReturnsCorrectValueIfParamFloat() {

        String result = Utils.kgStringSuffix(2.5f);
        Assert.assertNotNull(result);
        assertEquals("2.5 kg", result);
    }

    @Test
    public void testKgStringSuffixReturnsCorrectValueIfParamString() {

        String result = Utils.kgStringSuffix("3.4");
        Assert.assertNotNull(result);
        assertEquals("3.4 kg", result);
    }

    @Test
    public void testCmStringSuffixReturnsCorrectValueIfParamFloat() {

        String result = Utils.cmStringSuffix(1.5f);
        Assert.assertNotNull(result);
        assertEquals("1.5 cm", result);
    }

    @Test
    public void testCmStringSuffixReturnsCorrectValueIfParamString() {

        String result = Utils.cmStringSuffix("45.9");
        Assert.assertNotNull(result);
        assertEquals("45.9 cm", result);
    }


    @Test
    public void testConvertConvertsCommonPersonObjectToCommonPersonObjectClient() {

        Map<String, String> map = ImmutableMap.of("first_name", "Martin", "last_name", "Bull", "dob", "10-01-1970", "phone_number", "07246738839", "gender", "Male");
        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient("dfh45453483-34dfd893-394343cds3", map, "Marchello");

        Assert.assertNotNull(commonPersonObjectClient);
        Assert.assertNotNull(commonPersonObjectClient.getCaseId());
        Assert.assertNotNull(commonPersonObjectClient.getDetails());
        assertEquals(commonPersonObjectClient.getDetails().get("first_name"), "Martin");
    }

    @Test
    public void testGetVersionCodeShouldGetCorrectVersionCode() throws PackageManager.NameNotFoundException {
        Context context = mock(Context.class);
        PackageManager packageManager = mock(PackageManager.class);
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.versionCode = 40;
        doReturn(packageManager).when(context).getPackageManager();
        doReturn(packageInfo).when(packageManager).getPackageInfo(anyString(), anyInt());
        doReturn("").when(context).getPackageName();

        assertEquals(Utils.getVersionCode(context), 40);
    }

    @Test
    public void testIsAppVersionAllowedShouldReturnCorrectStatus() {
        TestApplication testApplication = (TestApplication) TestApplication.getInstance();
        org.smartregister.Context context = mock(org.smartregister.Context.class);
        testApplication.setContext(context);
        AllSettings settings = mock(AllSettings.class);
        doReturn(settings).when(context).allSettings();

        doReturn("false").when(settings).get(IS_APP_VERSION_ALLOWED, "true");
        assertFalse(Utils.isAppVersionAllowed());

        doReturn("true").when(settings).get(IS_APP_VERSION_ALLOWED, "true");;
        assertTrue(Utils.isAppVersionAllowed());
    }
}
