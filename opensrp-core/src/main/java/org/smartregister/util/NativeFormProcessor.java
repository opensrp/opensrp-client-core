package org.smartregister.util;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.util.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.BuildConfig;
import org.smartregister.CoreLibrary;
import org.smartregister.NativeFormFieldProcessor;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.Location;
import org.smartregister.domain.Task;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class NativeFormProcessor {
    private JSONObject jsonForm;
    private AllSharedPreferences allSharedPreferences;
    private Gson gson;
    private JSONArray _fields;
    private String entityId;
    private String formSubmissionId;
    private Client _client;
    private Event _event;
    private String encounterType;
    private String bindType;
    private org.smartregister.domain.Client domainClient;
    private org.smartregister.domain.Event domainEvent;
    private boolean hasClient = false;
    private FormTag formTag;
    private final int databaseVersion;
    private final ClientProcessorForJava clientProcessorForJava;
    private Map<String, NativeFormFieldProcessor> fieldProcessorMap = null;

    private static final String DETAILS = "details";
    private static final String METADATA = "metadata";


    public static NativeFormProcessor createInstance(String jsonString, int databaseVersion, ClientProcessorForJava clientProcessorForJava) throws JSONException {
        return new NativeFormProcessor(new JSONObject(jsonString), databaseVersion, clientProcessorForJava);
    }

    public static NativeFormProcessor createInstance(JSONObject jsonObject, int databaseVersion, ClientProcessorForJava clientProcessorForJava) {
        return new NativeFormProcessor(jsonObject, databaseVersion, clientProcessorForJava);
    }

    public static NativeFormProcessor createInstanceFromAsset(String filePath, Integer databaseVersion, ClientProcessorForJava clientProcessorForJava) throws JSONException {
        String jsonString = Utils.readAssetContents(DrishtiApplication.getInstance().getContext().applicationContext()
                , filePath);
        return createInstance(jsonString, databaseVersion, clientProcessorForJava);
    }

    @VisibleForTesting
    public NativeFormProcessor(JSONObject jsonObject, int databaseVersion, ClientProcessorForJava clientProcessorForJava) {
        this.jsonForm = jsonObject;
        this.databaseVersion = databaseVersion;
        this.clientProcessorForJava = clientProcessorForJava;
        allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();
    }

    public NativeFormProcessor usingFormTag(FormTag formTag) {
        this.formTag = formTag;
        return this;
    }

    public NativeFormProcessor withEntityId(String entityId) {
        this.entityId = entityId;
        return this;
    }

    public NativeFormProcessor withFormSubmissionId(String formSubmissionId) {
        this.formSubmissionId = formSubmissionId;
        return this;
    }

    public NativeFormProcessor withEncounterType(String encounterType) {
        this.encounterType = encounterType;
        return this;
    }

    public NativeFormProcessor withBindType(String bindType) {
        this.bindType = bindType;
        return this;
    }

    public NativeFormProcessor hasClient(boolean hasClient) {
        this.hasClient = hasClient;
        return this;
    }

    public NativeFormProcessor withFieldProcessors(Map<String, NativeFormFieldProcessor> fieldProcessorMap) {
        this.fieldProcessorMap = fieldProcessorMap;
        return this;
    }

    private FormTag getFormTag() {
        if (formTag == null)
            formTag = JsonFormUtils.constructFormMetaData(allSharedPreferences, databaseVersion);

        return formTag;
    }

    private JSONObject getOrCreateDetailsNode() throws JSONException {
        JSONObject formData = jsonForm.has(DETAILS) ? jsonForm.getJSONObject(DETAILS) : null;
        if (formData == null) {
            formData = new JSONObject();
            formData.put(AllConstants.JSON.Property.FORM_VERSION, jsonForm.optString("form_version"));
            formData.put(AllConstants.JSON.Property.APP_VERSION_NAME, BuildConfig.VERSION_NAME);

            jsonForm.put(DETAILS, formData);
        }

        return formData;
    }

    public NativeFormProcessor tagTaskDetails(Task task) throws JSONException {
        JSONObject formData = getOrCreateDetailsNode();
        formData.put(AllConstants.TASK_IDENTIFIER, task.getIdentifier());
        formData.put(AllConstants.TASK_BUSINESS_STATUS, task.getBusinessStatus());
        formData.put(AllConstants.TASK_STATUS, task.getStatus());
        return this;
    }

    public NativeFormProcessor tagLocationData(Location location) throws JSONException {
        if (location == null) return this;

        JSONObject formData = getOrCreateDetailsNode();
        formData.put(AllConstants.LOCATION_ID, location.getId());
        formData.put(AllConstants.LOCATION_UUID, location.getId());
        formData.put(AllConstants.LOCATION_VERSION, location.getServerVersion().toString());
        return this;
    }

    public NativeFormProcessor tagFeatureId(String featureId) throws JSONException {
        JSONObject formData = getOrCreateDetailsNode();
        formData.put(AllConstants.STRUCTURE_ID, featureId);
        return this;
    }

    public NativeFormProcessor tagLocationId(String locationId) throws JSONException {
        jsonForm.put(AllConstants.LOCATION_ID, locationId);
        return this;
    }

    public NativeFormProcessor tagChildLocationId(String childLocationId) throws JSONException {
        jsonForm.put(AllConstants.CHILD_LOCATION_ID, childLocationId);
        return this;
    }

    private JSONArray getFields() throws JSONException {
        if (_fields == null)
            _fields = jsonForm.getJSONObject(JsonFormUtils.STEP1).getJSONArray(JsonFormUtils.FIELDS);

        return _fields;
    }

    private Client createClient() throws JSONException {
        if (hasClient && _client == null)
            _client = JsonFormUtils.createBaseClient(
                    getFields(),
                    getFormTag(),
                    entityId
            );

        return _client;
    }

    private Event createEvent() throws JSONException {
        if (_event == null) {
            _event = JsonFormUtils.createEvent(
                    getFields(),
                    JsonFormUtils.getJSONObject(jsonForm, METADATA),
                    getFormTag(),
                    entityId,
                    encounterType,
                    bindType,
                    fieldProcessorMap
            );

            if (jsonForm.has(DETAILS)) {
                Map<String, String> map = gson.fromJson(jsonForm.getJSONObject(DETAILS).toString(), Map.class);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    _event.addDetails(entry.getKey(), entry.getValue());
                }
            }

            if (StringUtils.isNotBlank(formSubmissionId)) {
                _event.setFormSubmissionId(formSubmissionId);
                _event.setDateEdited(new Date());
            }
        }

        return _event;
    }

    public NativeFormProcessor tagEventMetadata() throws JSONException {
        JsonFormUtils.tagSyncMetadata(getFormTag(), createEvent());
        return this;
    }

    private org.smartregister.domain.Client getDomainClient() throws JSONException {
        if (hasClient && domainClient == null) {
            JSONObject clientJson = new JSONObject(gson.toJson(createClient()));
            domainClient = gson.fromJson(clientJson.toString(), org.smartregister.domain.Client.class);
        }

        return domainClient;
    }

    private org.smartregister.domain.Event getDomainEvent() throws JSONException {
        if (domainEvent == null) {
            JSONObject eventJson = new JSONObject(gson.toJson(createEvent()));
            domainEvent = gson.fromJson(eventJson.toString(), org.smartregister.domain.Event.class);
        }

        return domainEvent;
    }

    public NativeFormProcessor saveEvent() throws JSONException {
        JSONObject eventJson = new JSONObject(gson.toJson(createEvent()));
        getSyncHelper().addEvent(createEvent().getBaseEntityId(), eventJson);
        return this;
    }

    /**
     * when updating client info
     *
     * @return
     * @throws JSONException
     */
    public NativeFormProcessor mergeAndSaveClient() throws JSONException {
        JSONObject updatedClientJson = new JSONObject(JsonFormUtils.gson.toJson(createClient()));

        JSONObject originalClientJsonObject = getSyncHelper().getClient(createClient().getBaseEntityId());

        JSONObject mergedJson = new JSONObject(updatedClientJson.toString());

        mergeJsonObject(mergedJson, originalClientJsonObject);

        getSyncHelper().addClient(createClient().getBaseEntityId(), mergedJson);
        return this;
    }

    /**
     * when updating client info
     *
     * @return
     * @throws JSONException
     */
    public NativeFormProcessor updateAndSaveClient(Consumer<Client> consumer) throws JSONException {
        Client client = createClient();
        if (consumer != null)
            consumer.accept(client);

        JSONObject updatedClientJson = new JSONObject(JsonFormUtils.gson.toJson(client));

        JSONObject originalClientJsonObject = getSyncHelper().getClient(createClient().getBaseEntityId());

        JSONObject mergedJson = new JSONObject(updatedClientJson.toString());

        mergeJsonObject(mergedJson, originalClientJsonObject);

        getSyncHelper().addClient(createClient().getBaseEntityId(), mergedJson);
        return this;
    }

    private void mergeJsonObject(JSONObject updated, JSONObject original) throws JSONException {
        Iterator<String> keys = original.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            if (key.equals("_rev") || key.equals("serverVersion")) continue;

            if (original.get(key) instanceof JSONObject) {
                // do something with jsonObject here
                if (!updated.has(key))
                    updated.put(key, new JSONObject());

                JSONObject updatedJSONObject = updated.getJSONObject(key);
                JSONObject originalJSONObject = original.getJSONObject(key);

                mergeJsonObject(updatedJSONObject, originalJSONObject);

            } else if (original.get(key) instanceof String && !updated.has(key)) {
                updated.put(key, original.getString(key));
            } else if (original.get(key) instanceof JSONArray && !updated.has(key)) {
                updated.put(key, original.getJSONArray(key));
            }
        }
    }

    /**
     * Save clients
     *
     * @param consumer
     * @return
     * @throws JSONException
     */
    public NativeFormProcessor saveClient(Consumer<Client> consumer) throws JSONException {
        Client client = createClient();
        if (consumer != null)
            consumer.accept(client);

        JSONObject clientJson = new JSONObject(gson.toJson(client));
        getSyncHelper().addClient(createClient().getBaseEntityId(), clientJson);
        return this;
    }

    public NativeFormProcessor clientProcessForm() throws Exception {
        EventClient eventClient = new EventClient(getDomainEvent(), getDomainClient());

        clientProcessorForJava.processClient(Collections.singletonList(eventClient), true);
        long lastSyncTimeStamp = allSharedPreferences.fetchLastUpdatedAtDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);
        allSharedPreferences.saveLastUpdatedAtDate(lastSyncDate.getTime());
        return this;
    }

    public NativeFormProcessor closeRegistrationID(String uniqueIDKey) throws JSONException {
        JSONObject field = JsonFormUtils.getFieldJSONObject(JsonFormUtils.fields(jsonForm), uniqueIDKey);
        if (field != null) {
            String uniqueID = field.getString(JsonFormUtils.VALUE);
            getUniqueIdRepository().close(uniqueID);
        }
        return this;
    }

    public UniqueIdRepository getUniqueIdRepository() {
        return CoreLibrary.getInstance().context().getUniqueIdRepository();
    }

    public String getFieldValue(String fieldKey) throws JSONException {
        String value = null;

        JSONObject field = JsonFormUtils.getFieldJSONObject(JsonFormUtils.fields(jsonForm), fieldKey);
        if (field != null && field.has(JsonFormUtils.VALUE))
            value = field.getString(JsonFormUtils.VALUE);

        return StringUtils.isBlank(value) ? "" : value;
    }

    public ECSyncHelper getSyncHelper() {
        return ECSyncHelper.getInstance(DrishtiApplication.getInstance().getContext().applicationContext());
    }

    public Map<String, Object> getFormResults(JSONObject processedForm) throws JSONException {
        Map<String, Object> result = new HashMap<>();

        JSONArray obs = processedForm.getJSONArray("obs");
        int pos = 0;
        int total = obs.length();

        Map<String, JSONArray> jsonResults = new HashMap<>();
        while (pos < total) {
            JSONObject jsonObject = obs.getJSONObject(pos);
            String key = jsonObject.getString("formSubmissionField");

            JSONArray humanReadableValues = jsonObject.getJSONArray("humanReadableValues");
            JSONArray values = jsonObject.getJSONArray("values");

            JSONArray value = humanReadableValues.length() == values.length() ? humanReadableValues : values;

            JSONArray newValue = jsonResults.get(key);
            if (newValue != null) {
                for (int i = 0; i < value.length(); i++) {
                    newValue.put(value.get(i));
                }
            } else {
                newValue = value;
            }

            jsonResults.put(key, newValue);

            pos++;
        }

        for (Map.Entry<String, JSONArray> entry : jsonResults.entrySet()) {
            if (entry.getValue().length() == 1) {
                result.put(entry.getKey(), entry.getValue().getString(0));
            } else if (entry.getValue().length() > 1) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    // backward compatibility
    public <T> NativeFormProcessor populateValues(Map<String, T> dictionary) throws JSONException {
        populateValues(dictionary, null, null);
        return this;
    }

    // backward compatibility
    public <T> NativeFormProcessor populateValues(Map<String, T> dictionary, Consumer<JSONObject> consumer) throws JSONException {
        populateValues(dictionary, consumer, null);
        return this;
    }

    public <T> NativeFormProcessor populateValues(Map<String, T> dictionary, @Nullable Consumer<JSONObject> consumer, @Nullable Map<String, NativeFormProcessorFieldSource> fieldSourceMap) throws JSONException {
        int step = 1;
        while (jsonForm.has("step" + step)) {
            String stepName = "step" + step;
            JSONObject jsonStepObject = jsonForm.getJSONObject(stepName);
            JSONArray array = jsonStepObject.getJSONArray(JsonFormUtils.FIELDS);
            int position = 0;
            while (position < array.length()) {
                JSONObject object = array.getJSONObject(position);

                if (consumer != null)
                    consumer.accept(object);

                if (object.has(JsonFormUtils.KEY)) {
                    String key = object.getString(JsonFormUtils.KEY);
                    String type = object.has(JsonFormUtils.TYPE) ? object.getString(JsonFormUtils.TYPE) : "";

                    if (fieldSourceMap != null && fieldSourceMap.get(type) != null) {
                        fieldSourceMap.get(type).populateValue(stepName, jsonStepObject, object, dictionary);
                    } else if (dictionary.containsKey(key)) {
                        Object val = type.equals(JsonFormUtils.CHECK_BOX) ? getCheckBoxValues(key, dictionary, object) :
                                dictionary.get(key);

                        if (val != null)
                            object.put(JsonFormUtils.VALUE, val);
                    }

                }

                position++;
            }

            step++;
        }
        return this;
    }

    /**
     * This function receives a combination of a NativeForms field json and a dictionary of values.
     * The values are clustered by the last alphanumeric combination of values, i.e a UUID stripped of "-".
     * The values are the returned in a 2 dimensional map format of the different repeating groups.
     *
     * @param fieldJson  A NativeForms Field
     * @param dictionary A map containing key value pairs of fieldCode and value(can be an array or String)
     * @param <T>
     * @return
     * @throws JSONException
     */
    public <T> Map<String, Map<String, T>> getRepeatingGroupValues(JSONObject fieldJson, Map<String, T> dictionary) throws JSONException {
        Map<String, Map<String, T>> result = new HashMap<>();

        JSONArray arrayValues = fieldJson.getJSONArray(JsonFormUtils.VALUE);

        Set<String> valueSet = new HashSet<>();
        int position = 0;
        while (position < arrayValues.length()) {
            valueSet.add(arrayValues.getJSONObject(position).getString(JsonFormUtils.KEY));
            position++;
        }

        Pattern numberPattern = Pattern.compile("(.)*(\\d)(.)*");
        Pattern letterPattern = Pattern.compile(".*[a-zA-Z]+.*");

        for (Map.Entry<String, T> entry : dictionary.entrySet()) {
            String valueKey = entry.getKey();
            String[] entryKeyArray = valueKey.split("_");

            // if last element is alpha numeric, and the combination is in the set add this guy as an element
            if (entryKeyArray.length > 1) {
                String entryKey = StringUtils.join(Arrays.copyOfRange(entryKeyArray, 0, entryKeyArray.length - 1), "_");
                String lastElement = entryKeyArray[entryKeyArray.length - 1];

                if (lastElement.length() > 9
                        && valueSet.contains(entryKey)
                        && numberPattern.matcher(lastElement).matches()
                        && letterPattern.matcher(lastElement).matches()
                ) {

                    Map<String, T> value = result.get(lastElement);
                    if (value == null) value = new HashMap<>();

                    value.put(entryKey, entry.getValue());
                    result.put(lastElement, value);
                }
            }

        }

        return result;
    }

    private <T> JSONArray getCheckBoxValues(String key, Map<String, T> dictionary, JSONObject object) throws JSONException {
        Map<String, String> options = getCheckBoxOptions(object);

        Object val = dictionary.get(key);
        JSONArray allOptions = val instanceof JSONArray ? (JSONArray) val : new JSONArray();
        if (val instanceof String)
            allOptions.put(val);

        int x = 0;
        int size = allOptions.length();

        JSONArray jsonArray = new JSONArray();
        while (x < size) {
            String content = allOptions.getString(x);
            if (options.containsKey(content))
                jsonArray.put(options.get(content));
            x++;
        }

        // single option value will have result to a true / false statement
        if (allOptions.length() == 1 && options.size() == 1 && allOptions.getString(0).trim().equals("true")) {
            jsonArray.put(options.entrySet().iterator().next().getValue());
        }

        return jsonArray;
    }

    private Map<String, String> getCheckBoxOptions(JSONObject object) throws JSONException {
        Map<String, String> options = new HashMap<>();
        JSONArray jsonArray = object.getJSONArray(JsonFormUtils.OPTIONS_FIELD_NAME);

        int pos = 0;
        int size = jsonArray.length();
        while (pos < size) {
            JSONObject opt = jsonArray.getJSONObject(pos);

            String text = opt.getString(JsonFormUtils.TEXT);
            String key = opt.getString(JsonFormUtils.KEY);

            options.put(text, key);
            pos++;
        }

        return options;
    }
}
