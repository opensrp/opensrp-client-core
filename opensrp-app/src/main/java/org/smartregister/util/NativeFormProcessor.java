package org.smartregister.util;

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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NativeFormProcessor {
    private JSONObject jsonForm;
    private AllSharedPreferences allSharedPreferences;
    private Gson gson;
    private JSONArray _fields;
    private String entityId;
    private Client _client;
    private Event _event;
    private String encounterType;
    private String bindType;
    private org.smartregister.domain.Client domainClient;
    private org.smartregister.domain.Event domainEvent;
    private boolean hasClient = false;
    private FormTag formTag;

    private static final String DETAILS = "details";
    private static final String METADATA = "metadata";


    public static NativeFormProcessor createInstance(String jsonString) throws JSONException {
        return new NativeFormProcessor(new JSONObject(jsonString));
    }

    public static NativeFormProcessor createInstance(JSONObject jsonObject) {
        return new NativeFormProcessor(jsonObject);
    }

    public static NativeFormProcessor createInstanceFromAsset(String filePath) throws JSONException {
        String jsonString = org.smartregister.util.Utils.readAssetContents(DrishtiApplication.getInstance().getContext().applicationContext()
                , filePath);
        return createInstance(jsonString);
    }

    @VisibleForTesting
    public NativeFormProcessor(JSONObject jsonObject) {
        this.jsonForm = jsonObject;
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

    private FormTag getFormTag(){
        if(formTag == null)
            formTag = JsonFormUtils.constructFormMetaData(allSharedPreferences, -1);

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
                    bindType
            );

            if (jsonForm.has(DETAILS)) {
                Map<String, String> map = gson.fromJson(jsonForm.getJSONObject(DETAILS).toString(), Map.class);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    _event.addDetails(entry.getKey(), entry.getValue());
                }
            }
        }

        return _event;
    }

    public NativeFormProcessor tagEventMetadata() throws JSONException {
        JsonFormUtils.tagSyncMetadata(formTag, createEvent());
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

        ClientProcessorForJava.getInstance(DrishtiApplication.getInstance().getApplicationContext()).processClient(Collections.singletonList(eventClient), true);
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

        while (pos < total) {
            JSONObject jsonObject = obs.getJSONObject(pos);
            String key = jsonObject.getString("fieldCode");

            JSONArray humanReadableValues = jsonObject.getJSONArray("humanReadableValues");
            JSONArray values = jsonObject.getJSONArray("values");

            JSONArray value = humanReadableValues.length() == values.length() ? humanReadableValues : values;

            if (value.length() == 1) {
                result.put(key, value.getString(0));
            } else if (value.length() > 1) {
                result.put(key, value);
            }
            pos++;
        }

        return result;
    }

    public <T> NativeFormProcessor populateValues(Map<String, T> dictionary) throws JSONException {
        int step = 1;
        while (jsonForm.has("step" + step)) {
            JSONObject jsonStepObject = jsonForm.getJSONObject("step" + step);
            JSONArray array = jsonStepObject.getJSONArray(JsonFormUtils.FIELDS);
            int position = 0;
            while (position < array.length()) {
                JSONObject object = array.getJSONObject(position);
                String key = object.getString(JsonFormUtils.KEY);
                String type = object.getString(JsonFormUtils.TYPE);

                if (dictionary.containsKey(key)) {
                    Object val = type.equals(JsonFormUtils.CHECK_BOX) ? getCheckBoxValues(key, dictionary, object) :
                            dictionary.get(key);

                    object.put(JsonFormUtils.VALUE, val);
                }

                position++;
            }

            step++;
        }
        return this;
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
            jsonArray.put(options.get(content));
            x++;
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
