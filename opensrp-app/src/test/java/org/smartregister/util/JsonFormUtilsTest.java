package org.smartregister.util;

import android.content.res.AssetManager;
import android.util.Xml;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.cloudant.models.Client;
import org.smartregister.cloudant.models.Event;
import org.smartregister.domain.ANM;
import org.smartregister.domain.SyncStatus;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.repository.FormDataRepository;
import org.smartregister.service.ANMService;
import org.smartregister.sync.CloudantDataHandler;
import org.smartregister.util.mock.XmlSerializerMock;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static org.smartregister.util.JsonFormUtils.dd_MM_yyyy;

/**
 * Created by kaderchowdhury on 14/11/17.
 */
@PrepareForTest({CoreLibrary.class, CloudantDataHandler.class, Xml.class})
public class JsonFormUtilsTest extends BaseUnitTest {

    private JsonFormUtils formUtils;
    private String FORMNAME = "birthnotificationpregnancystatusfollowup";
    private String formDefinition = "www/form/" + FORMNAME + "/form_definition.json";
    private String model = "www/form/" + FORMNAME + "/model.xml";
    private String formJSON = "www/form/" + FORMNAME + "/form.json";
    private String formMultiJSON = "www/form/" + FORMNAME + "/form_multi.json";
    private String DEFAULT_BIND_PATH = "/model/instance/Child_Vaccination_Enrollment/";
    private String formSubmissionXML = "www/form/form_submission/form_submission_xml.xml";
    private String formSubmissionJSON = "www/form/form_submission/form_submission_json.json";
    private String entityRelationShip = "www/form/entity_relationship.json";
    private String formresultJson = "{\"count\":\"1\",\"mother\":{\"encounter_type\":\"New Woman Registration\"},\"entity_id\":\"\",\"relational_id\":\"\",\"step1\":{\"title\":\"Birth Registration\",\"fields\":[{\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"uploadButtonText\":\"শিশুর ছবি তুলুন \",\"openmrs_entity_parent\":\"\",\"type\":\"choose_image\",\"key\":\"Child_Photo\"},{\"default\":\"[\\\"Bangladesh\\\"]\",\"tree\":[{\"nodes\":[{\"nodes\":[{\"nodes\":[{\"nodes\":[{\"nodes\":[{\"nodes\":[{\"nodes\":[{\"key\":\"Kuptala:Ward1:1-KA:Abdur Rahim Memberer bari-Kholapar\",\"level\":\"\",\"name\":\"Abdur Rahim Memberer bari-Kholapar\"},{\"key\":\"Kuptala:Ward1:1-KA:Jaynal Abediner Bari-Jangalia\",\"level\":\"\",\"name\":\"Jaynal Abediner Bari-Jangalia\"},{\"key\":\"Kuptala:Ward1:1-KA:Narayon Cowdhurir bari-Kholapara\",\"level\":\"\",\"name\":\"Narayon Cowdhurir bari-Kholapara\"}],\"key\":\"Kuptala:Ward1:1-KA\",\"level\":\"\",\"name\":\"1-KA\"}],\"key\":\"Kuptala:Ward-1\",\"level\":\"\",\"name\":\"Ward-1\"},{\"nodes\":[{\"key\":\"Kuptala:Ward-2:1-KA\",\"level\":\"\",\"name\":\"1-KA\"}],\"key\":\"Kuptala:Ward-2\",\"level\":\"\",\"name\":\"Ward-2\"}],\"key\":\"Kuptala\",\"level\":\"\",\"name\":\"Kuptala\"}],\"key\":\"Gaibandha Sadar\",\"level\":\"\",\"name\":\"Gaibandha Sadar\"}],\"key\":\"Gaibandha\",\"level\":\"\",\"name\":\"Gaibandha\"}],\"key\":\"Rangpur\",\"level\":\"\",\"name\":\"Rangpur\"}],\"key\":\"Bangladesh\",\"level\":\"\",\"name\":\"Bangladesh\"}],\"v_required\":{\"value\":true,\"err\":\"Please enter the child's home facility\"},\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"value\":\"[\\\"Bangladesh\\\",\\\"Rangpur\\\",\\\"Gaibandha\\\",\\\"Gaibandha Sadar\\\",\\\"Kuptala\\\",\\\"Kuptala:Ward-1\\\",\\\"Kuptala:Ward1:1-KA\\\",\\\"Kuptala:Ward1:1-KA:Abdur Rahim Memberer bari-Kholapar\\\"]\",\"hint\":\"শিশুর EPI কেন্দ্র *\",\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"tree\",\"key\":\"HIE_FACILITIES\"},{\"read_only\":\"true\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the Child's ZEIR ID\"},\"openmrs_entity\":\"person_identifier\",\"openmrs_entity_id\":\"OpenMRS_ID\",\"value\":\"10177528\",\"hint\":\"নিবন্ধন নম্বর *\",\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"key\":\"OpenMRS_ID\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a valid ID\"}},{\"value\":\"123123131231231233\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"Child_Birth_Certificate\",\"hint\":\"শিশুর জন্মনিবন্ধন নাম্বার\",\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"key\":\"Child_Birth_Certificate\"},{\"v_regex\":{\"value\":\"[A-Za-z\\\\s.-]*\",\"err\":\"Please enter a valid name\"},\"value\":\"hatim\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"hint\":\"শিশুর নাম\",\"edit_type\":\"name\",\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"key\":\"First_Name\"},{\"values\":[\"Male\",\"Female\"],\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the sex\"},\"value\":\"Male\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"gender\",\"hint\":\"শিশুর লিঙ্গ *\",\"openmrs_entity_parent\":\"\",\"type\":\"spinner\",\"key\":\"Sex\"},{\"max_date\":\"today\",\"duration\":{\"label\":\"Age\"},\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the date of birth\"},\"value\":\"01-01-2017\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"min_date\":\"today-5y\",\"hint\":\"শিশুর জন্ম তারিখ *\",\"openmrs_entity_parent\":\"\",\"type\":\"date_picker\",\"expanded\":false,\"key\":\"Date_Birth\"},{\"v_required\":{\"value\":\"false\",\"err\":\"Enter the child's birth weight\"},\"v_min\":{\"value\":\"0.1\",\"err\":\"Weight must be greater than 0\"},\"value\":\"5\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"hint\":\"জন্মের সময় ওজন (kg) *\",\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"Birth_Weight\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid weight\"}},{\"household_id\":\"1ae4d826-ef15-45d5-a475-37e8b6791260\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother\\/guardian's first name\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s.-]*\",\"err\":\"Please enter a valid name\"},\"value\":\"Mohila Nutun\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1593AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"hint\":\"মা\\/অবিভাবকের নাম *\",\"look_up\":\"true\",\"edit_type\":\"name\",\"openmrs_entity_parent\":\"\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"key\":\"Mother_Guardian_First_Name\"},{\"v_max_length\":{\"value\":\"11\",\"err\":\"Please Enter 11 digit Mobile number\"},\"v_min_length\":{\"value\":\"11\",\"err\":\"Please Enter 11 digit Mobile number\"},\"v_regex\":{\"value\":\"(01[5-9][0-9]{8})|s*\",\"err\":\"Number must begin with 015, 016,017,018 or 019 and must be a total of 11 digits in length\"},\"value\":\"01918901991\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"hint\":\"মা\\/অবিভাবকের মোবাইল নম্বর\",\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"key\":\"Mother_Guardian_Number\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Number must begin with 095, 096, or 097 and must be a total of 11 digits in length\"}},{\"v_regex\":{\"value\":\"[A-Za-z\\\\s.-]*\",\"err\":\"Please enter a valid name\"},\"value\":\"mafinar\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"hint\":\"পিতা \\/ অবিভাবকের নাম\",\"edit_type\":\"name\",\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"Father_Guardian_Name\"},{\"values\":[\"Health facility\",\"Home\"],\"v_required\":{\"value\":true,\"err\":\"Please enter the place of birth\"},\"value\":\"Home\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"hint\":\"জন্মস্থান ধরণ *\",\"openmrs_choice_ids\":{\"Health facility\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Home\":\"1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"key\":\"Place_Birth\"},{\"v_required\":{\"value\":true,\"err\":\"Please enter the birth facility name\"},\"value\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"relevance\":{\"step1:Place_Birth\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"Health facility\\\")\"}},\"hint\":\"শিশুটি জন্মগ্রহণ করার সময় কোন স্বাস্থ্য কেন্দ্রের সুবিধা পেয়েছিল? *\",\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"Birth_Facility_Name\"},{\"v_required\":{\"value\":true,\"err\":\"Please specify the health facility the child was born in\"},\"value\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"relevance\":{\"step1:Birth_Facility_Name\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"[\\\"Home\\\"]\\\")\"}},\"hint\":\"শিশুর জন্ম স্থান * *\",\"edit_type\":\"name\",\"openmrs_entity_parent\":\"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\",\"key\":\"Birth_Facility_Name_Other\"}]},\"encounter_type\":\"Birth Registration\",\"metadata\":{\"phonenumber\":{\"value\":\"15555215554\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"phonenumber\"},\"subscriberid\":{\"value\":\"310260000000000\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"subscriberid\"},\"start\":{\"value\":\"2018-01-01 18:00:49\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"start\"},\"today\":{\"value\":\"01-01-2018\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"look_up\":{\"entity_id\":\"mother\",\"value\":\"69d28d10-1b16-4c56-b8c3-5359a6ba77c4\"},\"encounter_location\":\"4c8cb044-7b15-40b7-8ca2-6eceaa6c4e9a\",\"simserial\":{\"value\":\"89014103211118510720\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"simserial\"},\"end\":{\"value\":\"2018-01-01 18:01:41\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"end\"},\"deviceid\":{\"value\":\"bded72fcd7e3a083\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"deviceid\"}}}";
    private JSONObject formjson;
    private String bindtype = "ec_child";

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    @Mock
    private CloudantDataHandler cloudantDataHandler;
    @Mock
    private CoreLibrary coreLibrary;
    @Mock
    private Context context;
    @Mock
    private android.content.Context context_;
    @Mock
    private AssetManager assetManager;
    @Mock
    private ANMService anmService;
    @Mock
    private ANM anm;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.anmService()).thenReturn(anmService);
        PowerMockito.when(anmService.fetchDetails()).thenReturn(anm);
        PowerMockito.when(anm.name()).thenReturn("anmId");
        PowerMockito.mockStatic(CloudantDataHandler.class);
        PowerMockito.when(CloudantDataHandler.getInstance(context_.getApplicationContext())).thenReturn(cloudantDataHandler);
        PowerMockito.when(cloudantDataHandler.createClientDocument(Mockito.any(Client.class))).thenReturn(null);
        PowerMockito.when(cloudantDataHandler.createEventDocument(Mockito.any(Event.class))).thenReturn(null);
        formjson = new JSONObject(formresultJson);

    }

    @Test
    public void assertFillAttributesReturnNotNull() throws Exception {
        Assert.assertNotNull(JsonFormUtils.extractAttributes(JsonFormUtils.fields(formjson)));
    }

    @Test
    public void assertFillAttributesWithBindTypeNotReturnNullReturnNotNull() throws Exception {
        JSONObject formjson = new JSONObject(formresultJson);
        Assert.assertNotNull(JsonFormUtils.extractAttributes(JsonFormUtils.fields(formjson),bindtype));
    }

    @Test
    public void assertExtracIdentifiersReturnNotNull() throws Exception {
        JSONObject formjson = new JSONObject(formresultJson);
        Assert.assertNotNull(JsonFormUtils.extractIdentifiers(JsonFormUtils.fields(formjson)));
    }

    @Test
    public void assertExtracIdentifiersWithBindTypeReturnNotNull() throws Exception {
        JSONObject formjson = new JSONObject(formresultJson);
        Assert.assertNotNull(JsonFormUtils.extractIdentifiers(JsonFormUtils.fields(formjson),bindtype));
    }

    @Test
    public void assertExtracAddressesReturnNotNull() throws Exception {
        JSONObject formjson = new JSONObject(formresultJson);
        Assert.assertNotNull(JsonFormUtils.extractAddresses(JsonFormUtils.fields(formjson)));
    }

    @Test
    public void assertExtracAddressesWithBindTypeReturnNotNull() throws Exception {
        JSONObject formjson = new JSONObject(formresultJson);
        Assert.assertNotNull(JsonFormUtils.extractAddresses(JsonFormUtils.fields(formjson),bindtype));
    }

    @Test
    public void formatDateReturnsinRequiredFormat() throws Exception {
        Date date = new Date();
        String dateInFormat = dd_MM_yyyy.format(date);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        String dateinFMT = fmt.format(dd_MM_yyyy.parse(dateInFormat));
        String returnedDateinString = (JsonFormUtils.formatDate(dateInFormat));
        Assert.assertEquals(dateinFMT,returnedDateinString);
    }

    @Test
    public void formatDateReturnsDate() throws Exception {
        SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dd_MM_yyyy.parse("22-05-1988");
        String dateInFormat = dd_MM_yyyy.format(date);
        String dateInFormat2 = yyyy_MM_dd.format(date);
        Date datereturned = (JsonFormUtils.formatDate(dateInFormat,true));
        Date datereturned2 = (JsonFormUtils.formatDate(dateInFormat2,true));
        long diff = date.getTime() - datereturned.getTime();
        long diff2 = date.getTime() - datereturned2.getTime();
        Assert.assertEquals(diff,0l);
        Assert.assertEquals(diff2,0l);
    }



}
