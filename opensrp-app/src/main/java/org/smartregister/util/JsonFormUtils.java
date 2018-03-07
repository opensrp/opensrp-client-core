package org.smartregister.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Address;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.clientandeventmodel.Obs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by keyman on 08/02/2017.
 */
public class JsonFormUtils {
    public static final String TAG = "JsonFormUtils";

    public static final String OPENMRS_ENTITY = "openmrs_entity";
    public static final String OPENMRS_ENTITY_ID = "openmrs_entity_id";
    public static final String OPENMRS_ENTITY_PARENT = "openmrs_entity_parent";
    public static final String OPENMRS_CHOICE_IDS = "openmrs_choice_ids";
    public static final String OPENMRS_DATA_TYPE = "openmrs_data_type";

    public static final String PERSON_ATTRIBUTE = "person_attribute";
    public static final String PERSON_INDENTIFIER = "person_identifier";
    public static final String PERSON_ADDRESS = "person_address";

    public static final String CONCEPT = "concept";
    public static final String VALUE = "value";
    public static final String VALUES = "values";
    public static final String FIELDS = "fields";
    public static final String KEY = "key";
    public static final String ENTITY_ID = "entity_id";
    public static final String STEP1 = "step1";
    public static final String SECTIONS = "sections";
    public static final String attributes = "attributes";


    public static final SimpleDateFormat dd_MM_yyyy = new SimpleDateFormat("dd-MM-yyyy");
    //public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
    //2007-03-31T04:00:00.000Z
    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();

    public static void addObservation(Event e, JSONObject jsonObject) {
        String value = getString(jsonObject, VALUE);
        String entity = CONCEPT;
        if (StringUtils.isNotBlank(value)) {
            List<Object> vall = new ArrayList<>();

            String formSubmissionField = getString(jsonObject, KEY);

            String dataType = getString(jsonObject, OPENMRS_DATA_TYPE);
            if (StringUtils.isBlank(dataType)) {
                dataType = "text";
            }

            if (dataType.equals("date") && StringUtils.isNotBlank(value)) {
                String newValue = convertToOpenMRSDate(value);
                if (newValue != null) {
                    value = newValue;
                }
            }

            String entityVal = getString(jsonObject, OPENMRS_ENTITY);

            if (entityVal != null && entityVal.equals(entity)) {
                String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
                String entityParentVal = getString(jsonObject, OPENMRS_ENTITY_PARENT);

                List<Object> humanReadableValues = new ArrayList<>();

                JSONArray values = getJSONArray(jsonObject, VALUES);
                if (values != null && values.length() > 0) {
                    JSONObject choices = getJSONObject(jsonObject, OPENMRS_CHOICE_IDS);
                    String chosenConcept = getString(choices, value);
                    vall.add(chosenConcept);
                    humanReadableValues.add(value);
                } else {
                    vall.add(value);
                }

                e.addObs(new Obs(CONCEPT, dataType, entityIdVal,
                        entityParentVal, vall, humanReadableValues, null, formSubmissionField));
            } else if (StringUtils.isBlank(entityVal)) {
                vall.add(value);

                e.addObs(new Obs("formsubmissionField", dataType, formSubmissionField,
                        "", vall, new ArrayList<>(), null, formSubmissionField));
            }
        }
    }


    public static Map<String, String> extractIdentifiers(JSONArray fields) {
        Map<String, String> pids = new HashMap<>();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject jsonObject = getJSONObject(fields, i);
            fillIdentifiers(pids, jsonObject);
        }
        return pids;
    }

    public static Map<String, Object> extractAttributes(JSONArray fields) {
        Map<String, Object> pattributes = new HashMap<>();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject jsonObject = getJSONObject(fields, i);
            fillAttributes(pattributes, jsonObject);
        }

        return pattributes;
    }

    public static Map<String, Address> extractAddresses(JSONArray fields) {
        Map<String, Address> paddr = new HashMap<>();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject jsonObject = getJSONObject(fields, i);
            fillAddressFields(jsonObject, paddr);
        }
        return paddr;
    }


    public static void fillIdentifiers(Map<String, String> pids, JSONObject jsonObject) {

        String value = getString(jsonObject, VALUE);
        if (StringUtils.isBlank(value)) {
            return;
        }

        if (StringUtils.isNotBlank(getString(jsonObject, ENTITY_ID))) {
            return;
        }

        String entity = PERSON_INDENTIFIER;
        String entityVal = getString(jsonObject, OPENMRS_ENTITY);

        if (entityVal != null && entityVal.equals(entity)) {
            String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);

            pids.put(entityIdVal, value);
        }


    }

    public static void fillAttributes(Map<String, Object> pattributes, JSONObject jsonObject) {

        String value = getString(jsonObject, VALUE);
        if (StringUtils.isBlank(value)) {
            return;
        }

        if (StringUtils.isNotBlank(getString(jsonObject, ENTITY_ID))) {
            return;
        }

        String entity = PERSON_ATTRIBUTE;
        String entityVal = getString(jsonObject, OPENMRS_ENTITY);

        if (entityVal != null && entityVal.equals(entity)) {
            String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
            pattributes.put(entityIdVal, value);
        }
    }


    public static void fillAddressFields(JSONObject jsonObject, Map<String, Address> addresses) {

        if (jsonObject == null) {
            return;
        }

        try {

            String value = getString(jsonObject, VALUE);
            if (StringUtils.isBlank(value)) {
                return;
            }

            if (StringUtils.isNotBlank(getString(jsonObject, ENTITY_ID))) {
                return;
            }

            String entity = PERSON_ADDRESS;
            String entityVal = getString(jsonObject, OPENMRS_ENTITY);

            if (entityVal != null && entityVal.equalsIgnoreCase(entity)) {
                String addressType = getString(jsonObject, OPENMRS_ENTITY_PARENT);
                String addressField = getString(jsonObject, OPENMRS_ENTITY_ID);

                Address ad = addresses.get(addressType);
                if (ad == null) {
                    ad = new Address(addressType, null, null, null, null, null, null, null, null);
                }

                if (addressField.equalsIgnoreCase("startDate") || addressField.equalsIgnoreCase("start_date")) {
                    ad.setStartDate(DateUtil.parseDate(value));
                } else if (addressField.equalsIgnoreCase("endDate") || addressField.equalsIgnoreCase("end_date")) {
                    ad.setEndDate(DateUtil.parseDate(value));
                } else if (addressField.equalsIgnoreCase("latitude")) {
                    ad.setLatitude(value);
                } else if (addressField.equalsIgnoreCase("longitude")) {
                    ad.setLongitude(value);
                } else if (addressField.equalsIgnoreCase("geopoint")) {
                    // example geopoint 34.044494 -84.695704 4 76 = lat lon alt prec
                    String geopoint = value;
                    if (!StringUtils.isEmpty(geopoint)) {
                        String[] g = geopoint.split(" ");
                        ad.setLatitude(g[0]);
                        ad.setLongitude(g[1]);
                        ad.setGeopoint(geopoint);
                    }
                } else if (addressField.equalsIgnoreCase("postal_code") || addressField.equalsIgnoreCase("postalCode")) {
                    ad.setPostalCode(value);
                } else if (addressField.equalsIgnoreCase("sub_town") || addressField.equalsIgnoreCase("subTown")) {
                    ad.setSubTown(value);
                } else if (addressField.equalsIgnoreCase("town")) {
                    ad.setTown(value);
                } else if (addressField.equalsIgnoreCase("sub_district") || addressField.equalsIgnoreCase("subDistrict")) {
                    ad.setSubDistrict(value);
                } else if (addressField.equalsIgnoreCase("district") || addressField.equalsIgnoreCase("county")
                        || addressField.equalsIgnoreCase("county_district") || addressField.equalsIgnoreCase("countyDistrict")) {
                    ad.setCountyDistrict(value);
                } else if (addressField.equalsIgnoreCase("city") || addressField.equalsIgnoreCase("village")
                        || addressField.equalsIgnoreCase("cityVillage") || addressField.equalsIgnoreCase("city_village")) {
                    ad.setCityVillage(value);
                } else if (addressField.equalsIgnoreCase("state") || addressField.equalsIgnoreCase("state_province") || addressField.equalsIgnoreCase("stateProvince")) {
                    ad.setStateProvince(value);
                } else if (addressField.equalsIgnoreCase("country")) {
                    ad.setCountry(value);
                } else {
                    ad.addAddressField(addressField, value);
                }
                addresses.put(addressType, ad);
            }
        } catch (ParseException e) {
            Log.e(TAG, "", e);
        }
    }


    public static Map<String, String> extractIdentifiers(JSONArray fields, String bindType) {
        Map<String, String> pids = new HashMap<>();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject jsonObject = getJSONObject(fields, i);
            fillSubFormIdentifiers(pids, jsonObject, bindType);
        }
        return pids;
    }

    public static Map<String, Object> extractAttributes(JSONArray fields, String bindType) {
        Map<String, Object> pattributes = new HashMap<>();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject jsonObject = getJSONObject(fields, i);
            fillSubFormAttributes(pattributes, jsonObject, bindType);
        }
        return pattributes;
    }

    public static Map<String, Address> extractAddresses(JSONArray fields, String bindType) {
        Map<String, Address> paddr = new HashMap<>();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject jsonObject = getJSONObject(fields, i);
            fillSubFormAddressFields(jsonObject, paddr, bindType);
        }
        return paddr;
    }

    public static String getSubFormFieldValue(JSONArray jsonArray, FormEntityConstants.Person person, String bindType) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }

        if (person == null) {
            return null;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = getJSONObject(jsonArray, i);
            String bind = getString(jsonObject, ENTITY_ID);
            if (bind == null || !bind.equals(bindType)) {
                continue;
            }
            String entityVal = getString(jsonObject, OPENMRS_ENTITY);
            String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
            if (entityVal != null && entityVal.equals(person.entity()) && entityIdVal != null && entityIdVal.equals(person.name())) {
                return getString(jsonObject, VALUE);
            }

        }
        return null;
    }

    public static void fillSubFormIdentifiers(Map<String, String> pids, JSONObject jsonObject, String bindType) {

        String value = getString(jsonObject, VALUE);
        if (StringUtils.isBlank(value)) {
            return;
        }

        String bind = getString(jsonObject, ENTITY_ID);
        if (bind == null || !bind.equals(bindType)) {
            return;
        }

        String entity = PERSON_INDENTIFIER;
        String entityVal = getString(jsonObject, OPENMRS_ENTITY);

        if (entityVal != null && entityVal.equals(entity)) {
            String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
            pids.put(entityIdVal, value);
        }
    }

    public static void fillSubFormAttributes(Map<String, Object> pattributes, JSONObject jsonObject, String bindType) {

        String value = getString(jsonObject, VALUE);
        if (StringUtils.isBlank(value)) {
            return;
        }

        String bind = getString(jsonObject, ENTITY_ID);
        if (bind == null || !bind.equals(bindType)) {
            return;
        }

        String entity = PERSON_ATTRIBUTE;
        String entityVal = getString(jsonObject, OPENMRS_ENTITY);

        if (entityVal != null && entityVal.equals(entity)) {
            String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
            pattributes.put(entityIdVal, value);
        }
    }

    public static void fillSubFormAddressFields(JSONObject jsonObject, Map<String, Address> addresses, String bindType) {

        if (jsonObject == null) {
            return;
        }

        try {
            String value = getString(jsonObject, VALUE);
            if (StringUtils.isBlank(value)) {
                return;
            }

            String bind = getString(jsonObject, ENTITY_ID);
            if (bind == null || !bind.equals(bindType)) {
                return;
            }

            String entity = PERSON_ADDRESS;
            String entityVal = getString(jsonObject, OPENMRS_ENTITY);

            if (entityVal != null && entityVal.equalsIgnoreCase(entity)) {
                String addressType = getString(jsonObject, OPENMRS_ENTITY_PARENT);
                String addressField = getString(jsonObject, OPENMRS_ENTITY_ID);

                Address ad = addresses.get(addressType);
                if (ad == null) {
                    ad = new Address(addressType, null, null, null, null, null, null, null, null);
                }

                if (addressField.equalsIgnoreCase("startDate") || addressField.equalsIgnoreCase("start_date")) {
                    ad.setStartDate(DateUtil.parseDate(value));
                } else if (addressField.equalsIgnoreCase("endDate") || addressField.equalsIgnoreCase("end_date")) {
                    ad.setEndDate(DateUtil.parseDate(value));
                } else if (addressField.equalsIgnoreCase("latitude")) {
                    ad.setLatitude(value);
                } else if (addressField.equalsIgnoreCase("longitude")) {
                    ad.setLongitude(value);
                } else if (addressField.equalsIgnoreCase("geopoint")) {
                    // example geopoint 34.044494 -84.695704 4 76 = lat lon alt prec
                    String geopoint = value;
                    if (!StringUtils.isEmpty(geopoint)) {
                        String[] g = geopoint.split(" ");
                        ad.setLatitude(g[0]);
                        ad.setLongitude(g[1]);
                        ad.setGeopoint(geopoint);
                    }
                } else if (addressField.equalsIgnoreCase("postal_code") || addressField.equalsIgnoreCase("postalCode")) {
                    ad.setPostalCode(value);
                } else if (addressField.equalsIgnoreCase("sub_town") || addressField.equalsIgnoreCase("subTown")) {
                    ad.setSubTown(value);
                } else if (addressField.equalsIgnoreCase("town")) {
                    ad.setTown(value);
                } else if (addressField.equalsIgnoreCase("sub_district") || addressField.equalsIgnoreCase("subDistrict")) {
                    ad.setSubDistrict(value);
                } else if (addressField.equalsIgnoreCase("district") || addressField.equalsIgnoreCase("county")
                        || addressField.equalsIgnoreCase("county_district") || addressField.equalsIgnoreCase("countyDistrict")) {
                    ad.setCountyDistrict(value);
                } else if (addressField.equalsIgnoreCase("city") || addressField.equalsIgnoreCase("village")
                        || addressField.equalsIgnoreCase("cityVillage") || addressField.equalsIgnoreCase("city_village")) {
                    ad.setCityVillage(value);
                } else if (addressField.equalsIgnoreCase("state") || addressField.equalsIgnoreCase("state_province") || addressField.equalsIgnoreCase("stateProvince")) {
                    ad.setStateProvince(value);
                } else if (addressField.equalsIgnoreCase("country")) {
                    ad.setCountry(value);
                } else {
                    ad.addAddressField(addressField, value);
                }
                addresses.put(addressType, ad);
            }
        } catch (ParseException e) {
            Log.e(TAG, "", e);
        }
    }

    // Helper functions

    public static JSONArray fields(JSONObject jsonForm) {
        try {

            JSONObject step1 = jsonForm.has(STEP1) ? jsonForm.getJSONObject(STEP1) : null;
            if (step1 == null) {
                return null;
            }

            return step1.has(FIELDS) ? step1.getJSONArray(FIELDS) : null;

        } catch (JSONException e) {
            Log.e(TAG, "", e);
        }
        return null;
    }

    /**
     * return field values that are in sections e.g for the hia2 monthly draft form which has sections
     *
     * @param jsonForm
     * @return
     */
    public static Map<String, String> sectionFields(JSONObject jsonForm) {
        try {

            JSONObject step1 = jsonForm.has(STEP1) ? jsonForm.getJSONObject(STEP1) : null;
            if (step1 == null) {
                return null;
            }

            JSONArray sections = step1.has(SECTIONS) ? step1.getJSONArray(SECTIONS) : null;
            if (sections == null) {
                return null;
            }

            Map<String, String> result = new HashMap<>();
            for (int i = 0; i < sections.length(); i++) {
                JSONObject sectionsJSONObject = sections.getJSONObject(i);
                if (sectionsJSONObject.has(FIELDS)) {
                    JSONArray fieldsArray = sectionsJSONObject.getJSONArray(FIELDS);
                    for (int j = 0; j < fieldsArray.length(); j++) {
                        JSONObject fieldJsonObject = fieldsArray.getJSONObject(j);
                        String key = fieldJsonObject.getString(KEY);
                        String value = fieldJsonObject.getString(VALUE);
                        result.put(key, value);

                    }
                }

            }
            return result;

        } catch (JSONException e) {
            Log.e(TAG, "", e);
            return null;
        }

    }

    public static String getFieldValue(JSONArray jsonArray, FormEntityConstants.Person person) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }

        if (person == null) {
            return null;
        }

        return value(jsonArray, person.entity(), person.entityId());
    }

    public static String getFieldValue(JSONArray jsonArray, FormEntityConstants.Encounter encounter) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }

        if (encounter == null) {
            return null;
        }

        return value(jsonArray, encounter.entity(), encounter.entityId());
    }

    public static String value(JSONArray jsonArray, String entity, String entityId) {

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = getJSONObject(jsonArray, i);
            if (StringUtils.isNotBlank(getString(jsonObject, ENTITY_ID))) {
                continue;
            }
            String entityVal = getString(jsonObject, OPENMRS_ENTITY);
            String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
            if (entityVal != null && entityVal.equals(entity) && entityIdVal != null && entityIdVal.equals(entityId)) {
                return getString(jsonObject, VALUE);
            }

        }
        return null;
    }

    public static String getFieldValue(JSONArray jsonArray, String key) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = getJSONObject(jsonArray, i);
            String keyVal = getString(jsonObject, KEY);
            if (keyVal != null && keyVal.equals(key)) {
                return getString(jsonObject, VALUE);
            }
        }
        return null;
    }

    public static JSONObject getJSONObject(JSONArray jsonArray, int index) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }

        try {
            return jsonArray.getJSONObject(index);
        } catch (JSONException e) {
            return null;

        }
    }

    public static JSONArray getJSONArray(JSONObject jsonObject, String field) {
        if (jsonObject == null || jsonObject.length() == 0) {
            return null;
        }

        try {
            return jsonObject.getJSONArray(field);
        } catch (JSONException e) {
            return null;

        }
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String field) {
        if (jsonObject == null || jsonObject.length() == 0) {
            return null;
        }

        try {
            return jsonObject.getJSONObject(field);
        } catch (JSONException e) {
            return null;

        }
    }

    public static String getString(JSONObject jsonObject, String field) {
        if (jsonObject == null) {
            return null;
        }

        try {
            return jsonObject.has(field) ? jsonObject.getString(field) : null;
        } catch (JSONException e) {
            return null;

        }
    }

    public static Long getLong(JSONObject jsonObject, String field) {
        if (jsonObject == null) {
            return null;
        }

        try {
            return jsonObject.has(field) ? jsonObject.getLong(field) : null;
        } catch (JSONException e) {
            return null;

        }
    }

    public static Date formatDate(String dateString, boolean startOfToday) {
        try {

            if (StringUtils.isBlank(dateString)) {
                return null;
            }

            if (dateString.matches("\\d{2}-\\d{2}-\\d{4}")) {
                return dd_MM_yyyy.parse(dateString);
            } else if (dateString.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return DateUtil.parseDate(dateString);
            }

        } catch (ParseException e) {
            Log.e(TAG, "", e);
        }

        return null;
    }

    public static String generateRandomUUIDString() {
        return UUID.randomUUID().toString();
    }

    public static void addToJSONObject(JSONObject jsonObject, String key, String value) {
        try {
            if (jsonObject == null) {
                return;
            }

            jsonObject.put(key, value);
        } catch (JSONException e) {
            Log.e(TAG, "", e);
        }
    }

    public static String formatDate(String date) throws ParseException {
        Date inputDate = dd_MM_yyyy.parse(date);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        return fmt.format(inputDate);
    }

    public static JSONObject merge(JSONObject original, JSONObject updated) {
        JSONObject mergedJSON = new JSONObject();
        try {
            mergedJSON = new JSONObject(original, getNames(original));
            for (String key : getNames(updated)) {
                mergedJSON.put(key, updated.get(key));
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return mergedJSON;
    }

    public static String[] getNames(JSONObject jo) {
        int length = jo.length();
        if (length == 0) {
            return null;
        }
        Iterator i = jo.keys();
        String[] names = new String[length];
        int j = 0;
        while (i.hasNext()) {
            names[j] = (String) i.next();
            j += 1;
        }
        return names;
    }

    public static String convertToOpenMRSDate(String value) {
        if (value.matches("\\d{4}-\\d{2}-\\d{2}")) { // already in openmrs date format
            return value;
        }

        try {
            Date date = formatDate(value, false);
            if (date != null) {
                return DateUtil.yyyyMMdd.format(date);
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        return null;
    }

}
