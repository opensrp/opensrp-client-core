package org.smartregister.util;

import android.content.res.AssetManager;
import android.util.Xml;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import androidx.test.core.app.ApplicationProvider;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.ANM;
import org.smartregister.domain.SyncStatus;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.repository.FormDataRepository;
import org.smartregister.service.ANMService;
import org.smartregister.util.mock.XmlSerializerMock;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by kaderchowdhury on 14/11/17.
 */
public class FormUtilsTest extends BaseUnitTest {

    private FormUtils formUtils;
    private String FORMNAME = "birthnotificationpregnancystatusfollowup";
    private String formDefinition = "www/form/" + FORMNAME + "/form_definition.json";
    private String model = "www/form/" + FORMNAME + "/model.xml";
    private String formJSON = "www/form/" + FORMNAME + "/form.json";
    private String formMultiJSON = "www/form/" + FORMNAME + "/form_multi.json";
    private String DEFAULT_BIND_PATH = "/model/instance/Child_Vaccination_Enrollment/";
    private String formSubmissionXML = "www/form/form_submission/form_submission_xml.xml";
    private String formSubmissionJSON = "www/form/form_submission/form_submission_json.json";
    private String entityRelationShip = "www/form/entity_relationship.json";
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

    private AutoCloseable autoCloseable;

    private static File getFileFromPath(Object obj, String fileName) {
        ClassLoader classLoader = obj.getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        return new File(resource.getPath());
    }

    @Before
    public void setUp() throws Exception {
        autoCloseable = MockitoAnnotations.openMocks(this);
        try (MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
        }

        Mockito.when(coreLibrary.context()).thenReturn(context);
        Mockito.when(context.anmService()).thenReturn(anmService);
        Mockito.when(anmService.fetchDetails()).thenReturn(anm);
        Mockito.when(anm.name()).thenReturn("anmId");

        formUtils = FormUtils.getInstance(context_);
    }

    @After
    public void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void assertretrieveValueForLinkedRecord() throws Exception {
        formUtils = new FormUtils(context_);
        Mockito.when(context_.getAssets()).thenReturn(assetManager);

        Mockito.when(assetManager.open(entityRelationShip)).thenAnswer(new Answer<InputStream>() {
            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return new FileInputStream(getFileFromPath(this, entityRelationShip));
            }
        });
        JSONObject mockobject = Mockito.mock(JSONObject.class);
        Mockito.when(mockobject.getString(Mockito.anyString())).thenReturn("val");
        formUtils.retrieveValueForLinkedRecord("household.elco", mockobject);
    }

    @Test
    public void assertgenerateXMLInputForFormWithEntityId() throws Exception {
        formUtils = new FormUtils(context_);
        Mockito.when(context_.getAssets()).thenReturn(assetManager);

        Mockito.when(assetManager.open(formDefinition)).thenAnswer(new Answer<InputStream>() {
            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return new FileInputStream(getFileFromPath(this, formDefinition));
            }
        });

        Mockito.when(assetManager.open(model)).thenAnswer(new Answer<InputStream>() {
            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return new FileInputStream(getFileFromPath(this, model));
            }
        });
        Mockito.when(assetManager.open(formJSON)).thenAnswer(new Answer<InputStream>() {
            @Override
            public InputStream answer(InvocationOnMock invocation) throws Throwable {
                return new FileInputStream(getFileFromPath(this, formJSON));
            }
        });

        FormDataRepository formDataRepository = Mockito.mock(FormDataRepository.class);
        Mockito.when(context.formDataRepository()).thenReturn(formDataRepository);
        Mockito.when(formDataRepository.getMapFromSQLQuery(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(new HashMap<>());
        DetailsRepository detailsRepository = Mockito.mock(DetailsRepository.class);
        Mockito.when(context.detailsRepository()).thenReturn(detailsRepository);
        Mockito.when(detailsRepository.getAllDetailsForClient(Mockito.anyString())).thenReturn(new HashMap<>());

        XmlSerializerMock xmlSerializer = new XmlSerializerMock();
        try (MockedStatic<Xml> xmlMockedStatic = Mockito.mockStatic(Xml.class)) {
            xmlMockedStatic.when(Xml::newSerializer).thenReturn(xmlSerializer);
        }

        assertNotNull(formUtils.generateXMLInputForFormWithEntityId("baseEntityId", FORMNAME, null));
    }

    @Test
    public void assertWithEntityIdReturnsFormSubmissionBuilder() {
        FormSubmissionBuilder builder = new FormSubmissionBuilder();
        assertNotNull(builder.withEntityId("baseEntityId"));
    }

    @Test
    public void assertWithSyncStatusReturnsFormSubmissionBuilder() {
        FormSubmissionBuilder builder = new FormSubmissionBuilder();
        SyncStatus syncStatus = null;
        assertNotNull(builder.withSyncStatus(syncStatus));
    }

    @Test
    public void assertConstructorInitializationNotNull() throws Exception {
        assertNotNull(new FormUtils(context_));
    }

    public String getStringFromStream(InputStream is) throws Exception {
        String fileContents = "";
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        fileContents = new String(buffer, "UTF-8");
        return fileContents;
    }

    @Test
    public void getFormJsonShouldReturnCorrectFormWithSameLength() throws IOException {
        Mockito.doReturn(ApplicationProvider.getApplicationContext().getResources()).when(context_).getResources();
        Mockito.doReturn(ApplicationProvider.getApplicationContext().getApplicationContext()).when(context_).getApplicationContext();
        assertEquals(10011, formUtils.getFormJson("test_basic_form").toString().length());
    }

    @Test
    public void getIndexForFormNameShouldReturnCorrectIndex() {
        String[] formNames = new String[]{"Birth Reg", "Immunisation Reg", "Death Form"};
        assertEquals(1, formUtils.getIndexForFormName("Immunisation Reg", formNames));
    }

    @Test
    public void getJsonFieldFromArrayShouldReturnObjectWithCorrectNameProperty() throws JSONException {
        JSONArray jsonArray = new JSONArray("[{\"name\":\"first_name\",\"type\":\"edit_text\",\"value\":\"John\"},{\"name\":\"last_name\",\"type\":\"edit_text\",\"value\":\"Doe\"}]");

        JSONObject resultJson = ReflectionHelpers.callInstanceMethod(formUtils, "getJsonFieldFromArray"
                , ReflectionHelpers.ClassParameter.from(String.class, "last_name")
                , ReflectionHelpers.ClassParameter.from(JSONArray.class, jsonArray));
        assertEquals("Doe", resultJson.getString("value"));
    }

    @Test
    public void testGetObjectAtPath() throws Exception {

        String[] path1 = {"key1", "key2"};
        JSONObject jsonObject1 = new JSONObject("{\"key1\": {\"key2\": \"value\"}}");
        Object result1 = formUtils.getObjectAtPath(path1, jsonObject1);
        assertEquals("value", result1);

        String[] path2 = {"key1", "key3"};
        JSONObject jsonObject2 = new JSONObject("{\"key1\": {\"key2\": \"value\"}}");
        Object result2 = formUtils.getObjectAtPath(path2, jsonObject2);
        assertNull(result2);


        String[] path3 = {"key1", "key4","key5"};
        JSONObject jsonObject3 = new JSONObject("{\"key1\": {\"key4\": [{\"key5\": \"value\"}]}}");
        Object result3 = formUtils.getObjectAtPath(path3, jsonObject3);
        assertEquals("value", result3);


        String[] path4 = {"key1", "key4"};
        JSONObject jsonObject4 = new JSONObject("{\"key1\": {\"key4\": []}}");
        Object result4 = formUtils.getObjectAtPath(path4, jsonObject4);
        assertEquals("[]", result4.toString());
    }

    @Test
    public void testGetFieldsArrayForSubFormDefinition() throws Exception {

        JSONArray fieldsArray2 = new JSONArray("[{\"name\": \"field1\"}, {\"name\": \"field2\", \"source\": \"source2\"}]");
        String bindPath2 = "example";
        JSONObject fieldsDefinition2 = new JSONObject();
        fieldsDefinition2.put("fields", fieldsArray2);
        fieldsDefinition2.put("bind_type", bindPath2);
        JSONArray result2 = formUtils.getFieldsArrayForSubFormDefinition(fieldsDefinition2);
        assertNotNull(result2);
        assertEquals(2, result2.length());

        JSONObject item1 = result2.getJSONObject(0);
        assertEquals("field1", item1.getString("name"));
        assertEquals(bindPath2 + ".field1", item1.getString("source"));

        JSONObject item2 = result2.getJSONObject(1);
        assertEquals("field2", item2.getString("name"));
        assertEquals(bindPath2 + ".source2", item2.getString("source"));
    }

    @Test
    public void testHasChildElements() {
        Node element = Mockito.mock(Node.class);
        NodeList children = Mockito.mock(NodeList.class);
        Mockito.when(element.getChildNodes()).thenReturn(children);
        Mockito.when(children.getLength()).thenReturn(0);
        boolean result1 = formUtils.hasChildElements(element);
        assertFalse(result1);



        Mockito.when(children.getLength()).thenReturn(1);
        Mockito.when(children.item(ArgumentMatchers.anyInt())).thenReturn(element);
        Mockito.when(element.getNodeType()).thenReturn(Node.TEXT_NODE);
        boolean result3 = formUtils.hasChildElements(element);
        assertFalse(result3);
    }

    @Test
    public void testGetValueForPath() throws Exception {


        String[] path2 = {"key1", "key2"};
        JSONObject jsonObject2 = new JSONObject();
        String result2 = formUtils.getValueForPath(path2, jsonObject2);
        assertNull(result2);

        JSONObject innerObject3 = new JSONObject("{\"content\": \"value\"}");
        JSONArray innerArray3 = new JSONArray("[\"value\"]");
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("key1", innerObject3);
        jsonObject3.put("key2", innerArray3);
        jsonObject3.put("key3", "value3");

        String[] path3 = {"key1", "content"};
        String result3 = formUtils.getValueForPath(path3, jsonObject3);
        assertEquals("value", result3);

        String[] path4 = {"key2"};
        String result4 = formUtils.getValueForPath(path4, jsonObject3);
        assertEquals("value", result4);

        String[] path5 = {"key3"};
        String result5 = formUtils.getValueForPath(path5, jsonObject3);
        assertEquals("value3", result5);

    }

    @Test
    public void testGetSubForms() throws Exception {
        JSONArray subFormDataArray = new JSONArray();
        String entity_id = "123";
        JSONObject subFormDefinition = new JSONObject();
        JSONObject overrides = new JSONObject();
        subFormDefinition.put("fields",new JSONArray());
        subFormDefinition.put("bind_type","/bind/type");

        JSONArray result = ReflectionHelpers.callInstanceMethod(formUtils, "getSubForms"
                , ReflectionHelpers.ClassParameter.from(JSONArray.class, subFormDataArray)
                , ReflectionHelpers.ClassParameter.from(String.class, entity_id)
                , ReflectionHelpers.ClassParameter.from(JSONObject.class, subFormDefinition)
                , ReflectionHelpers.ClassParameter.from(JSONObject.class, overrides));

        assertNotNull(result);
        assertEquals(1, result.length());
    }

    @Test
    public void testRetrieveSubformDefinitionForBindPath() throws Exception {
        // Create sample input data
        JSONArray subForms = new JSONArray();
        JSONObject subForm1 = new JSONObject();
        subForm1.put("default_bind_path", "path/to/SubForm1");
        JSONObject subForm2 = new JSONObject();
        subForm2.put("default_bind_path", "path/to/SubForm2");
        subForms.put(subForm1);
        subForms.put(subForm2);
        String fieldName = "SubForm1";


        JSONObject result = ReflectionHelpers.callInstanceMethod(formUtils, "retriveSubformDefinitionForBindPath"
                , ReflectionHelpers.ClassParameter.from(JSONArray.class, subForms)
                , ReflectionHelpers.ClassParameter.from(String.class, fieldName));

        assertNotNull(result);
        assertEquals("path/to/SubForm1", result.getString("default_bind_path"));
    }

    @Test
    public void testGetSubFormNames() throws Exception {
        // Create sample input data
        JSONObject formDefinition = new JSONObject();
        JSONObject form = new JSONObject();
        JSONArray subForms = new JSONArray();
        JSONObject subForm1 = new JSONObject();
        subForm1.put("default_bind_path", "path/to/SubForm1");
        JSONObject subForm2 = new JSONObject();
        subForm2.put("default_bind_path", "path/to/SubForm2");
        subForms.put(subForm1);
        subForms.put(subForm2);
        form.put("sub_forms", subForms);
        formDefinition.put("form", form);

        List<String> result = ReflectionHelpers.callInstanceMethod(formUtils, "getSubFormNames"
                , ReflectionHelpers.ClassParameter.from(JSONObject.class, formDefinition));
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("SubForm1"));
        assertTrue(result.contains("SubForm2"));
    }
}
