package org.smartregister.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.clientandeventmodel.Address;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.exception.JsonFormMissingStepCountException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.smartregister.util.JsonFormUtils.dd_MM_yyyy;

/**
 * Created by kaderchowdhury on 14/11/17.
 */
public class JsonFormUtilsTest {

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
    private String formresultJson =
            "{\"count\":\"1\",\"mother\":{\"encounter_type\":\"New Woman Registration\"},\"entity_id\":\"\"," +
                    "\"relational_id\":\"\",\"step1\":{\"title\":\"Birth Registration\"," +
                    "\"fields\":[{\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"uploadButtonText\":\"শিশুর ছবি " +
                    "তুলুন \",\"openmrs_entity_parent\":\"\",\"type\":\"choose_image\",\"key\":\"Child_Photo\"}," +
                    "{\"default\":\"[\\\"Bangladesh\\\"]\"," +
                    "\"tree\":[{\"nodes\":[{\"nodes\":[{\"nodes\":[{\"nodes\":[{\"nodes\":[{\"nodes\":[{\"nodes\":[{\"key" +
                    "\":\"Kuptala:Ward1:1-KA:Abdur Rahim Memberer bari-Kholapar\",\"level\":\"\",\"name\":\"Abdur Rahim " +
                    "Memberer bari-Kholapar\"},{\"key\":\"Kuptala:Ward1:1-KA:Jaynal Abediner Bari-Jangalia\"," +
                    "\"level\":\"\",\"name\":\"Jaynal Abediner Bari-Jangalia\"},{\"key\":\"Kuptala:Ward1:1-KA:Narayon " +
                    "Cowdhurir bari-Kholapara\",\"level\":\"\",\"name\":\"Narayon Cowdhurir bari-Kholapara\"}]," +
                    "\"key\":\"Kuptala:Ward1:1-KA\",\"level\":\"\",\"name\":\"1-KA\"}],\"key\":\"Kuptala:Ward-1\"," +
                    "\"level\":\"\",\"name\":\"Ward-1\"},{\"nodes\":[{\"key\":\"Kuptala:Ward-2:1-KA\",\"level\":\"\"," +
                    "\"name\":\"1-KA\"}],\"key\":\"Kuptala:Ward-2\",\"level\":\"\",\"name\":\"Ward-2\"}]," +
                    "\"key\":\"Kuptala\",\"level\":\"\",\"name\":\"Kuptala\"}],\"key\":\"Gaibandha Sadar\",\"level\":\"\"," +
                    "\"name\":\"Gaibandha Sadar\"}],\"key\":\"Gaibandha\",\"level\":\"\",\"name\":\"Gaibandha\"}]," +
                    "\"key\":\"Rangpur\",\"level\":\"\",\"name\":\"Rangpur\"}],\"key\":\"Bangladesh\",\"level\":\"\"," +
                    "\"name\":\"Bangladesh\"}],\"v_required\":{\"value\":true,\"err\":\"Please enter the child's home " +
                    "facility\"},\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"value\":\"[\\\"Bangladesh\\\"," +
                    "\\\"Rangpur\\\",\\\"Gaibandha\\\",\\\"Gaibandha Sadar\\\",\\\"Kuptala\\\",\\\"Kuptala:Ward-1\\\"," +
                    "\\\"Kuptala:Ward1:1-KA\\\",\\\"Kuptala:Ward1:1-KA:Abdur Rahim Memberer bari-Kholapar\\\"]\"," +
                    "\"hint\":\"শিশুর EPI কেন্দ্র *\",\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"text\"," +
                    "\"type\":\"tree\",\"key\":\"HIE_FACILITIES\"},{\"read_only\":\"true\"," +
                    "\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the Child's ZEIR ID\"}," +
                    "\"openmrs_entity\":\"person_identifier\",\"openmrs_entity_id\":\"OpenMRS_ID\",\"value\":\"10177528\"," +
                    "\"hint\":\"নিবন্ধন নম্বর *\",\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\"," +
                    "\"key\":\"OpenMRS_ID\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a valid ID\"}}," +
                    "{\"value\":\"123123131231231233\",\"openmrs_entity\":\"person_attribute\"," +
                    "\"openmrs_entity_id\":\"Child_Birth_Certificate\",\"hint\":\"শিশুর জন্মনিবন্ধন নাম্বার\"," +
                    "\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"key\":\"Child_Birth_Certificate\"}," +
                    "{\"v_regex\":{\"value\":\"[A-Za-z\\\\s.-]*\",\"err\":\"Please enter a valid name\"}," +
                    "\"value\":\"hatim\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\"," +
                    "\"hint\":\"শিশুর নাম\",\"edit_type\":\"name\",\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\"," +
                    "\"key\":\"First_Name\"},{\"values\":[\"Male\",\"Female\"],\"v_required\":{\"value\":\"true\"," +
                    "\"err\":\"Please enter the sex\"},\"value\":\"Male\",\"openmrs_entity\":\"person\"," +
                    "\"openmrs_entity_id\":\"gender\",\"hint\":\"শিশুর লিঙ্গ *\",\"openmrs_entity_parent\":\"\"," +
                    "\"type\":\"spinner\",\"key\":\"Sex\"},{\"max_date\":\"today\",\"duration\":{\"label\":\"Age\"}," +
                    "\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the date of birth\"}," +
                    "\"value\":\"01-01-2017\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\"," +
                    "\"min_date\":\"today-5y\",\"hint\":\"শিশুর জন্ম তারিখ *\",\"openmrs_entity_parent\":\"\"," +
                    "\"type\":\"date_picker\",\"expanded\":false,\"key\":\"Date_Birth\"}," +
                    "{\"v_required\":{\"value\":\"false\",\"err\":\"Enter the child's birth weight\"}," +
                    "\"v_min\":{\"value\":\"0.1\",\"err\":\"Weight must be greater than 0\"},\"value\":\"5\"," +
                    "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
                    "\"hint\":\"জন্মের সময় ওজন (kg) *\",\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"text\"," +
                    "\"type\":\"edit_text\",\"key\":\"Birth_Weight\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a " +
                    "valid weight\"}},{\"household_id\":\"1ae4d826-ef15-45d5-a475-37e8b6791260\"," +
                    "\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother\\/guardian's first name\"}," +
                    "\"v_regex\":{\"value\":\"[A-Za-z\\\\s.-]*\",\"err\":\"Please enter a valid name\"},\"value\":\"Mohila" +
                    " Nutun\",\"openmrs_entity\":\"concept\"," +
                    "\"openmrs_entity_id\":\"1593AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"hint\":\"মা\\/অবিভাবকের নাম *\"," +
                    "\"look_up\":\"true\",\"edit_type\":\"name\",\"openmrs_entity_parent\":\"\",\"entity_id\":\"mother\"," +
                    "\"type\":\"edit_text\",\"key\":\"Mother_Guardian_First_Name\"},{\"v_max_length\":{\"value\":\"11\"," +
                    "\"err\":\"Please Enter 11 digit Mobile number\"},\"v_min_length\":{\"value\":\"11\",\"err\":\"Please " +
                    "Enter 11 digit Mobile number\"},\"v_regex\":{\"value\":\"(01[5-9][0-9]{8})|s*\",\"err\":\"Number must" +
                    " begin with 015, 016,017,018 or 019 and must be a total of 11 digits in length\"}," +
                    "\"value\":\"01918901991\",\"openmrs_entity\":\"concept\"," +
                    "\"openmrs_entity_id\":\"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"hint\":\"মা\\/অবিভাবকের মোবাইল " +
                    "নম্বর\",\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"key\":\"Mother_Guardian_Number\"," +
                    "\"v_numeric\":{\"value\":\"true\",\"err\":\"Number must begin with 095, 096, or 097 and must be a " +
                    "total of 11 digits in length\"}},{\"v_regex\":{\"value\":\"[A-Za-z\\\\s.-]*\",\"err\":\"Please enter " +
                    "a valid name\"},\"value\":\"mafinar\",\"openmrs_entity\":\"concept\"," +
                    "\"openmrs_entity_id\":\"1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"hint\":\"পিতা \\/ অবিভাবকের নাম\"," +
                    "\"edit_type\":\"name\",\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"text\"," +
                    "\"type\":\"edit_text\",\"key\":\"Father_Guardian_Name\"},{\"values\":[\"Health facility\",\"Home\"]," +
                    "\"v_required\":{\"value\":true,\"err\":\"Please enter the place of birth\"},\"value\":\"Home\"," +
                    "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
                    "\"hint\":\"জন্মস্থান ধরণ *\",\"openmrs_choice_ids\":{\"Health " +
                    "facility\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
                    "\"Home\":\"1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"openmrs_entity_parent\":\"\"," +
                    "\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"key\":\"Place_Birth\"}," +
                    "{\"v_required\":{\"value\":true,\"err\":\"Please enter the birth facility name\"},\"value\":\"\"," +
                    "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
                    "\"relevance\":{\"step1:Place_Birth\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"Health " +
                    "facility\\\")\"}},\"hint\":\"শিশুটি জন্মগ্রহণ করার সময় কোন স্বাস্থ্য কেন্দ্রের সুবিধা পেয়েছিল? *\"," +
                    "\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\"," +
                    "\"key\":\"Birth_Facility_Name\"},{\"v_required\":{\"value\":true,\"err\":\"Please specify the health " +
                    "facility the child was born in\"},\"value\":\"\",\"openmrs_entity\":\"concept\"," +
                    "\"openmrs_entity_id\":\"160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
                    "\"relevance\":{\"step1:Birth_Facility_Name\":{\"type\":\"string\",\"ex\":\"equalTo(., " +
                    "\\\"[\\\"Home\\\"]\\\")\"}},\"hint\":\"শিশুর জন্ম স্থান * *\",\"edit_type\":\"name\"," +
                    "\"openmrs_entity_parent\":\"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\"," +
                    "\"key\":\"Birth_Facility_Name_Other\"}]},\"encounter_type\":\"Birth Registration\"," +
                    "\"metadata\":{\"phonenumber\":{\"value\":\"15555215554\",\"openmrs_entity\":\"concept\"," +
                    "\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"," +
                    "\"openmrs_data_type\":\"phonenumber\"},\"subscriberid\":{\"value\":\"310260000000000\"," +
                    "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
                    "\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"subscriberid\"}," +
                    "\"start\":{\"value\":\"2018-01-01 18:00:49\",\"openmrs_entity\":\"concept\"," +
                    "\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"," +
                    "\"openmrs_data_type\":\"start\"},\"today\":{\"value\":\"01-01-2018\",\"openmrs_entity_parent\":\"\"," +
                    "\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"}," +
                    "\"look_up\":{\"entity_id\":\"mother\",\"value\":\"69d28d10-1b16-4c56-b8c3-5359a6ba77c4\"}," +
                    "\"encounter_location\":\"4c8cb044-7b15-40b7-8ca2-6eceaa6c4e9a\"," +
                    "\"simserial\":{\"value\":\"89014103211118510720\",\"openmrs_entity\":\"concept\"," +
                    "\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"," +
                    "\"openmrs_data_type\":\"simserial\"},\"end\":{\"value\":\"2018-01-01 18:01:41\"," +
                    "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
                    "\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"end\"}," +
                    "\"deviceid\":{\"value\":\"bded72fcd7e3a083\",\"openmrs_entity\":\"concept\"," +
                    "\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"," +
                    "\"openmrs_data_type\":\"deviceid\"}}}";
    private JSONObject formjson;
    private String bindtype = "ec_child";
    private String miltiStepForm =
            "{\n" + "  \"count\": \"2\",\n" + "  \"step1\": {\n" + "    \"title\": \"Demographic Info\",\n" +
                    "    \"next\": \"step2\",\n" + "    \"fields\": [\n" + "      {\n" +
                    "        \"key\": \"educ_level\",\n" + "        \"openmrs_entity_parent\": \"\",\n" +
                    "        \"openmrs_entity\": \"concept\",\n" +
                    "        \"openmrs_entity_id\": \"1712AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                    "        \"type\": \"native_radio\",\n" + "        \"label\": \"Highest level of school\",\n" +
                    "        \"label_text_style\": \"bold\",\n" + "        \"options\": [\n" + "          {\n" +
                    "            \"key\": \"none\",\n" + "            \"openmrs_entity_parent\": \"\",\n" +
                    "            \"openmrs_entity\": \"concept\",\n" +
                    "            \"openmrs_entity_id\": \"1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                    "            \"text\": \"None\"\n" + "          },\n" + "          {\n" +
                    "            \"key\": \"dont_know\",\n" + "            \"text\": \"Don't know\",\n" +
                    "            \"openmrs_entity_parent\": \"\",\n" + "            \"openmrs_entity\": \"concept\",\n" +
                    "            \"openmrs_entity_id\": \"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" + "          },\n" +
                    "          {\n" + "            \"key\": \"primary\",\n" + "            \"text\": \"Primary\",\n" +
                    "            \"openmrs_entity_parent\": \"\",\n" + "            \"openmrs_entity\": \"concept\",\n" +
                    "            \"openmrs_entity_id\": \"1713AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" + "          },\n" +
                    "          {\n" + "            \"key\": \"secondary\",\n" + "            \"text\": \"Secondary\",\n" +
                    "            \"openmrs_entity_parent\": \"\",\n" + "            \"openmrs_entity\": \"concept\",\n" +
                    "            \"openmrs_entity_id\": \"1714AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" + "          },\n" +
                    "          {\n" + "            \"key\": \"higher\",\n" + "            \"text\": \"Higher\",\n" +
                    "            \"openmrs_entity_parent\": \"\",\n" + "            \"openmrs_entity\": \"concept\",\n" +
                    "            \"openmrs_entity_id\": \"160292AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" + "          }\n" +
                    "        ],\n" + "        \"v_required\": {\n" + "          \"value\": true,\n" +
                    "          \"err\": \"Please specify your education level\"\n" + "        }\n" + "      }\n" +
                    "    ]\n" + "  },\n" + "  \"step2\": {\n" + "    \"title\": \"Current Pregnancy\",\n" +
                    "    \"fields\": [\n" + "      {\n" + "        \"key\": \"lmp_known\",\n" +
                    "        \"openmrs_entity_parent\": \"\",\n" + "        \"openmrs_entity\": \"concept\",\n" +
                    "        \"openmrs_entity_id\": \"165258AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                    "        \"type\": \"native_radio\",\n" + "        \"label\": \"LMP known?\",\n" +
                    "        \"label_text_style\": \"bold\",\n" + "        \"max_date\": \"today\",\n" +
                    "        \"options\": [\n" + "          {\n" + "            \"key\": \"yes\",\n" +
                    "            \"text\": \"Yes\",\n" + "            \"openmrs_entity_parent\": \"\",\n" +
                    "            \"openmrs_entity\": \"concept\",\n" +
                    "            \"openmrs_entity_id\": \"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                    "            \"specify_info\": \"specify date\",\n" +
                    "            \"specify_widget\": \"date_picker\",\n" + "            \"max_date\": \"today\",\n" +
                    "            \"min_date\": \"today-280d\"\n" + "          },\n" + "          {\n" +
                    "            \"key\": \"no\",\n" + "            \"text\": \"No\",\n" +
                    "            \"openmrs_entity_parent\": \"\",\n" + "            \"openmrs_entity\": \"concept\",\n" +
                    "            \"openmrs_entity_id\": \"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" + "          }\n" +
                    "        ],\n" + "        \"v_required\": {\n" + "          \"value\": true\n" + "        }\n" +
                    "      }\n" + "    ]\n" + "  }\n" + "}";

    private String eventFormFields = "[{\"key\":\"contact_reason\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160288AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
            "\"type\":\"native_radio\",\"label\":\"Reason for coming to facility\",\"label_text_style\":\"bold\"," +
            "\"options\":[{\"key\":\"specific_complaint\",\"text\":\"Specific complaint\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5219AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]," +
            "\"value\":\"specific_complaint\",\"v_required\":{\"value\":\"true\",\"err\":\"Reason for coming to facility " +
            "is required\"}},{\"key\":\"specific_complaint\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5219AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
            "\"type\":\"check_box\",\"label\":\"Specific complaint(s)\",\"label_text_style\":\"bold\"," +
            "\"text_color\":\"#000000\",\"exclusive\":[\"dont_know\",\"none\"]," +
            "\"options\":[{\"key\":\"abnormal_discharge\",\"text\":\"Abnormal vaginal discharge\",\"value\":false," +
            "\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"123395AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"altered_skin_color\"," +
            "\"text\":\"Jaundice\",\"value\":true,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"136443AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"value\":\"['altered_skin_color']\"}," +
            "{\"key\":\"specific_complaint_other\",\"openmrs_entity_parent\":\" 5219AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
            "\"type\":\"normal_edit_text\",\"edit_text_style\":\"bordered\",\"hint\":\"Specify\",\"value\":\"Not Sure what" +
            " to add\"},{\"key\":\"repiratory_exam\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\"," +
            "\"label\":\"Respiratory exam\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"extra_rel\":true," +
            "\"has_extra_rel\":\"resOne3\",\"options\":[{\"key\":\"resOne1\",\"text\":\"Not done\"," +
            "\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"136443AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"resOne2\",\"text\":\"Normal\"," +
            "\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"136453AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"resOne3\",\"text\":\"Abnormal\"," +
            "\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"100003AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"specify_info\":\"Specify\"," +
            "\"specify_info_color\":\"#b5b5b5\",\"specify_widget\":\"check_box\"," +
            "\"content_form\":\"child_enrollment_sub_form\",\"content_form_location\":\"\",\"secondary_suffix\":\"test\"," +
            "\"secondary_value\":[{\"key\":\"respiratory_exam_abnormal\",\"type\":\"check_box\"," +
            "\"values\":[\"slow_breathing:Slow breathing:true\",\"other:Other (specify):true\"]," +
            "\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}," +
            "\"value_openmrs_attributes\":[{\"key\":\"respiratory_exam_abnormal\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165269AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}," +
            "{\"key\":\"respiratory_exam_abnormal\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\"," +
            "\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"Place_Birth\"," +
            "\"type\":\"spinner\",\"values\":[\"Health facility\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}," +
            "\"value_openmrs_attributes\":[{\"key\":\"Place_Birth\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}," +
            "{\"key\":\"respiratory_exam_radio_button\",\"type\":\"native_radio\",\"values\":[\"1:Not done\"]," +
            "\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"165300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}," +
            "\"value_openmrs_attributes\":[{\"key\":\"respiratory_exam_radio_button\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165269AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}," +
            "{\"key\":\"respiratory_exam_abnormal_other\",\"type\":\"edit_text\",\"values\":[\"Very Sick\"]," +
            "\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"165300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}}]}],\"is-rule-check\":false," +
            "\"value\":\"resOne3\"},{\"key\":\"accordion_blood_type\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165269AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
            "\"text\":\"Blood Type test\",\"type\":\"expansion_panel\",\"display_bottom_section\":true," +
            "\"content_form\":\"tests_blood_type_sub_form\",\"container\":\"anc_test\"," +
            "\"value\":[{\"key\":\"respiratory_exam_abnormal\",\"type\":\"check_box\",\"values\":[\"slow_breathing:Slow " +
            "breathing:true\",\"other:Other (specify):true\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}," +
            "\"value_openmrs_attributes\":[{\"key\":\"respiratory_exam_abnormal\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165269AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}," +
            "{\"key\":\"respiratory_exam_abnormal\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\"," +
            "\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"Place_Birth\"," +
            "\"type\":\"spinner\",\"values\":[\"Health facility\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}," +
            "\"value_openmrs_attributes\":[{\"key\":\"Place_Birth\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}," +
            "{\"key\":\"respiratory_exam_radio_button\",\"type\":\"native_radio\",\"values\":[\"1:Not done\"]," +
            "\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"165300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}," +
            "\"value_openmrs_attributes\":[{\"key\":\"respiratory_exam_radio_button\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165269AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}," +
            "{\"key\":\"respiratory_exam_abnormal_other\",\"type\":\"edit_text\",\"values\":[\"Very Sick\"]," +
            "\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"165300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}}]}]";

    private String formMetaData = "{\n" + "    \"start\": {\n" + "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" + "      \"openmrs_data_type\": \"start\",\n" +
            "      \"openmrs_entity_id\": \"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"value\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" + "    },\n" + "    \"end\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" + "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"end\",\n" +
            "      \"openmrs_entity_id\": \"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"value\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" + "    },\n" + "    \"today\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" + "      \"openmrs_entity\": \"encounter\",\n" +
            "      \"openmrs_entity_id\": \"encounter_date\",\n" + "        \"value\":\"2019-03-13\"\n" + "    },\n" +
            "    \"deviceid\": {\n" + "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" + "      \"openmrs_data_type\": \"deviceid\",\n" +
            "      \"openmrs_entity_id\": \"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"value\":\"specific437598439875984\"\n" + "    },\n" + "    \"subscriberid\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" + "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"subscriberid\",\n" +
            "      \"openmrs_entity_id\": \"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"value\":\"4379857437985798437598\"\n" + "    },\n" + "    \"simserial\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" + "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"simserial\",\n" +
            "      \"openmrs_entity_id\": \"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"value\":\"326587364756436875643875\"\n" + "    },\n" + "    \"phonenumber\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" + "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"phonenumber\",\n" +
            "      \"openmrs_entity_id\": \"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"value\":\"+3u465632453\"\n" + "    },\n" + "    \"encounter_location\": \"\",\n" +
            "    \"look_up\": {\n" + "      \"entity_id\": \"\",\n" + "      \"value\": \"\"\n" + "    }\n" + "  }";

    private String clientFormFields ="[{\"key\":\"first_name\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"type\":\"edit_text\",\"hint\":\"First " +
            "name\",\"edit_type\":\"name\",\"value\":\"John\"},{\"key\":\"last_name\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"type\":\"edit_text\",\"hint\":\"Last " +
            "name\",\"edit_type\":\"name\",\"value\":\"Doe\"},{\"key\":\"gender\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"gender\",\"type\":\"hidden\",\"value\":\"F\"}," +
            "{\"key\":\"dob\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\"," +
            "\"openmrs_entity_id\":\"birthdate\",\"type\":\"hidden\",\"value\":\"12-12-1990\"},{\"key\":\"dob_unknown\"," +
            "\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate_estimated\"," +
            "\"type\":\"check_box\",\"options\":[{\"key\":\"dob_unknown\",\"text\":\"DOB unknown?\"," +
            "\"text_size\":\"18px\",\"value\":\"false\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\"," +
            "\"openmrs_entity_id\":\"\"}]},{\"key\":\"age\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"age\",\"type\":\"hidden\",\"value\":\"\"}," +
            "{\"key\":\"age_entered\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\"," +
            "\"openmrs_entity_id\":\"age\",\"type\":\"edit_text\",\"hint\":\"Age\"},{\"key\":\"home_address\"," +
            "\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"address2\"," +
            "\"type\":\"edit_text\",\"hint\":\"Home address\",\"edit_type\":\"name\",\"value\":\"Nairobi\"}]";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        formjson = new JSONObject(formresultJson);

    }

    @Test
    public void assertFillAttributesReturnNotNull() throws Exception {
        Assert.assertNotNull(JsonFormUtils.extractAttributes(JsonFormUtils.fields(formjson)));
    }

    @Test
    public void assertFillAttributesWithBindTypeNotReturnNullReturnNotNull() throws Exception {
        JSONObject formjson = new JSONObject(formresultJson);
        Assert.assertNotNull(JsonFormUtils.extractAttributes(JsonFormUtils.fields(formjson), bindtype));
    }

    @Test
    public void assertExtracIdentifiersReturnNotNull() throws Exception {
        JSONObject formjson = new JSONObject(formresultJson);
        Assert.assertNotNull(JsonFormUtils.extractIdentifiers(JsonFormUtils.fields(formjson)));
    }

    @Test
    public void assertExtracIdentifiersWithBindTypeReturnNotNull() throws Exception {
        JSONObject formjson = new JSONObject(formresultJson);
        Assert.assertNotNull(JsonFormUtils.extractIdentifiers(JsonFormUtils.fields(formjson), bindtype));
    }

    @Test
    public void assertExtracAddressesReturnNotNull() throws Exception {
        JSONObject formjson = new JSONObject(formresultJson);
        Assert.assertNotNull(JsonFormUtils.extractAddresses(JsonFormUtils.fields(formjson)));
    }

    @Test
    public void assertExtracAddressesWithBindTypeReturnNotNull() throws Exception {
        JSONObject formjson = new JSONObject(formresultJson);
        Assert.assertNotNull(JsonFormUtils.extractAddresses(JsonFormUtils.fields(formjson), bindtype));
    }

    @Test
    public void formatDateReturnsinRequiredFormat() throws Exception {
        Date date = new Date();
        String dateInFormat = dd_MM_yyyy.format(date);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        String dateinFMT = fmt.format(dd_MM_yyyy.parse(dateInFormat));
        String returnedDateinString = (JsonFormUtils.formatDate(dateInFormat));
        Assert.assertEquals(dateinFMT, returnedDateinString);
    }

    @Test
    public void formatDateReturnsDate() throws Exception {
        SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dd_MM_yyyy.parse("22-05-1988");
        String dateInFormat = dd_MM_yyyy.format(date);
        String dateInFormat2 = yyyy_MM_dd.format(date);
        Date datereturned = (JsonFormUtils.formatDate(dateInFormat, true));
        Date datereturned2 = (JsonFormUtils.formatDate(dateInFormat2, true));
        long diff = date.getTime() - datereturned.getTime();
        long diff2 = date.getTime() - datereturned2.getTime();
        Assert.assertEquals(diff, 0l);
        Assert.assertEquals(diff2, 0l);
    }

    @Test
    public void addObservationAddsObservationToEvent() throws Exception {
        String observationJsonObjectString =
                "{\"v_required\":{\"value\":\"false\",\"err\":\"Enter the child's birth weight\"},\"v_min\":{\"value\":\"0" +
                        ".1\",\"err\":\"Weight must be greater than 0\"},\"value\":\"5\",\"openmrs_entity\":\"concept\"," +
                        "\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"hint\":\"জন্মের সময় ওজন (kg) " +
                        "*\",\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\"," +
                        "\"key\":\"Birth_Weight\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid weight\"}}";
        JSONObject observationJsonObject = new JSONObject(observationJsonObjectString);
        org.smartregister.clientandeventmodel.Event event = Mockito.mock(org.smartregister.clientandeventmodel.Event.class);
        JsonFormUtils.addObservation(event, observationJsonObject);
        Mockito.verify(event, Mockito.atLeastOnce()).addObs(any(Obs.class));
    }

    @Test
    public void addObservationAddsObservationFormSubmissionFieldToEvent() throws Exception {
        String observationJsonObjectString =
                "{\"v_required\":{\"value\":\"false\",\"err\":\"Enter the child's birth weight\"},\"v_min\":{\"value\":\"0" +
                        ".1\",\"err\":\"Weight must be greater than 0\"},\"value\":\"5\",\"openmrs_entity\":\"\"," +
                        "\"openmrs_entity_id\":\"\",\"hint\":\"জন্মের সময় ওজন (kg) *\",\"openmrs_entity_parent\":\"\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"Birth_Weight\"," +
                        "\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid weight\"}}";
        JSONObject observationJsonObject = new JSONObject(observationJsonObjectString);
        org.smartregister.clientandeventmodel.Event event = Mockito.mock(org.smartregister.clientandeventmodel.Event.class);
        JsonFormUtils.addObservation(event, observationJsonObject);
        Mockito.verify(event, Mockito.atLeastOnce()).addObs(any(Obs.class));
    }

    @Test
    public void assertfilladdressStartDateAddsStartDate() throws Exception {
        String addressJsonWithStartDateString =
                "{\"value\":\"2017-05-22\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"startDate\"," +
                        "\"hint\":\"address of household start date\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"date\",\"type\":\"edit_text\",\"key\":\"address_start_date\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithStartDateString), addressHashMap);
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getStartDate().getTime() -
                org.smartregister.clientandeventmodel.DateUtil.parseDate("2017-05-22").getTime(), 0);
    }

    @Test
    public void assertfilladdressEndDateAddsEndDate() throws Exception {
        String addressJsonWithEndDateString =
                "{\"value\":\"2017-05-22\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"end_date\"," +
                        "\"hint\":\"address of household end date\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"date\",\"type\":\"edit_text\",\"key\":\"address_end_date\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithEndDateString), addressHashMap);
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getEndDate().getTime() -
                org.smartregister.clientandeventmodel.DateUtil.parseDate("2017-05-22").getTime(), 0);
    }

    @Test
    public void assertfilladdressLongitudeAddsLongitude() throws Exception {
        String addressJsonWithLongitudeString =
                "{\"value\":\"34.044494\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"longitude\"," +
                        "\"hint\":\"address of household longitude\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"address_longitude\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithLongitudeString), addressHashMap);
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getLongitude(), "34.044494");
    }

    @Test
    public void assertfilladdresslatitudeAddslatitude() throws Exception {
        String addressJsonWithStartlatitudeString =
                "{\"value\":\"34.044494\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"latitude\"," +
                        "\"hint\":\"address of household latitude\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"address_latitude\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithStartlatitudeString), addressHashMap);
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getLatitude(), "34.044494");
    }

    @Test
    public void assertfilladdressGeopointAddsGeopoint() throws Exception {
        String addressJsonWithGeopointString =
                "{\"value\":\"34.044494 -84.695704 4 76 = lat lon alt prec\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"geopoint\",\"hint\":\"address of household geopoint\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"address_geopoint\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithGeopointString), addressHashMap);
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getGeopoint(),
                "34.044494 -84.695704 4 76 = lat lon alt prec");
    }

    @Test
    public void assertfilladdressPostal_codeAddsPostal_code() throws Exception {
        String addressJsonWithStartPostal_code =
                "{\"value\":\"4021\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"postal_code\"," +
                        "\"hint\":\"address of household postal_code\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"postal_code\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithStartPostal_code), addressHashMap);
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getPostalCode(), "4021");
    }

    @Test
    public void assertfilladdressSub_townAddsSub_town() throws Exception {
        String addressJsonWithSub_townString =
                "{\"value\":\"Kotwali\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"sub_town\"," +
                        "\"hint\":\"address of household sub_town\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"sub_town\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithSub_townString), addressHashMap);
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getSubTown(), "Kotwali");
    }

    @Test
    public void assertfilladdressTownAddsTown() throws Exception {
        String addressJsonWithTownString =
                "{\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"town\"," +
                        "\"hint\":\"address of household town\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"town\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithTownString), addressHashMap);
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getTown(), "Chittagong");
    }

    @Test
    public void assertfilladdressSub_districtAddsSub_district() throws Exception {
        String addressJsonWithsub_districtString =
                "{\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"sub_district\"," +
                        "\"hint\":\"address of household sub_district\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"sub_district\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithsub_districtString), addressHashMap);
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getSubDistrict(), "Chittagong");
    }

    @Test
    public void assertfilladdressDistrictAddsDistrict() throws Exception {
        String addressJsonWithDistrictString =
                "{\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"district\"," +
                        "\"hint\":\"address of household district\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"district\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithDistrictString), addressHashMap);
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getCountyDistrict(), "Chittagong");
    }

    @Test
    public void assertfilladdressCityVillageAddsCityVillage() throws Exception {
        String addressJsonWithCityVillageString =
                "{\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"cityVillage\"," +
                        "\"hint\":\"address of household cityVillage\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"cityVillage\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithCityVillageString), addressHashMap);
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getCityVillage(), "Chittagong");
    }

    @Test
    public void assertfilladdressStateAddsState() throws Exception {
        String addressJsonWithStateString =
                "{\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"state\"," +
                        "\"hint\":\"address of household state\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"state\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithStateString), addressHashMap);
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getStateProvince(), "Chittagong");
    }

    @Test
    public void assertfilladdressCountryAddsCountry() throws Exception {
        String addressJsonWithCountryString =
                "{\"value\":\"Bangladesh\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"country\"," +
                        "\"hint\":\"address of household country\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"country\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithCountryString), addressHashMap);
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getCountry(), "Bangladesh");
    }

    @Test
    public void assertfillSubformaddressStartDateAddsStartDate() throws Exception {
        String addressJsonWithStartDateString =
                "{\"entity_id\":\"mother\",\"value\":\"2017-05-22\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"startDate\",\"hint\":\"address of household start date\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"date\"," +
                        "\"type\":\"edit_text\",\"key\":\"address_start_date\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithStartDateString), addressHashMap, "mother");
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getStartDate().getTime() -
                org.smartregister.clientandeventmodel.DateUtil.parseDate("2017-05-22").getTime(), 0);
    }

    @Test
    public void assertfillSubFormaddressEndDateAddsEndDate() throws Exception {
        String addressJsonWithEndDateString =
                "{\"entity_id\":\"mother\",\"value\":\"2017-05-22\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"end_date\",\"hint\":\"address of household end date\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"date\"," +
                        "\"type\":\"edit_text\",\"key\":\"address_end_date\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithEndDateString), addressHashMap, "mother");
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getEndDate().getTime() -
                org.smartregister.clientandeventmodel.DateUtil.parseDate("2017-05-22").getTime(), 0);
    }

    @Test
    public void assertfillSubFormaddressLongitudeAddsLongitude() throws Exception {
        String addressJsonWithLongitudeString =
                "{\"entity_id\":\"mother\",\"value\":\"34.044494\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"longitude\",\"hint\":\"address of household longitude\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"address_longitude\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithLongitudeString), addressHashMap, "mother");
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getLongitude(), "34.044494");
    }

    @Test
    public void assertfillSubFormaddresslatitudeAddslatitude() throws Exception {
        String addressJsonWithStartlatitudeString =
                "{\"entity_id\":\"mother\",\"value\":\"34.044494\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"latitude\",\"hint\":\"address of household latitude\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"address_latitude\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithStartlatitudeString), addressHashMap, "mother");
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getLatitude(), "34.044494");
    }

    @Test
    public void assertfillSubFormaddressGeopointAddsGeopoint() throws Exception {
        String addressJsonWithGeopointString =
                "{\"entity_id\":\"mother\",\"value\":\"34.044494 -84.695704 4 76 = lat lon alt prec\"," +
                        "\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"geopoint\",\"hint\":\"address of " +
                        "household geopoint\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"address_geopoint\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithGeopointString), addressHashMap, "mother");
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getGeopoint(),
                "34.044494 -84.695704 4 76 = lat lon alt prec");
    }

    @Test
    public void assertfillSubFormaddressPostal_codeAddsPostal_code() throws Exception {
        String addressJsonWithStartPostal_code =
                "{\"entity_id\":\"mother\",\"value\":\"4021\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"postal_code\",\"hint\":\"address of household postal_code\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"postal_code\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithStartPostal_code), addressHashMap, "mother");
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getPostalCode(), "4021");
    }

    @Test
    public void assertfillSubFormaddressSub_townAddsSub_town() throws Exception {
        String addressJsonWithSub_townString =
                "{\"entity_id\":\"mother\",\"value\":\"Kotwali\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"sub_town\",\"hint\":\"address of household sub_town\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"sub_town\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithSub_townString), addressHashMap, "mother");
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getSubTown(), "Kotwali");
    }

    @Test
    public void assertfillSubFormaddressTownAddsTown() throws Exception {
        String addressJsonWithTownString =
                "{\"entity_id\":\"mother\",\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"town\",\"hint\":\"address of household town\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"town\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithTownString), addressHashMap, "mother");
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getTown(), "Chittagong");
    }

    @Test
    public void assertfillSubformaddressSub_districtAddsSub_district() throws Exception {
        String addressJsonWithsub_districtString =
                "{\"entity_id\":\"mother\",\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"sub_district\",\"hint\":\"address of household sub_district\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"sub_district\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithsub_districtString), addressHashMap, "mother");
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getSubDistrict(), "Chittagong");
    }

    @Test
    public void assertfillSubformaddressDistrictAddsDistrict() throws Exception {
        String addressJsonWithDistrictString =
                "{\"entity_id\":\"mother\",\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"district\",\"hint\":\"address of household district\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"district\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithDistrictString), addressHashMap, "mother");
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getCountyDistrict(), "Chittagong");
    }

    @Test
    public void assertfillSubformaddressCityVillageAddsCityVillage() throws Exception {
        String addressJsonWithCityVillageString =
                "{\"entity_id\":\"mother\",\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"cityVillage\",\"hint\":\"address of household cityVillage\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"cityVillage\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithCityVillageString), addressHashMap, "mother");
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getCityVillage(), "Chittagong");
    }

    @Test
    public void assertfillSubformaddressStateAddsState() throws Exception {
        String addressJsonWithStateString =
                "{\"entity_id\":\"mother\",\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"state\",\"hint\":\"address of household state\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"state\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithStateString), addressHashMap, "mother");
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getStateProvince(), "Chittagong");
    }

    @Test
    public void assertfillSubformaddressCountryAddsCountry() throws Exception {
        String addressJsonWithCountryString =
                "{\"entity_id\":\"mother\",\"value\":\"Bangladesh\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"country\",\"hint\":\"address of household country\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"country\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithCountryString), addressHashMap, "mother");
        Assert.assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getCountry(), "Bangladesh");
    }

    @Test
    public void assertMergeWillMergeJsonObjects() throws Exception {
        String addressJsonWithCountryString =
                "{\"entity_id\":\"mother\",\"value\":\"Bangladesh\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"country\",\"hint\":\"address of household country\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"country\"}";
        String originalString =
                "{\"value\":\"Bangladesh\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"country\"," +
                        "\"hint\":\"address of household country\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"country\"}";
        JSONObject updatedExpected = new JSONObject(addressJsonWithCountryString);
        JSONObject original = new JSONObject(originalString);
        JSONObject toMerge = new JSONObject();
        toMerge.put("entity_id", "mother");
        JSONObject updated = JsonFormUtils.merge(original, toMerge);
        Assert.assertTrue(areEqual(updated, updatedExpected));

    }

    @Test
    public void testGetMultiStepFormFields() throws JSONException, JsonFormMissingStepCountException {
        Assert.assertNotNull(miltiStepForm);

        JSONObject jsonForm = new JSONObject(miltiStepForm);
        Assert.assertNotNull(jsonForm);

        JSONArray formFields = JsonFormUtils.getMultiStepFormFields(jsonForm);
        Assert.assertNotNull(formFields);
    }

    @Test(expected = JsonFormMissingStepCountException.class)
    public void testGetMultiStepFormFieldsWithoutFormCount() throws JSONException, JsonFormMissingStepCountException {
        String json = " {\n" + "    \"key\": \"lmp_known\",\n" + "    \"openmrs_entity_parent\": \"\",\n" +
                "    \"openmrs_entity\": \"concept\",\n" +
                "    \"openmrs_entity_id\": \"165258AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "    \"type\": \"native_radio\",\n" + "    \"label\": \"LMP known?\",\n" +
                "    \"label_text_style\": \"bold\",\n" + "    \"max_date\": \"today\",\n" + "    \"options\": [\n" +
                "      {\n" + "        \"key\": \"yes\",\n" + "        \"text\": \"Yes\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" + "        \"openmrs_entity\": \"concept\",\n" +
                "        \"openmrs_entity_id\": \"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "        \"specify_info\": \"specify date\",\n" + "        \"specify_widget\": \"date_picker\",\n" +
                "        \"max_date\": \"today\",\n" + "        \"min_date\": \"today-280d\"\n" + "      },\n" +
                "      {\n" + "        \"key\": \"no\",\n" + "        \"text\": \"No\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" + "        \"openmrs_entity\": \"concept\",\n" +
                "        \"openmrs_entity_id\": \"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" + "      }\n" + "    ],\n" +
                "    \"v_required\": {\n" + "      \"value\": true\n" + "    }\n" + "  }";
        JSONObject jsonForm = new JSONObject(json);
        Assert.assertNotNull(jsonForm);

        JSONArray formFields = JsonFormUtils.getMultiStepFormFields(jsonForm);
        Assert.assertNotNull(formFields);
        Assert.assertEquals(formFields.length(), 0);
    }

    @Test
    public void testEventCreationForForm() throws JSONException {
        JSONArray fields = new JSONArray(eventFormFields);
        Assert.assertNotNull(fields);

        JSONObject metadata = new JSONObject(formMetaData);
        Assert.assertNotNull(metadata);

        FormTag formTag = new FormTag();
        formTag.providerId = "52c9534da60e66bfc6d1641b3359894c";
        formTag.appVersion = 1;
        formTag.databaseVersion = 20;

        Assert.assertNotNull(formTag);

        org.smartregister.clientandeventmodel.Event event =
                JsonFormUtils.createEvent(fields, metadata, formTag, "97dc48f681ddcf188b2758fba89635fe", "Quick Check", "");
        Assert.assertNotNull(event.getEventType());
        Assert.assertEquals(event.getObs().size(), 20);
        Assert.assertEquals(event.getProviderId(), "52c9534da60e66bfc6d1641b3359894c");
    }

    @Test
    public void testClientCreation() throws JSONException {
        JSONArray fields = new JSONArray(clientFormFields);
        Assert.assertNotNull(fields);

        FormTag formTag = new FormTag();
        formTag.providerId = "52c9534da60e66bfc6d1641b3359894c";
        formTag.appVersion = 1;
        formTag.databaseVersion = 20;

        Assert.assertNotNull(formTag);
        Client client = JsonFormUtils.createBaseClient(fields,formTag,"97dc48f681ddcf188b2758fba89635fe");
        Assert.assertEquals(client.getGender(), "F" );
        Assert.assertEquals(client.getFirstName(), "John");
    }

    @Test
    public void testConvertToOpenMRSDate(){
        String date = "12-12-2019";
        String openmrsDate = JsonFormUtils.convertToOpenMRSDate(date);
        Assert.assertEquals(openmrsDate,"2019-12-12");
    }

    @Test
    public void testGetFieldJsonObject() throws  JSONException{
        JSONArray fields = new JSONArray(clientFormFields);
        Assert.assertNotNull(fields);

        JSONObject field = JsonFormUtils.getFieldJSONObject(fields,"first_name");
        Assert.assertNotNull(field);
        Assert.assertTrue(field.has("value"));
        Assert.assertEquals(field.get("value"), "John");
    }

    public static boolean areEqual(Object ob1, Object ob2) throws JSONException {
        Object obj1Converted = convertJsonElement(ob1);
        Object obj2Converted = convertJsonElement(ob2);
        return obj1Converted.equals(obj2Converted);
    }

    private static Object convertJsonElement(Object elem) throws JSONException {
        if (elem instanceof JSONObject) {
            JSONObject obj = (JSONObject) elem;
            Iterator<String> keys = obj.keys();
            Map<String, Object> jsonMap = new HashMap<>();
            while (keys.hasNext()) {
                String key = keys.next();
                jsonMap.put(key, convertJsonElement(obj.get(key)));
            }
            return jsonMap;
        } else if (elem instanceof JSONArray) {
            JSONArray arr = (JSONArray) elem;
            Set<Object> jsonSet = new HashSet<>();
            for (int i = 0; i < arr.length(); i++) {
                jsonSet.add(convertJsonElement(arr.get(i)));
            }
            return jsonSet;
        } else {
            return elem;
        }
    }

}
