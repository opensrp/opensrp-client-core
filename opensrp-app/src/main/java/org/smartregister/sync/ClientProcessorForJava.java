package org.smartregister.sync;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.db.Address;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.db.Obs;
import org.smartregister.domain.jsonmapping.ClassificationRule;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.ClientField;
import org.smartregister.domain.jsonmapping.Column;
import org.smartregister.domain.jsonmapping.JsonMapping;
import org.smartregister.domain.jsonmapping.Rule;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.util.AssetHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.smartregister.event.Event.FORM_SUBMITTED;

public class ClientProcessorForJava {
    private static final String TAG = ClientProcessorForJava.class.getName();

    protected static final String VALUES_KEY = "values";
    private static final String detailsUpdated = "detailsUpdated";

    private String[] openmrsGenIds = {};
    private Map<String, Object> jsonMap = new HashMap<>();

    private static ClientProcessorForJava instance;
    private Context mContext;

    public ClientProcessorForJava(Context context) {
        mContext = context;
    }

    public static ClientProcessorForJava getInstance(Context context) {
        if (instance == null) {
            instance = new ClientProcessorForJava(context);
        }

        return instance;
    }

    public synchronized void processClient(List<EventClient> eventClientList) throws Exception {

        final String EC_CLIENT_CLASSIFICATION = "ec_client_classification.json";
        ClientClassification clientClassification = assetJsonToJava(EC_CLIENT_CLASSIFICATION, ClientClassification.class);
        if (clientClassification == null) {
            return;
        }

        if (!eventClientList.isEmpty()) {
            for (EventClient eventClient : eventClientList) {
                // Iterate through the events
                if (eventClient.getClient() != null) {
                    processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
                }
            }
        }
    }

    public Boolean processEvent(Event event, Client client, ClientClassification clientClassification) throws Exception {
        try {
            if (event.getCreator() != null) {
                Log.i(TAG, "EVENT from openmrs");
            }
            // For data integrity check if a client exists, if not pull one from cloudant and
            // insert in drishti sqlite db

            if (client == null) {
                return false;
            }

            // Get the client type classification
            List<ClassificationRule> clientClasses = clientClassification.case_classification_rules;
            if (clientClasses == null || clientClasses.isEmpty()) {
                return false;
            }

            // Check if child is deceased and skip
            if (client.getDeathdate() != null) {
                return false;
            }

            for (ClassificationRule clientClass : clientClasses) {
                processClientClass(clientClass, event, client);
            }

            // Incase the details have not been updated
            String updatedString = event.getDetails() != null ? event.getDetails().get(detailsUpdated) : null;
            if (StringUtils.isBlank(updatedString) || !Boolean.TRUE.toString().equals(updatedString)) {
                updateClientDetailsTable(event, client);
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    public Boolean processClientClass(ClassificationRule clientClass, Event event, Client client) {
        try {
            if (clientClass == null) {
                return false;
            }

            if (event == null) {
                return false;
            }

            if (client == null) {
                return false;
            }

            Rule rule = clientClass.rule;
            List<org.smartregister.domain.jsonmapping.Field> fields = rule.fields;

            for (org.smartregister.domain.jsonmapping.Field field : fields) {
                processField(field, event, client);
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
            return null;
        }
    }

    public Boolean processField(org.smartregister.domain.jsonmapping.Field field, Event event, Client client) {
        try {
            if (field == null) {
                return false;
            }

            // keep checking if the event data matches the values expected by each rule, break the
            // moment the rule fails
            String dataSegment = null;
            String fieldName = field.field;
            String fieldValue = field.field_value;
            String responseKey = null;

            if (fieldName != null && fieldName.contains(".")) {
                String fieldNameArray[] = fieldName.split("\\.");
                dataSegment = fieldNameArray[0];
                fieldName = fieldNameArray[1];
                String concept = field.concept;

                if (concept != null) {
                    fieldValue = concept;
                    responseKey = VALUES_KEY;
                }
            }

            List<String> createsCase = field.creates_case;
            List<String> closesCase = field.closes_case;

            // some fields are in the main doc e.g event_type so fetch them from the main doc
            if (StringUtils.isNotBlank(dataSegment)) {
                List<String> responseValues = field.values;
                Object dataSegmentObject = getValue(event, dataSegment);
                if (dataSegmentObject != null) {
                    if (dataSegmentObject instanceof List) {

                        List dataSegmentList = (List) dataSegmentObject;
                        // Iterate in the segment e.g obs segment
                        for (Object segment : dataSegmentList) {
                            // let's discuss this further, to get the real value in the doc we've to
                            // use the keys 'fieldcode' and 'value'
                            Object value = getValue(segment, fieldName);
                            String docSegmentFieldValue = value != null ? value.toString() : "";
                            Object values = getValue(segment, responseKey);
                            List<String> docSegmentResponseValues = new ArrayList<>();
                            if (values instanceof List) {
                                docSegmentResponseValues = getValues((List) value);
                            }

                            if (docSegmentFieldValue.equalsIgnoreCase(fieldValue) && (!Collections
                                    .disjoint(responseValues, docSegmentResponseValues))) {
                                // this is the event obs we're interested in put it in the respective
                                // bucket specified by type variable
                                processCaseModel(event, client, createsCase);
                                closeCase(client, closesCase);
                            }

                        }
                    } else if (dataSegmentObject instanceof Map) {
                        Map map = (Map) dataSegmentObject;
                        // This means field_value and response_key are null so pick the
                        // value from the json object for the field_name
                        if (map.containsKey(fieldName)) {
                            Object objectValue = map.get(fieldName);
                            if (objectValue != null && objectValue instanceof String) {
                                String docSegmentFieldValue = objectValue.toString();
                                if (docSegmentFieldValue.equalsIgnoreCase(fieldValue)) {
                                    processCaseModel(event, client, createsCase);
                                    closeCase(client, closesCase);
                                }
                            }
                        }
                    }
                }

            } else {
                //fetch from the main doc
                Object value = getValue(event, fieldName);
                String docSegmentFieldValue = value != null ? value.toString() : "";
                if (docSegmentFieldValue.equalsIgnoreCase(fieldValue)) {
                    processCaseModel(event, client, createsCase);
                    closeCase(client, closesCase);
                }
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
            return null;
        }
    }

    public Boolean closeCase(Client client, List<String> closesCase) {
        try {
            if (closesCase == null || closesCase.isEmpty()) {
                return false;
            }

            String baseEntityId = client.getBaseEntityId();

            for (String tableName : closesCase) {
                closeCase(tableName, baseEntityId);
                updateFTSsearch(tableName, baseEntityId, null);
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
            return null;
        }
    }

    public Boolean processCaseModel(Event event, Client client, List<String> createsCase) {
        try {

            if (createsCase == null || createsCase.isEmpty()) {
                return false;
            }
            for (String clientType : createsCase) {
                Table table = getColumnMappings(clientType);
                List<Column> columns = table.columns;
                String baseEntityId = client != null ? client.getBaseEntityId() : event != null ? event.getBaseEntityId() : null;

                ContentValues contentValues = new ContentValues();
                //Add the base_entity_id
                contentValues.put("base_entity_id", baseEntityId);
                contentValues.put("is_closed", 0);

                for (Column colObject : columns) {
                    processCaseModel(event, client, colObject, contentValues);
                }

                // Modify openmrs generated identifier, Remove hyphen if it exists
                updateIdenitifier(contentValues);

                // save the values to db
                executeInsertStatement(contentValues, clientType);

                updateFTSsearch(clientType, baseEntityId, contentValues);
                Long timestamp = getEventDate(event.getEventDate());
                addContentValuesToDetailsTable(contentValues, timestamp);
                updateClientDetailsTable(event, client);
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);

            return null;
        }
    }

    public void processCaseModel(Event event, Client client, Column column, ContentValues contentValues) {
        try {
            String expectedEncounterType = event.getEventType();
            String docType = column.type;
            String columnName = column.column_name;
            JsonMapping jsonMapping = column.json_mapping;
            String dataSegment = null;
            String fieldName = jsonMapping.field;
            String fieldValue = null;
            String responseKey = null;

            String valueField = jsonMapping.value_field;

            if (fieldName != null && fieldName.contains(".")) {
                String fieldNameArray[] = fieldName.split("\\.");
                dataSegment = fieldNameArray[0];
                fieldName = fieldNameArray[1];
                fieldValue = StringUtils.isNotBlank(jsonMapping.concept) ? jsonMapping.concept
                        : (StringUtils.isNotBlank(jsonMapping.formSubmissionField) ? jsonMapping
                        .formSubmissionField : null);
                if (fieldValue != null) {
                    responseKey = VALUES_KEY;
                }
            }

            Object document = docType == null ? event : docType.equalsIgnoreCase("Event") ? event : client;

            Object docSegment;

            if (StringUtils.isNotBlank(dataSegment)) {
                // pick data from a specific section of the doc
                docSegment = getValue(document, dataSegment);
            } else {
                // else the use the main doc as the doc segment
                docSegment = document;
            }

            // special handler needed to process address,
            if (dataSegment != null && dataSegment.equalsIgnoreCase("addresses")) {
                Map<String, String> addressMap = getClientAddressAsMap(client);
                if (addressMap.containsKey(fieldName)) {
                    contentValues.put(columnName, addressMap.get(fieldName));
                }
                return;
            }

            // special handler for relationalid
            if (dataSegment != null && dataSegment.equalsIgnoreCase("relationships") && document instanceof Client) {
                Map<String, List<String>> relationshipMap = client.getRelationships();

                List<String> relationShipIds = relationshipMap.get(fieldName);
                if (relationShipIds != null && !relationShipIds.isEmpty()) {
                    contentValues.put(columnName, relationShipIds.get(0));
                }

                return;
            }

            String encounterType = jsonMapping.event_type;

            if (docSegment instanceof List) {

                List docSegmentList = (List) docSegment;

                for (Object segment : docSegmentList) {
                    String columnValue = null;

                    if (fieldValue == null) {
                        // This means field_value and response_key are null so pick the
                        // value from the json object for the field_name
                        columnValue = getValueAsString(segment, fieldName);
                    } else {
                        // this means field_value and response_key are not null e.g when
                        // retrieving some value in the events obs section
                        String expectedFieldValue = getValueAsString(segment, fieldName);
                        // some events can only be differentiated by the event_type value
                        // eg pnc1,pnc2, anc1,anc2
                        // check if encountertype (the one in ec_client_fields.json) is
                        // null or it matches the encounter type from the ec doc we're
                        // processing
                        boolean encounterTypeMatches =
                                (encounterType == null) || (encounterType
                                        .equalsIgnoreCase(expectedEncounterType));

                        if (encounterTypeMatches && expectedFieldValue
                                .equalsIgnoreCase(fieldValue)) {

                            if (StringUtils.isNotBlank(valueField)) {
                                columnValue = getValueAsString(segment, valueField);
                            }

                            if (columnValue == null) {
                                Object values = getValue(segment, responseKey);
                                if (values instanceof List) {
                                    columnValue = getValues((List) values).get(0);
                                }
                            }
                        }
                    }

                    // after successfully retrieving the column name and value store it
                    // in Content value
                    if (columnValue != null) {
                        columnValue = getHumanReadableConceptResponse(columnValue, segment);
                        contentValues.put(columnName, columnValue);
                    }
                }

            } else if (docSegment instanceof Map) {
                Map map = (Map) docSegment;
                // This means field_value and response_key are null so pick the
                // value from the json object for the field_name
                if (fieldValue == null && map.containsKey(fieldName)) {
                    Object mapValue = map.get(fieldName);
                    if (mapValue != null && mapValue instanceof String) {
                        String columnValue = getHumanReadableConceptResponse(mapValue.toString(), docSegment);
                        contentValues.put(columnName, columnValue);
                    }
                }
            } else {
                //e.g client attributes section
                String columnValue = getValueAsString(docSegment, fieldName);

                // after successfully retrieving the column name and value store it in
                // Content value
                if (columnValue != null) {
                    columnValue = getHumanReadableConceptResponse(columnValue,
                            docSegment);
                    contentValues.put(columnName, columnValue);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    /**
     * Save the populated content values to details table
     *
     * @param values
     * @param eventDate
     */
    protected void addContentValuesToDetailsTable(ContentValues values, Long eventDate) {
        try {
            String baseEntityId = values.getAsString("base_entity_id");

            for (String key : values.keySet()) {
                String value = values.getAsString(key);
                saveClientDetails(baseEntityId, key, value, eventDate);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    /**
     * Update the details table with the new info, All the obs are extracted and saved as key
     * value with the key being the formSubmissionField and value being the value field.
     * If the key value already exists then the row will simply be updated with the value if the
     * event date is most recent
     *
     * @param event
     * @param client
     */
    public void updateClientDetailsTable(Event event, Client client) {
        try {
            Log.d(TAG, "Started updateClientDetailsTable");

            String baseEntityId = client.getBaseEntityId();
            Long timestamp = getEventDate(event.getEventDate());

            Map<String, String> genderInfo = getGender(client);
            saveClientDetails(baseEntityId, genderInfo, timestamp);

            Map<String, String> addressInfo = getClientAddressAsMap(client);
            saveClientDetails(baseEntityId, addressInfo, timestamp);

            Map<String, String> attributes = getClientAttributes(client);
            saveClientDetails(baseEntityId, attributes, timestamp);

            Map<String, String> obs = getObsFromEvent(event);
            saveClientDetails(baseEntityId, obs, timestamp);

            event.addDetails(detailsUpdated, Boolean.TRUE.toString());

            Log.d(TAG, "Finished updateClientDetailsTable");
            // save the other misc, client info date of birth...
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    /**
     * Retrieve the obs as key value pair <br/>
     * Key being the formSubmissionField and value being entered value
     *
     * @param event
     * @return
     */
    private Map<String, String> getObsFromEvent(Event event) {
        Map<String, String> obsMap = new HashMap<String, String>();

        try {
            List<Obs> obsList = event.getObs();
            for (Obs obs : obsList) {
                List<String> values = getValues(obs.getValues());
                String key = obs.getFormSubmissionField();
                if (StringUtils.isNotBlank(key)) {
                    for (String conceptValue : values) {
                        String value = getHumanReadableConceptResponse(conceptValue, obs);
                        if (StringUtils.isNotBlank(value)) {
                            obsMap.put(key, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return obsMap;
    }

    private Map<String, String> getClientAttributes(Client client) {
        Map<String, String> attributes = new HashMap<>();
        try {
            Map<String, Object> clientAttributes = client.getAttributes();
            for (Map.Entry<String, Object> entry : clientAttributes.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof String) {
                    attributes.put(entry.getKey(), value.toString());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }

        return attributes;
    }

    private Map<String, String> getGender(Client client) {
        Map<String, String> map = new HashMap<String, String>();
        final String GENDER = "gender";
        try {
            String gender = client.getGender();
            if (StringUtils.isNotBlank(gender)) {
                map.put(GENDER, gender);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }

        return map;
    }

    public void saveClientDetails(String baseEntityId, Map<String, String> values, Long timestamp) {
        for (String key : values.keySet()) {
            String value = values.get(key);
            saveClientDetails(baseEntityId, key, value, timestamp);
        }
    }

    /**
     * Save a single details row to the db
     *
     * @param baseEntityId
     * @param key
     * @param value
     * @param timestamp
     */
    private void saveClientDetails(String baseEntityId, String key, String value, Long timestamp) {
        DetailsRepository detailsRepository = org.smartregister.CoreLibrary.getInstance().context().
                detailsRepository();
        detailsRepository.add(baseEntityId, key, value, timestamp);
    }


    /**
     * Get human readable values from the json doc humanreadablevalues key if the key is empty
     * return value
     *
     * @param value
     * @param object
     * @return
     * @throws Exception
     */
    protected String getHumanReadableConceptResponse(String value, Object object) {
        try {
            if (StringUtils.isBlank(value) || (object != null && !(object instanceof Obs))) {
                return value;
            }

            final String HUMAN_READABLE_VALUES = "humanReadableValues";
            List humanReadableValues = new ArrayList();
            Object humanReadableObject = getValue(object, HUMAN_READABLE_VALUES);
            if (humanReadableObject != null && humanReadableObject instanceof List) {
                humanReadableValues = (List) humanReadableObject;
            }

            if (object == null || humanReadableValues.isEmpty()) {
                String humanReadableValue = org.smartregister.CoreLibrary.getInstance().context().
                        customHumanReadableConceptResponse().get(value);

                if (StringUtils.isNotBlank(humanReadableValue)) {
                    return humanReadableValue;
                }

                return value;
            }

            return humanReadableValues.size() == 1 ? humanReadableValues.get(0).toString()
                    : humanReadableValues.toString();
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return value;
    }

    public Map<String, String> getClientAddressAsMap(Client client) {
        Map<String, String> addressMap = new HashMap<String, String>();
        if (client == null) {
            return addressMap;
        }
        try {
            final String addressFieldsKey = "addressFields";

            List<Address> addressList = client.getAddresses();
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                Map<String, String> addressFieldMap = address.getAddressFields();
                if (addressFieldMap != null) {
                    for (Map.Entry<String, String> entry : addressFieldMap.entrySet()) {
                        addressMap.put(entry.getKey(), entry.getValue());
                    }
                }

                List<Field> fields = getFields(address.getClass());
                for (Field classField : fields) {
                    String fieldName = classField.getName();
                    if (!fieldName.equals(addressFieldsKey)) {
                        String value = getValueAsString(address, classField.getName());
                        if (value != null) {
                            addressMap.put(classField.getName(), value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return addressMap;
    }

    /**
     * Insert the a new record to the database and returns its id
     **/
    public Long executeInsertStatement(ContentValues values, String tableName) {
        CommonRepository cr = org.smartregister.CoreLibrary.getInstance().context().commonrepository(tableName);
        return cr.executeInsertStatement(values, tableName);
    }

    public void closeCase(String tableName, String baseEntityId) {
        CommonRepository cr = org.smartregister.CoreLibrary.getInstance().context().commonrepository(tableName);
        cr.closeCase(baseEntityId, tableName);
    }

    public boolean deleteCase(String tableName, String baseEntityId) {
        CommonRepository cr = org.smartregister.CoreLibrary.getInstance().context().commonrepository(tableName);
        return cr.deleteCase(baseEntityId, tableName);
    }

    public Table getColumnMappings(String registerName) {
        try {
            ClientField clientField = assetJsonToJava("ec_client_fields.json", ClientField.class);
            if (clientField == null) {
                return null;
            }
            List<Table> bindObjects = clientField.bindobjects;
            for (Table bindObject : bindObjects) {
                if (bindObject.name.equalsIgnoreCase(registerName)) {
                    return bindObject;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return null;
    }

    protected <T> T assetJsonToJava(String fileName, Class<T> clazz) {
        return AssetHandler.assetJsonToJava(jsonMap, mContext, fileName, clazz);
    }

    protected List<String> getValues(List list) {
        List<String> values = new ArrayList<String>();
        if (list == null) {
            return values;
        }
        for (Object o : list) {
            if (o instanceof String) {
                values.add(o.toString());
            }
        }
        return values;
    }

    protected Object getValue(Object instance, String fieldName) {
        if (instance == null || StringUtils.isBlank(fieldName)) {
            return null;
        }
        try {
            Field field = getField(instance.getClass(), fieldName);
            if (field == null) {
                return null;
            }
            field.setAccessible(true);
            return field.get(instance);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    protected String getValueAsString(Object instance, String fieldName) {
        Object object = getValue(instance, fieldName);
        if (object != null) {
            return object.toString();
        }
        return null;
    }


    private long getEventDate(DateTime eventDate) {
        if (eventDate == null) {
            return new Date().getTime();
        } else {
            return eventDate.getMillis();
        }
    }

    private List<Field> getFields(Class clazz) {
        List<Field> fields = new ArrayList<>();
        if (instance == null) {
            return new ArrayList<>();
        }


        Class current = clazz;
        while (current != null) { // we don't want to process Object.class
            // do something with current's fields
            Field[] fieldArray = current.getDeclaredFields();
            if (fieldArray != null) {
                fields.addAll(Arrays.asList(fieldArray));
            }

            current = current.getSuperclass();
        }

        return fields;
    }

    private Field getField(Class clazz, String fieldName) {
        if (clazz == null || StringUtils.isBlank(fieldName)) {
            return null;
        }

        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            // No need to log this, log will be to big
        }
        if (field != null) {
            return field;
        }

        return getField(clazz.getSuperclass(), fieldName);
    }


    public void updateFTSsearch(String tableName, String entityId, ContentValues contentValues) {
        Log.d(TAG, "Starting updateFTSsearch table: " + tableName);
        AllCommonsRepository allCommonsRepository = org.smartregister.CoreLibrary.getInstance().context().
                allCommonsRepositoryobjects(tableName);

        if (allCommonsRepository != null) {
            allCommonsRepository.updateSearch(entityId);
            updateRegisterCount(entityId);
        }

        Log.d(TAG, "Finished updateFTSsearch table: " + tableName);
    }

    protected void updateRegisterCount(String entityId) {
        FORM_SUBMITTED.notifyListeners(entityId);
    }

    /**
     * Update given identifier, removes hyphen
     *
     * @param values
     */
    private void updateIdenitifier(ContentValues values) {
        try {
            for (String identifier : getOpenmrsGenIds()) {
                Object value = values.get(identifier);
                if (value != null && value instanceof String) {
                    String sValue = value.toString();
                    if (StringUtils.isNotBlank(sValue)) {
                        values.remove(identifier);
                        values.put(identifier, sValue.replace("-", ""));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    public Context getContext() {
        return mContext;
    }

    protected String[] getOpenmrsGenIds() {
        return openmrsGenIds;
    }
}
