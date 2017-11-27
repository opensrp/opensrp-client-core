package org.smartregister.util;

import android.content.res.AssetManager;

import junit.framework.Assert;

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
import org.smartregister.repository.FormDataRepository;
import org.smartregister.service.ANMService;
import org.smartregister.sync.CloudantDataHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by kaderchowdhury on 14/11/17.
 */
@PrepareForTest({CoreLibrary.class, CloudantDataHandler.class})
public class FormUtilsTest extends BaseUnitTest {

    FormUtils formUtils;
    String FORMNAME = "birthnotificationpregnancystatusfollowup";
    String formDefinition = "www/form/"+FORMNAME+"/form_definition.json";
    String model = "www/form/"+FORMNAME+"/model.xml";
    String formJSON = "www/form/"+FORMNAME+"/form.json";
    String formMultiJSON = "www/form/"+FORMNAME+"/form_multi.json";
    String DEFAULT_BIND_PATH = "/model/instance/Child_Vaccination_Enrollment/";
    String formSubmissionXML = "www/form/form_submission/form_submission_xml.xml";
    String formSubmissionJSON = "www/form/form_submission/form_submission_json.json";
    @Rule
    public PowerMockRule rule = new PowerMockRule();
    @Mock
    CloudantDataHandler cloudantDataHandler;
    @Mock
    CoreLibrary coreLibrary;
    @Mock
    Context context;
    @Mock
    android.content.Context context_;
    @Mock
    AssetManager assetManager;
    @Mock
    ANMService anmService;
    @Mock
    ANM anm;

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
        formUtils = FormUtils.getInstance(context_);
    }

    @Test
    public void assertretrieveValueForLinkedRecord(){
//        formUtils.retrieveValueForLinkedRecord();
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
        formUtils.generateXMLInputForFormWithEntityId("baseEntityId",FORMNAME,null);
    }
    @Test
    public void assertWithEntityIdReturnsFormSubmissionBuilder(){
        FormSubmissionBuilder builder= new FormSubmissionBuilder();
        Assert.assertNotNull(builder.withEntityId("baseEntityId"));
    }
    @Test
    public void assertWithSyncStatusReturnsFormSubmissionBuilder(){
        FormSubmissionBuilder builder= new FormSubmissionBuilder();
        SyncStatus syncStatus = null;
        Assert.assertNotNull(builder.withSyncStatus(syncStatus));
    }

    @Test
    public void assertConstructorInitializationNotNull() throws Exception {
        Assert.assertNotNull(new FormUtils(context_));
    }

    @Test
    public void assertgenerateFormSubmisionFromXMLString() throws Exception {
        formUtils = new FormUtils(context_);

        String formData = getStringFromStream(new FileInputStream(getFileFromPath(this,formSubmissionXML)));

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
                return new FileInputStream(getFileFromPath(this,formJSON));
            }
        });
        FormDataRepository formDataRepository = Mockito.mock(FormDataRepository.class);
        Mockito.when(context.formDataRepository()).thenReturn(formDataRepository);
        Mockito.when(formDataRepository.queryUniqueResult(Mockito.anyString())).thenReturn(null);

        formUtils.generateFormSubmisionFromXMLString("baseEntityId", formData, FORMNAME, new JSONObject());
    }

    private static File getFileFromPath(Object obj, String fileName) {
        ClassLoader classLoader = obj.getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        return new File(resource.getPath());
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
}
