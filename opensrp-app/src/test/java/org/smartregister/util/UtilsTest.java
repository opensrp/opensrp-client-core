package org.smartregister.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.TableRow;

import androidx.test.core.app.ApplicationProvider;

import com.google.common.collect.ImmutableMap;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncFilter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.jsonmapping.LoginResponseData;
import org.smartregister.domain.jsonmapping.User;
import org.smartregister.domain.jsonmapping.util.Team;
import org.smartregister.domain.jsonmapping.util.TeamLocation;
import org.smartregister.domain.jsonmapping.util.TeamMember;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.AllSharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.smartregister.TestUtils.getContext;
import static org.smartregister.util.Utils.getDefaultLocale;

/**
 * Created by kaderchowdhury on 12/11/17.
 */
public class UtilsTest extends BaseRobolectricUnitTest {

    @Before
    public void setUp() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()));
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "allSharedPreferences", allSharedPreferences);
    }

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
        assertNull(Utils.toDate("20171020", true));
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
        Context context = getContext(40);
        assertEquals(Utils.getVersionCode(context), 40);
    }

    @Test
    public void testIs2xxSuccessfulShouldReturnCorrectStatus() {
        assertTrue(Utils.is2xxSuccessful(200));
        assertTrue(Utils.is2xxSuccessful(203));
        assertTrue(Utils.is2xxSuccessful(207));
        assertFalse(Utils.is2xxSuccessful(300));
    }

    @Test
    public void testDobStringToDateTimeShouldReturnCorrectDateTime() {
        assertEquals(Utils.dobStringToDateTime("2000-12-30").toString(), new DateTime("2000-12-30").toString());
    }

    @Test
    public void testDobStringToDateShouldReturnCorrectDate() {
        assertEquals(Utils.dobStringToDate("2000-12-30").toString(), Utils.dobStringToDateTime("2000-12-30").toDate().toString());
    }

    @Test
    public void testGetFilterValueShouldGetCorrectFilterValue() {
        LoginResponseData loginResponseData = getLoginResponseData();
        LoginResponse loginResponse = LoginResponse.SUCCESS.withPayload(loginResponseData);
        assertEquals(loginResponseData.team.team.uuid, Utils.getFilterValue(loginResponse, SyncFilter.TEAM));
        assertEquals(loginResponseData.team.team.uuid, Utils.getFilterValue(loginResponse, SyncFilter.TEAM_ID));
        assertEquals(loginResponseData.team.team.location.uuid, Utils.getFilterValue(loginResponse, SyncFilter.LOCATION));
        assertEquals(loginResponseData.user.getUsername(), Utils.getFilterValue(loginResponse, SyncFilter.PROVIDER));
    }

    @Test
    @Ignore
    public void testGetPropertiesShouldGetPropertyFile() {
        AppProperties appProperties = Utils.getProperties(RuntimeEnvironment.application);
        assertTrue(appProperties.getPropertyBoolean("property_4"));
        assertEquals("property_1", appProperties.getProperty("property_1"));
        assertEquals("property_2", appProperties.getProperty("property_2"));
        assertEquals("property_3", appProperties.getProperty("property_3"));
    }

    @Test
    public void testGetUserDefaultTeamIdShouldGetCorrectTeamId() {
        LoginResponseData loginResponseData = getLoginResponseData();
        assertEquals(loginResponseData.team.team.uuid, Utils.getUserDefaultTeamId(loginResponseData));
    }

    @Test
    public void testIsEmptyCollectionShouldReturnCorrectStatus() {
        List<String> list = new ArrayList<>();
        list.add("string");
        assertFalse(Utils.isEmptyCollection(list));
        list.clear();
        assertTrue(Utils.isEmptyCollection(list));
    }

    @Test
    public void testIsEmptyMapShouldReturnCorrectStatus() {
        Map<String, String> map = new HashMap<>();
        map.put("key", "string");
        assertFalse(Utils.isEmptyMap(map));
        map.clear();
        assertTrue(Utils.isEmptyMap(map));
    }

    @Test
    public void testGetNameShouldGetCorrectlyFormattedName() {
        assertEquals("John Doe", Utils.getName("John", "Doe"));
        assertEquals("John", Utils.getName("John", ""));
        assertEquals("Doe", Utils.getName("", "Doe"));
    }

    private LoginResponseData getLoginResponseData() {
        Team team = new Team();
        team.uuid = "team_uuid";

        TeamLocation teamLocation = new TeamLocation();
        teamLocation.uuid = "location_uuid";

        TeamMember teamMember = new TeamMember();
        teamMember.team = team;
        teamMember.team.location = teamLocation;
        teamMember.team = team;

        User user = new User();
        user.setUsername("user_name");

        LoginResponseData loginResponseData = new LoginResponseData();
        loginResponseData.team = teamMember;
        loginResponseData.user = user;

        return loginResponseData;
    }

    @Test
    public void testGetAgeFromDateShouldGetCorrectAge() {
        String date = "2000-12-12";
        DateTime dateTime = DateTime.parse(date);
        assertEquals(Utils.getAgeFromDate(date), Years.yearsBetween(dateTime.toLocalDate(), LocalDate.now()).getYears());
    }

    @Test
    public void testGetDobShouldGetCorrectDob() {
        int age = 10;
        String datePattern = "YYYY-MM-dd";
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -age);
        assertEquals(cal.getWeekYear() + "-" + "01" + "-" + "01", Utils.getDob(age, datePattern));
        assertEquals("01" + "-" + "01" + "-" + cal.getWeekYear(), Utils.getDob(age));
    }

    @Test
    public void testGetAllSharedPreferencesShouldGetPreferences() {
        assertNotNull(Utils.getAllSharedPreferences());
    }

    @Test
    public void testGetPrefferedNameShouldGetCorrectPreferredName() {
        Utils.getAllSharedPreferences().updateANMUserName("provider");
        Utils.getAllSharedPreferences().updateANMPreferredName("provider", "prov");
        assertEquals("prov", Utils.getPrefferedName());
    }

    @Test
    public void testGetBuildDateShouldGetCorrectBuildDate() {
        Date date = new Date(CoreLibrary.getBuildTimeStamp());
        assertEquals(new SimpleDateFormat("dd MMM yyyy", getDefaultLocale()).format(date), Utils.getBuildDate(true));
        assertEquals(new SimpleDateFormat("dd MMMM yyyy", getDefaultLocale()).format(date), Utils.getBuildDate(false));
    }

    @Test
    public void testGetUserInitialsShouldGetCorrectInitials() {
        assertEquals("Me", Utils.getUserInitials());
        Utils.getAllSharedPreferences().updateANMUserName("provider");
        Utils.getAllSharedPreferences().updateANMPreferredName("provider", "provider name");
        assertEquals("pn", Utils.getUserInitials());
        Utils.getAllSharedPreferences().updateANMPreferredName("provider", "provider");
        assertEquals("p", Utils.getUserInitials());
    }

    @Test
    public void testGetAppIdShouldReturnAppId() {
        assertEquals("org.smartregister.test", Utils.getAppId(RuntimeEnvironment.application));
    }

    @Test
    public void testGetAppVersionShouldReturnAppVersion() {
        assertNull(Utils.getAppVersion(RuntimeEnvironment.application));
    }
}

