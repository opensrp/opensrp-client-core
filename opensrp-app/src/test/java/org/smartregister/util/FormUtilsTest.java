package org.smartregister.util;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;
import android.util.Xml;

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
import org.smartregister.domain.ClientForm;
import org.smartregister.domain.SyncStatus;
import org.smartregister.repository.ClientFormRepository;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.repository.FormDataRepository;
import org.smartregister.service.ANMService;
import org.smartregister.sync.CloudantDataHandler;
import org.smartregister.util.mock.XmlSerializerMock;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by kaderchowdhury on 14/11/17.
 */
@PrepareForTest({CoreLibrary.class, CloudantDataHandler.class, Xml.class})
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
        formUtils = FormUtils.getInstance(context_);
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
        Mockito.when(formDataRepository.getMapFromSQLQuery(Mockito.anyString(),Mockito.any(String[].class))).thenReturn(new HashMap<String, String>());
        DetailsRepository detailsRepository = Mockito.mock(DetailsRepository.class);
        Mockito.when(context.detailsRepository()).thenReturn(detailsRepository);
        Mockito.when(detailsRepository.getAllDetailsForClient(Mockito.anyString())).thenReturn(new HashMap<String, String>());
        PowerMockito.mockStatic(Xml.class);
        XmlSerializerMock xmlSerializer = new XmlSerializerMock();
        PowerMockito.when(Xml.newSerializer()).thenReturn(xmlSerializer);
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

    @Test
    public void assertgenerateFormSubmisionFromXMLString() throws Exception {
        formUtils = new FormUtils(context_);

        String formData = getStringFromStream(new FileInputStream(getFileFromPath(this, formSubmissionXML)));

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
        Mockito.when(formDataRepository.queryUniqueResult(Mockito.anyString(),Mockito.any(String[].class))).thenReturn(null);

        Assert.assertNotNull(formUtils.generateFormSubmisionFromXMLString("baseEntityId", formData, FORMNAME, new JSONObject()));
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

    @Test
    public void getFormJsonFromRepositoryOrAssets() throws Exception {
        formUtils = new FormUtils(context_);

        Resources resources = Mockito.mock(Resources.class);
        Configuration configuration = Mockito.mock(Configuration.class);
        ClientFormRepository clientFormRepository = Mockito.mock(ClientFormRepository.class);
        ClientForm clientForm = new ClientForm();
        clientForm.setJson("\"{\\n  \\\"form\\\": \\\"Sick Child Referral\\\",\\n  \\\"count\\\": \\\"1\\\",\\n  \\\"encounter_type\\\": \\\" \\\",\\n  \\\"entity_id\\\": \\\"\\\",\\n  \\\"relational_id\\\": \\\"\\\",\\n  \\\"rules_file\\\": \\\"rule/general_neat_referral_form_rules.yml\\\",\\n  \\\"metadata\\\": {\\n    \\\"start\\\": {\\n      \\\"openmrs_entity_parent\\\": \\\"\\\",\\n      \\\"openmrs_entity\\\": \\\"concept\\\",\\n      \\\"openmrs_data_type\\\": \\\"start\\\",\\n      \\\"openmrs_entity_id\\\": \\\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\"\\n    },\\n    \\\"end\\\": {\\n      \\\"openmrs_entity_parent\\\": \\\"\\\",\\n      \\\"openmrs_entity\\\": \\\"concept\\\",\\n      \\\"openmrs_data_type\\\": \\\"end\\\",\\n      \\\"openmrs_entity_id\\\": \\\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\"\\n    },\\n    \\\"today\\\": {\\n      \\\"openmrs_entity_parent\\\": \\\"\\\",\\n      \\\"openmrs_entity\\\": \\\"encounter\\\",\\n      \\\"openmrs_entity_id\\\": \\\"encounter_date\\\"\\n    },\\n    \\\"deviceid\\\": {\\n      \\\"openmrs_entity_parent\\\": \\\"\\\",\\n      \\\"openmrs_entity\\\": \\\"concept\\\",\\n      \\\"openmrs_data_type\\\": \\\"deviceid\\\",\\n      \\\"openmrs_entity_id\\\": \\\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\"\\n    },\\n    \\\"subscriberid\\\": {\\n      \\\"openmrs_entity_parent\\\": \\\"\\\",\\n      \\\"openmrs_entity\\\": \\\"concept\\\",\\n      \\\"openmrs_data_type\\\": \\\"subscriberid\\\",\\n      \\\"openmrs_entity_id\\\": \\\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\"\\n    },\\n    \\\"simserial\\\": {\\n      \\\"openmrs_entity_parent\\\": \\\"\\\",\\n      \\\"openmrs_entity\\\": \\\"concept\\\",\\n      \\\"openmrs_data_type\\\": \\\"simserial\\\",\\n      \\\"openmrs_entity_id\\\": \\\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\"\\n    },\\n    \\\"phonenumber\\\": {\\n      \\\"openmrs_entity_parent\\\": \\\"\\\",\\n      \\\"openmrs_entity\\\": \\\"concept\\\",\\n      \\\"openmrs_data_type\\\": \\\"phonenumber\\\",\\n      \\\"openmrs_entity_id\\\": \\\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\"\\n    },\\n    \\\"encounter_location\\\": \\\"\\\",\\n    \\\"look_up\\\": {\\n      \\\"entity_id\\\": \\\"\\\",\\n      \\\"value\\\": \\\"\\\"\\n    }\\n  },\\n  \\\"steps\\\": [\\n    {\\n      \\\"title\\\": \\\"Sick child form\\\",\\n      \\\"fields\\\": [\\n        {\\n          \\\"name\\\": \\\"chw_referral_service\\\",\\n          \\\"type\\\": \\\"invisible\\\",\\n          \\\"properties\\\": {\\n            \\\"text\\\": \\\"Choose referral service\\\"\\n          },\\n          \\\"meta_data\\\": {\\n            \\\"openmrs_entity\\\": \\\"concept\\\",\\n            \\\"openmrs_entity_id\\\": \\\"09978\\\",\\n            \\\"openmrs_entity_parent\\\": \\\"\\\"\\n          },\\n          \\\"options\\\": [],\\n          \\\"required_status\\\": \\\"yes:Please specify referral service\\\"\\n        },\\n        {\\n          \\\"name\\\": \\\"problem\\\",\\n          \\\"type\\\": \\\"multi_choice_checkbox\\\",\\n          \\\"properties\\\": {\\n            \\\"text\\\": \\\"Pick condition/problem associated with the client.\\\"\\n          },\\n          \\\"meta_data\\\": {\\n            \\\"openmrs_entity_parent\\\": \\\"\\\",\\n            \\\"openmrs_entity\\\": \\\"concept\\\",\\n            \\\"openmrs_entity_id\\\": \\\"163182AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\"\\n          },\\n          \\\"options\\\": [\\n            {\\n              \\\"name\\\": \\\"Fast_breathing_and_difficulty_with_breathing\\\",\\n              \\\"text\\\": \\\"Fast breathing and difficulty with breathing\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"142373AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Umbilical_cord_navel_bleeding\\\",\\n              \\\"text\\\": \\\"Umbilical cord/navel bleeding\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"123844AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Excessive_crying\\\",\\n              \\\"text\\\": \\\"Excessive crying\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"140944AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Convulsions\\\",\\n              \\\"text\\\": \\\"Convulsions\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"113054AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Unable_to_breastfeed_or_swallow\\\",\\n              \\\"text\\\": \\\"Unable to breastfeed or swallow\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"159861AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Neck_stiffness\\\",\\n              \\\"text\\\": \\\"Neck stiffness\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"112721AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Fever\\\",\\n              \\\"text\\\": \\\"Fever\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"140238AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Bloating\\\",\\n              \\\"text\\\": \\\"Bloating\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"147132AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Redness_around_the_umbilical_cord_foul_smelling_discharge_from_the_umbilical_cord\\\",\\n              \\\"text\\\": \\\"Redness around the umbilical cord, foul-smelling discharge from the umbilical cord\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"132407AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Bacterial_conjunctivitis\\\",\\n              \\\"text\\\": \\\"Bacterial conjunctivitis\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"148026AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Severe_anaemia\\\",\\n              \\\"text\\\": \\\"Severe anaemia\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"162044AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Severe_abdominal_pain\\\",\\n              \\\"text\\\": \\\"Severe abdominal pain\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"165271AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Pale_or_jaundiced\\\",\\n              \\\"text\\\": \\\"Pale or jaundiced\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"136443AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Cyanosis_blueness_of_lips\\\",\\n              \\\"text\\\": \\\"Cyanosis (blueness of lips)\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"143050AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Skin_rash_pustules\\\",\\n              \\\"text\\\": \\\"Skin rash / pustules\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"512AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Diarrhea\\\",\\n              \\\"text\\\": \\\"Diarrhea\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"142412AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Vomiting\\\",\\n              \\\"text\\\": \\\"Vomiting\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"122983AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Disabilities\\\",\\n              \\\"text\\\": \\\"Disabilities\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"162558AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Premature_baby\\\",\\n              \\\"text\\\": \\\"Premature baby\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"159908AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Care_of_HIV_exposed_infant\\\",\\n              \\\"text\\\": \\\"Care of HIV-exposed infant\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"164818AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Immunisation\\\",\\n              \\\"text\\\": \\\"Immunisation\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"1914AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Other_symptom\\\",\\n              \\\"text\\\": \\\"Other symptom\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            }\\n          ],\\n          \\\"required_status\\\": \\\"yes:Please specify client's problems\\\"\\n        },\\n        {\\n          \\\"name\\\": \\\"problem_other\\\",\\n          \\\"type\\\": \\\"text_input_edit_text\\\",\\n          \\\"meta_data\\\": {\\n            \\\"openmrs_entity\\\": \\\"concept\\\",\\n            \\\"openmrs_entity_id\\\": \\\"160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n            \\\"openmrs_entity_parent\\\": \\\"163182AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\"\\n          },\\n          \\\"properties\\\": {\\n            \\\"hint\\\": \\\"Other symptoms\\\",\\n            \\\"type\\\": \\\"name\\\"\\n          },\\n          \\\"required_status\\\": \\\"true:Please specify other symptoms\\\",\\n          \\\"subjects\\\": \\\"problem:map\\\"\\n        },\\n        {\\n          \\\"name\\\": \\\"service_before_referral\\\",\\n          \\\"meta_data\\\": {\\n            \\\"openmrs_entity_parent\\\": \\\"\\\",\\n            \\\"openmrs_entity\\\": \\\"concept\\\",\\n            \\\"openmrs_entity_id\\\": \\\"164378AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\"\\n          },\\n          \\\"type\\\": \\\"multi_choice_checkbox\\\",\\n          \\\"properties\\\": {\\n            \\\"text\\\": \\\"Pre-referral management given.\\\"\\n          },\\n          \\\"options\\\": [\\n            {\\n              \\\"name\\\": \\\"ORS\\\",\\n              \\\"text\\\": \\\"ORS\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"351AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Panadol\\\",\\n              \\\"text\\\": \\\"Panadol\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"70116AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"Other_treatment\\\",\\n              \\\"text\\\": \\\"Other treatment\\\",\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            },\\n            {\\n              \\\"name\\\": \\\"None\\\",\\n              \\\"text\\\": \\\"None\\\",\\n              \\\"is_exclusive\\\": true,\\n              \\\"meta_data\\\": {\\n                \\\"openmrs_entity\\\": \\\"concept\\\",\\n                \\\"openmrs_entity_id\\\": \\\"164369AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n                \\\"openmrs_entity_parent\\\": \\\"\\\"\\n              }\\n            }\\n          ],\\n          \\\"required_status\\\": \\\"Pre-referral management field is required\\\"\\n        },\\n        {\\n          \\\"name\\\": \\\"service_before_referral_other\\\",\\n          \\\"type\\\": \\\"text_input_edit_text\\\",\\n          \\\"meta_data\\\": {\\n            \\\"openmrs_entity\\\": \\\"concept\\\",\\n            \\\"openmrs_entity_id\\\": \\\"160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\n            \\\"openmrs_entity_parent\\\": \\\"164378AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\"\\n          },\\n          \\\"properties\\\": {\\n            \\\"hint\\\": \\\"Other Treatment\\\",\\n            \\\"type\\\": \\\"name\\\"\\n          },\\n          \\\"required_status\\\": \\\"true:Please specify other treatment given\\\",\\n          \\\"subjects\\\": \\\"service_before_referral:map\\\"\\n        },\\n        {\\n          \\\"name\\\": \\\"chw_referral_hf\\\",\\n          \\\"type\\\": \\\"spinner\\\",\\n          \\\"meta_data\\\": {\\n            \\\"openmrs_entity\\\": \\\"concept\\\",\\n            \\\"openmrs_entity_id\\\": \\\"chw_referral_hf\\\",\\n            \\\"openmrs_entity_parent\\\": \\\"\\\"\\n          },\\n          \\\"properties\\\": {\\n            \\\"text\\\": \\\"Choose referral facility\\\",\\n            \\\"searchable\\\": \\\"Choose referral facility\\\"\\n          },\\n          \\\"options\\\": [],\\n          \\\"required_status\\\": \\\"yes:Please specify referral facility\\\"\\n        },\\n        {\\n          \\\"name\\\": \\\"referral_appointment_date\\\",\\n          \\\"type\\\": \\\"datetime_picker\\\",\\n          \\\"properties\\\": {\\n            \\\"hint\\\": \\\"Please select the appointment date\\\",\\n            \\\"type\\\": \\\"date_picker\\\",\\n            \\\"display_format\\\": \\\"dd/MM/yyyy\\\"\\n          },\\n          \\\"meta_data\\\": {\\n            \\\"openmrs_entity\\\": \\\"concept\\\",\\n            \\\"openmrs_entity_id\\\": \\\"referral_appointment_date\\\",\\n            \\\"openmrs_entity_parent\\\": \\\"\\\"\\n          },\\n          \\\"required_status\\\": \\\"true:Please specify the appointment date\\\"\\n        },\\n        {\\n          \\\"name\\\": \\\"referral_date\\\",\\n          \\\"meta_data\\\": {\\n            \\\"openmrs_entity_parent\\\": \\\"\\\",\\n            \\\"openmrs_entity\\\": \\\"concept\\\",\\n            \\\"openmrs_entity_id\\\": \\\"163181AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\"\\n          },\\n          \\\"type\\\": \\\"hidden\\\"\\n        },\\n        {\\n          \\\"name\\\": \\\"referral_time\\\",\\n          \\\"openmrs_entity_parent\\\": \\\"\\\",\\n          \\\"openmrs_entity\\\": \\\"concept\\\",\\n          \\\"openmrs_entity_id\\\": \\\"referral_time\\\",\\n          \\\"type\\\": \\\"hidden\\\"\\n        },\\n        {\\n          \\\"name\\\": \\\"referral_type\\\",\\n          \\\"openmrs_entity_parent\\\": \\\"\\\",\\n          \\\"openmrs_entity\\\": \\\"concept\\\",\\n          \\\"openmrs_entity_id\\\": \\\"referral_type\\\",\\n          \\\"type\\\": \\\"hidden\\\"\\n        },\\n        {\\n          \\\"name\\\": \\\"referral_status\\\",\\n          \\\"openmrs_entity_parent\\\": \\\"\\\",\\n          \\\"openmrs_entity\\\": \\\"concept\\\",\\n          \\\"openmrs_entity_id\\\": \\\"referral_status\\\",\\n          \\\"type\\\": \\\"hidden\\\"\\n        }\\n      ]\\n    }\\n  ]\\n}\"");


        configuration.locale = new Locale("en");

        Mockito.when( context_.getResources()).thenReturn(resources);
        Mockito.when( resources.getConfiguration()).thenReturn(configuration);
        Mockito.when( CoreLibrary.getInstance().context().getClientFormRepository()).thenReturn(clientFormRepository);
        Mockito.when( clientFormRepository.getActiveClientFormByIdentifier("sick_child_referral_form")).thenReturn(clientForm);
        Assert.assertNotNull(formUtils.getFormJsonFromRepositoryOrAssets("sick_child_referral_form"));
    }
}
