package org.smartregister.util;

import android.content.res.AssetManager;
import android.util.Xml;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

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

        Assert.assertNotNull(formUtils.generateXMLInputForFormWithEntityId("baseEntityId", FORMNAME, null));
    }

    @Test
    public void assertWithEntityIdReturnsFormSubmissionBuilder() {
        FormSubmissionBuilder builder = new FormSubmissionBuilder();
        Assert.assertNotNull(builder.withEntityId("baseEntityId"));
    }

    @Test
    public void assertWithSyncStatusReturnsFormSubmissionBuilder() {
        FormSubmissionBuilder builder = new FormSubmissionBuilder();
        SyncStatus syncStatus = null;
        Assert.assertNotNull(builder.withSyncStatus(syncStatus));
    }

    @Test
    public void assertConstructorInitializationNotNull() throws Exception {
        Assert.assertNotNull(new FormUtils(context_));
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
        Assert.assertEquals(10011, formUtils.getFormJson("test_basic_form").toString().length());
    }

    @Test
    public void getIndexForFormNameShouldReturnCorrectIndex() {
        String[] formNames = new String[]{"Birth Reg", "Immunisation Reg", "Death Form"};
        Assert.assertEquals(1, formUtils.getIndexForFormName("Immunisation Reg", formNames));
    }

    @Test
    public void getJsonFieldFromArrayShouldReturnObjectWithCorrectNameProperty() throws JSONException {
        JSONArray jsonArray = new JSONArray("[{\"name\":\"first_name\",\"type\":\"edit_text\",\"value\":\"John\"},{\"name\":\"last_name\",\"type\":\"edit_text\",\"value\":\"Doe\"}]");

        JSONObject resultJson = ReflectionHelpers.callInstanceMethod(formUtils, "getJsonFieldFromArray"
                , ReflectionHelpers.ClassParameter.from(String.class, "last_name")
                , ReflectionHelpers.ClassParameter.from(JSONArray.class, jsonArray));
        Assert.assertEquals("Doe", resultJson.getString("value"));
    }

}
