package org.smartregister.util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.clientandeventmodel.Address;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.exception.JsonFormMissingStepCountException;

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

    public static final String ENCOUNTER = "encounter";
    public static final String ENCOUNTER_LOCATION = "encounter_location";

    public static final SimpleDateFormat dd_MM_yyyy = new SimpleDateFormat("dd-MM-yyyy");
    //public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
    //2007-03-31T04:00:00.000Z
    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();

    public static Client createBaseClient(JSONArray fields, FormTag formTag, String entityId) {

        String firstName = getFieldValue(fields, FormEntityConstants.Person.first_name);
        String middleName = getFieldValue(fields, FormEntityConstants.Person.middle_name);
        String lastName = getFieldValue(fields, FormEntityConstants.Person.last_name);
        String bd = getFieldValue(fields, FormEntityConstants.Person.birthdate);
        Date birthdate = formatDate(bd, true);
        String dd = getFieldValue(fields, FormEntityConstants.Person.deathdate);
        Date deathdate = formatDate(dd, true);
        String aproxbd = getFieldValue(fields, FormEntityConstants.Person.birthdate_estimated);
        Boolean birthdateApprox = false;
        if (!StringUtils.isEmpty(aproxbd) && NumberUtils.isNumber(aproxbd)) {
            int birthDateEstimated = 0;
            try {
                birthDateEstimated = Integer.parseInt(aproxbd);
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
            birthdateApprox = birthDateEstimated > 0;
        }
        String aproxdd = getFieldValue(fields, FormEntityConstants.Person.deathdate_estimated);
        Boolean deathdateApprox = false;
        if (!StringUtils.isEmpty(aproxdd) && NumberUtils.isNumber(aproxdd)) {
            int deathDateEstimated = 0;
            try {
                deathDateEstimated = Integer.parseInt(aproxdd);
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
            deathdateApprox = deathDateEstimated > 0;
        }
        String gender = getFieldValue(fields, FormEntityConstants.Person.gender);

        List<Address> addresses = new ArrayList<>(extractAddresses(fields).values());

        Client client = (Client) new Client(entityId).withFirstName(firstName).withMiddleName(middleName).withLastName(lastName)
                .withBirthdate((birthdate), birthdateApprox).withDeathdate(deathdate, deathdateApprox).withGender(gender)
                .withDateCreated(new Date());

        client.setClientApplicationVersion(formTag.appVersion);
        client.setClientDatabaseVersion(formTag.databaseVersion);

        client.withRelationships(new HashMap<String, List<String>>()).withAddresses(addresses)
                .withAttributes(extractAttributes(fields)).withIdentifiers(extractIdentifiers(fields));
        return client;

    }

    public static Event createEvent(JSONArray fields, JSONObject metadata, FormTag formTag, String entityId, String encounterType, String bindType) {

        String encounterDateField = getFieldValue(fields, FormEntityConstants.Encounter.encounter_date);
        String encounterLocation = null;

        Date encounterDate = new Date();
        if (StringUtils.isNotBlank(encounterDateField)) {
            Date dateTime = formatDate(encounterDateField, false);
            if (dateTime != null) {
                encounterDate = dateTime;
            }
        }
        try {
            encounterLocation = metadata.getString(ENCOUNTER_LOCATION);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        if (StringUtils.isBlank(encounterLocation)) {
            encounterLocation = formTag.locationId;
        }

        String formSubmissionId = formTag != null && formTag.formSubmissionId != null ? formTag.formSubmissionId : generateRandomUUIDString();
        Event event =
                (Event) new Event().withBaseEntityId(entityId).withEventDate(encounterDate).withEventType(encounterType)
                        .withLocationId(encounterLocation).withProviderId(formTag.providerId).withEntityType(bindType)
                        .withFormSubmissionId(formSubmissionId).withDateCreated(new Date());

        event.setChildLocationId(formTag.childLocationId);
        event.setTeam(formTag.team);
        event.setTeamId(formTag.teamId);

        event.setClientApplicationVersion(formTag.appVersion);
        event.setClientDatabaseVersion(formTag.databaseVersion);

        for (int i = 0; i < fields.length(); i++) {
            JSONObject jsonObject = getJSONObject(fields, i);
            try {
                if (jsonObject.has(AllConstants.TYPE) &&
                        (AllConstants.NATIVE_RADIO.equals(jsonObject.getString(AllConstants.TYPE)) ||
                                AllConstants.ANC_RADIO_BUTTON.equals(jsonObject.getString(AllConstants.TYPE))) &&
                        jsonObject.has(AllConstants.EXTRA_REL) && jsonObject.getBoolean(AllConstants.EXTRA_REL) &&
                        jsonObject.has(AllConstants.HAS_EXTRA_REL)) {
                    String extraFieldsKey = jsonObject.getString(AllConstants.HAS_EXTRA_REL);
                    JSONArray options = jsonObject.getJSONArray(AllConstants.OPTIONS);
                    initiateOptionsObsCreation(event, extraFieldsKey, options);

                } else if (jsonObject.has(AllConstants.TYPE) &&
                        AllConstants.EXPANSION_PANEL.equals(jsonObject.getString(AllConstants.TYPE))) {
                    createObsFromPopUpValues(event, jsonObject, false);
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }

            try {
                if (!AllConstants.EXPANSION_PANEL.equals(jsonObject.getString(AllConstants.TYPE))) {
                    addObservation(event, jsonObject);
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        createFormMetadataObs(metadata, event);

        return event;

    }

    private static void initiateOptionsObsCreation(Event event, String extraFieldsKey, JSONArray options) throws JSONException {
        for (int k = 0; k < options.length(); k++) {
            JSONObject option = options.getJSONObject(k);
            if (option.has(KEY) && extraFieldsKey.equals(option.getString(KEY)) &&
                    option.has(AllConstants.SECONDARY_VALUE)) {
                createObsFromPopUpValues(event, option, true);
            }
        }
    }

    private static void createFormMetadataObs(JSONObject metadata, Event event) {
        if (metadata != null) {
            Iterator<?> keys = metadata.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                JSONObject jsonObject = getJSONObject(metadata, key);
                String value = getString(jsonObject, VALUE);
                if (StringUtils.isNotBlank(value)) {
                    String entityVal = getString(jsonObject, OPENMRS_ENTITY);
                    if (entityVal != null) {
                        if (entityVal.equals(CONCEPT)) {
                            addToJSONObject(jsonObject, KEY, key);
                            addObservation(event, jsonObject);
                        } else if (entityVal.equals(ENCOUNTER)) {
                            String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
                            if (entityIdVal.equals(FormEntityConstants.Encounter.encounter_date.name())) {
                                Date eDate = formatDate(value, false);
                                if (eDate != null) {
                                    event.setEventDate(eDate);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void createObsFromPopUpValues(Event event, JSONObject jsonObject, boolean type) {
        try {
            JSONArray secondaryValues = new JSONArray();
            if (type) {
                if (jsonObject.has(AllConstants.SECONDARY_VALUE)) {
                    secondaryValues = jsonObject.getJSONArray(AllConstants.SECONDARY_VALUE);
                }
            } else {
                if (jsonObject.has(VALUE)) {
                    secondaryValues = jsonObject.getJSONArray(VALUE);
                }
            }

            for (int j = 0; j < secondaryValues.length(); j++) {
                JSONObject secondaryValue = secondaryValues.getJSONObject(j);
                JSONObject parentOpenMRSAttributes = new JSONObject();
                if (secondaryValue.has(AllConstants.OPENMRS_ATTRIBUTES)) {
                    parentOpenMRSAttributes = secondaryValue.getJSONObject(AllConstants.OPENMRS_ATTRIBUTES);
                }

                JSONArray valueOpenMRSAttributes;
                if (secondaryValue.has(KEY)) {
                    String secondaryValueKey = secondaryValue.getString(KEY);
                    String secondaryValueType = secondaryValue.getString(AllConstants.TYPE);

                    if (secondaryValue.has(AllConstants.VALUE_OPENMRS_ATTRIBUTES)) {
                        valueOpenMRSAttributes = secondaryValue.getJSONArray(AllConstants.VALUE_OPENMRS_ATTRIBUTES);

                        JSONObject checkBoxObsObject = new JSONObject();

                        for (int l = 0; l < valueOpenMRSAttributes.length(); l++) {
                            JSONObject valueOpenMRSAttribute = valueOpenMRSAttributes.getJSONObject(l);
                            JSONObject popupJson = new JSONObject();
                            if (valueOpenMRSAttribute.get(KEY).equals(secondaryValueKey)) {
                                if (AllConstants.CHECK_BOX.equals(secondaryValueType)) {
                                    checkBoxObsObject =
                                            getCheckBoxJsonObjects(event, parentOpenMRSAttributes, valueOpenMRSAttributes,
                                                    secondaryValueKey, secondaryValueType, checkBoxObsObject, l,
                                                    valueOpenMRSAttribute, popupJson);
                                } else {
                                    popupJson.put(KEY, secondaryValueKey);
                                    popupJson.put(OPENMRS_ENTITY, CONCEPT);
                                    popupJson.put(OPENMRS_ENTITY_ID, parentOpenMRSAttributes.getString(OPENMRS_ENTITY_ID));
                                    popupJson.put(VALUE, valueOpenMRSAttribute.getString(OPENMRS_ENTITY_ID));
                                    popupJson.put(AllConstants.TYPE, secondaryValueType);

                                    if (AllConstants.NATIVE_RADIO.equals(secondaryValueType) ||
                                            AllConstants.ANC_RADIO_BUTTON.equals(secondaryValueType)) {
                                        popupJson.put(OPENMRS_ENTITY_PARENT, "");
                                    } else if (AllConstants.SPINNER.equals(secondaryValueType)) {
                                        popupJson.put(OPENMRS_ENTITY_PARENT,
                                                parentOpenMRSAttributes.getString(OPENMRS_ENTITY_ID));
                                    }
                                    addObservation(event, popupJson);
                                }
                            }
                        }
                    } else {
                        JSONObject otherWidgetObject = new JSONObject();
                        JSONArray values = secondaryValue.getJSONArray(VALUES);
                        String value = "";
                        if (values != null) {
                            String valueString = values.getString(0);
                            String[] valueStringArray = valueString.split(":");
                            if (valueStringArray.length > 1) {
                                value = valueStringArray[1];
                            } else {
                                value = valueStringArray[0];
                            }
                        }


                        otherWidgetObject
                                .put(OPENMRS_ENTITY_PARENT, parentOpenMRSAttributes.getString(OPENMRS_ENTITY_PARENT));
                        otherWidgetObject.put(OPENMRS_ENTITY_ID, parentOpenMRSAttributes.getString(OPENMRS_ENTITY_ID));
                        otherWidgetObject.put(OPENMRS_ENTITY, parentOpenMRSAttributes.getString(OPENMRS_ENTITY));
                        otherWidgetObject.put(VALUE, value);
                        otherWidgetObject.put(AllConstants.TYPE, secondaryValueType);
                        otherWidgetObject.put(KEY, secondaryValue.getString(KEY));
                        addObservation(event, otherWidgetObject);


                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @NonNull
    private static JSONObject getCheckBoxJsonObjects(Event event, JSONObject parentOpenMRSAttributes,
                                                     JSONArray valueOpenMRSAttributes, String secondaryValueKey,
                                                     String secondaryValueType, JSONObject checkBoxObsObject, int l,
                                                     JSONObject valueOpenMRSAttribute, JSONObject popupJson)
            throws JSONException {
        JSONObject checkBoxOptionsObject = checkBoxObsObject;
        checkBoxOptionsObject.put(KEY, secondaryValueKey);
        checkBoxOptionsObject.put(OPENMRS_ENTITY, CONCEPT);
        checkBoxOptionsObject.put(OPENMRS_ENTITY_ID, parentOpenMRSAttributes.getString(OPENMRS_ENTITY_ID));
        checkBoxOptionsObject.put(OPENMRS_ENTITY_PARENT, parentOpenMRSAttributes.getString(OPENMRS_ENTITY_PARENT));
        popupJson.put(OPENMRS_ENTITY_PARENT, valueOpenMRSAttribute.getString(OPENMRS_ENTITY_PARENT));
        popupJson.put(OPENMRS_ENTITY_ID, valueOpenMRSAttribute.getString(OPENMRS_ENTITY_ID));
        popupJson.put(OPENMRS_ENTITY, valueOpenMRSAttribute.getString(OPENMRS_ENTITY));
        popupJson.put(VALUE, true);

        if (checkBoxOptionsObject.has(AllConstants.OPTIONS)) {
            JSONArray values = checkBoxOptionsObject.getJSONArray(AllConstants.OPTIONS);
            values.put(popupJson);
            checkBoxOptionsObject.put(AllConstants.OPTIONS, values);
        } else {
            JSONArray values = new JSONArray();
            values.put(popupJson);
            checkBoxOptionsObject.put(AllConstants.OPTIONS, values);
        }

        checkBoxOptionsObject.put(AllConstants.TYPE, secondaryValueType);
        checkBoxOptionsObject.put(VALUE, "value");

        if ((valueOpenMRSAttributes.length() - 1) == l) {
            addObservation(event, checkBoxOptionsObject);
            checkBoxOptionsObject = new JSONObject();
        }
        return checkBoxOptionsObject;
    }

    public static void addObservation(Event e, JSONObject jsonObject) {
        String value = getString(jsonObject, VALUE);
        String type = getString(jsonObject, AllConstants.TYPE);
        String entity = CONCEPT;
        if (StringUtils.isNotBlank(value)) {
            if (AllConstants.CHECK_BOX.equals(type)) {
                try {
                    if (jsonObject.has(AllConstants.OPTIONS)) {
                        JSONArray conceptsOptions = jsonObject.getJSONArray(AllConstants.OPTIONS);
                        for (int i = 0; i < conceptsOptions.length(); i++) {
                            JSONObject option = conceptsOptions.getJSONObject(i);
                            boolean optionValue = option.getBoolean(VALUE);
                            if (optionValue) {
                                option.put(AllConstants.TYPE, type);
                                option.put(AllConstants.PARENT_ENTITY_ID, jsonObject.getString(OPENMRS_ENTITY_ID));
                                option.put(KEY, jsonObject.getString(KEY));
                                createObservation(e, option, String.valueOf(option.getBoolean(VALUE)), entity);
                            }
                        }
                    }
                } catch (JSONException e1) {
                    Log.e(TAG, e1.getMessage());
                }
            } else {
                createObservation(e, jsonObject, value, entity);
            }
        }
    }

    private static void createObservation(Event e, JSONObject jsonObject, String value, String entity) {
        List<Object> vall = new ArrayList<>();

        String formSubmissionField = getString(jsonObject, KEY);
        String obsValue = value;

        String dataType = getString(jsonObject, OPENMRS_DATA_TYPE);
        if (StringUtils.isBlank(dataType)) {
            dataType = AllConstants.TEXT;
        }

        if (dataType.equals(AllConstants.DATE) && StringUtils.isNotBlank(obsValue)) {
            String newValue = convertToOpenMRSDate(obsValue);
            if (newValue != null) {
                obsValue = newValue;
            }
        }

        String entityVal = getString(jsonObject, OPENMRS_ENTITY);

        if (entityVal != null && entityVal.equals(entity)) {
            String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
            String entityParentVal = getString(jsonObject, OPENMRS_ENTITY_PARENT);

            List<Object> humanReadableValues = new ArrayList<>();

            JSONArray values = getJSONArray(jsonObject, VALUES);
            String widgetType = getString(jsonObject, AllConstants.TYPE);
            if (AllConstants.CHECK_BOX.equals(widgetType)) {
                entityIdVal = getString(jsonObject, AllConstants.PARENT_ENTITY_ID);
                entityParentVal = getString(jsonObject, AllConstants.PARENT_ENTITY_ID);
                vall.add(getString(jsonObject, OPENMRS_ENTITY_ID));
                if (jsonObject.has(AllConstants.TEXT)) {
                    humanReadableValues.add(getString(jsonObject, AllConstants.TEXT));
                }
            } else if ((AllConstants.NATIVE_RADIO.equals(widgetType) || AllConstants.ANC_RADIO_BUTTON.equals(widgetType)) &&
                    jsonObject.has(AllConstants.OPTIONS)) {
                try {
                    JSONArray options = getJSONArray(jsonObject, AllConstants.OPTIONS);
                    for (int i = 0; i < options.length(); i++) {
                        JSONObject option = options.getJSONObject(i);
                        if (obsValue.equals(option.getString(KEY))) {
                            entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
                            entityParentVal = "";
                            vall.add(option.getString(OPENMRS_ENTITY_ID));
                        }
                    }
                } catch (JSONException e1) {
                    Log.e(TAG, e1.getMessage());
                }
            } else {
                if (values != null && values.length() > 0) {
                    JSONObject choices = getJSONObject(jsonObject, OPENMRS_CHOICE_IDS);
                    String chosenConcept = getString(choices, obsValue);
                    vall.add(chosenConcept);
                    humanReadableValues.add(obsValue);
                } else {
                    vall.add(obsValue);
                }
            }
            e.addObs(new Obs(CONCEPT, dataType, entityIdVal, entityParentVal, vall, humanReadableValues, null,
                    formSubmissionField));
        } else if (StringUtils.isBlank(entityVal)) {
            vall.add(obsValue);

            e.addObs(new Obs("formsubmissionField", dataType, formSubmissionField, "", vall, new ArrayList<>(), null,
                    formSubmissionField));
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
                } else if (addressField.equalsIgnoreCase("district") || addressField.equalsIgnoreCase("county") ||
                        addressField.equalsIgnoreCase("county_district") ||
                        addressField.equalsIgnoreCase("countyDistrict")) {
                    ad.setCountyDistrict(value);
                } else if (addressField.equalsIgnoreCase("city") || addressField.equalsIgnoreCase("village") ||
                        addressField.equalsIgnoreCase("cityVillage") || addressField.equalsIgnoreCase("city_village")) {
                    ad.setCityVillage(value);
                } else if (addressField.equalsIgnoreCase("state") || addressField.equalsIgnoreCase("state_province") ||
                        addressField.equalsIgnoreCase("stateProvince")) {
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
            if (entityVal != null && entityVal.equals(person.entity()) && entityIdVal != null &&
                    entityIdVal.equals(person.name())) {
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
                } else if (addressField.equalsIgnoreCase("district") || addressField.equalsIgnoreCase("county") ||
                        addressField.equalsIgnoreCase("county_district") ||
                        addressField.equalsIgnoreCase("countyDistrict")) {
                    ad.setCountyDistrict(value);
                } else if (addressField.equalsIgnoreCase("city") || addressField.equalsIgnoreCase("village") ||
                        addressField.equalsIgnoreCase("cityVillage") || addressField.equalsIgnoreCase("city_village")) {
                    ad.setCityVillage(value);
                } else if (addressField.equalsIgnoreCase("state") || addressField.equalsIgnoreCase("state_province") ||
                        addressField.equalsIgnoreCase("stateProvince")) {
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
     * Returns a JSONArray of all the forms fields in a multi step form.
     *
     * @param jsonForm {@link JSONObject}
     * @return fields {@link JSONArray}
     * @author dubdabasoduba
     */
    public static JSONArray getMultiStepFormFields(JSONObject jsonForm) throws JsonFormMissingStepCountException {
        JSONArray fields = new JSONArray();
        try {
            if (jsonForm.has(AllConstants.COUNT)) {
                int stepCount = Integer.parseInt(jsonForm.getString(AllConstants.COUNT));
                for (int i = 0; i < stepCount; i++) {
                    String stepName = AllConstants.STEP + (i + 1);
                    JSONObject step = jsonForm.has(stepName) ? jsonForm.getJSONObject(stepName) : null;
                    if (step != null && step.has(FIELDS)) {
                        JSONArray stepFields = step.getJSONArray(FIELDS);
                        for (int k = 0; k < stepFields.length(); k++) {
                            JSONObject field = stepFields.getJSONObject(k);
                            fields.put(field);
                        }
                    }
                }
            } else {
                throw new JsonFormMissingStepCountException("The form step count is needed for the fields to be fetched");
            }

        } catch (JSONException e) {
            Log.e(TAG, "", e);
        }
        return fields;
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

    public static JSONObject toJSONObject(String jsonString) {
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
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

    public static String getFieldValue(String jsonString, String key) {
        JSONObject jsonForm = toJSONObject(jsonString);
        if (jsonForm == null) {
            return null;
        }

        JSONArray fields = fields(jsonForm);
        if (fields == null) {
            return null;
        }

        return getFieldValue(fields, key);

    }

    public static JSONObject getFieldJSONObject(JSONArray jsonArray, String key) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = getJSONObject(jsonArray, i);
            String keyVal = getString(jsonObject, KEY);
            if (keyVal != null && keyVal.equals(key)) {
                return jsonObject;
            }
        }
        return null;
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

    public static String getString(String jsonString, String field) {
        return getString(toJSONObject(jsonString), field);
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
