package org.smartregister.util;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;
import android.util.Xml;

import junit.framework.Assert;

import org.apache.commons.lang3.StringEscapeUtils;
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
        clientForm.setJson("{\"form\":\"Sick Child Referral\",\"count\":\"1\",\"encounter_type\":\" \",\"entity_id\":\"\",\"relational_id\":\"\",\"rules_file\":\"rule/general_neat_referral_form_rules.yml\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"encounter_location\":\"\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"steps\":[{\"title\":\"Sick child form\",\"fields\":[{\"name\":\"chw_referral_service\",\"type\":\"invisible\",\"properties\":{\"text\":\"Choose referral service\"},\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"09978\",\"openmrs_entity_parent\":\"\"},\"options\":[],\"required_status\":\"yes:Please specify referral service\"},{\"name\":\"problem\",\"type\":\"multi_choice_checkbox\",\"properties\":{\"text\":\"Pick condition/problem associated with the client.\"},\"meta_data\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163182AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"options\":[{\"name\":\"Fast_breathing_and_difficulty_with_breathing\",\"text\":\"Fast breathing and difficulty with breathing\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"142373AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Umbilical_cord_navel_bleeding\",\"text\":\"Umbilical cord/navel bleeding\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"123844AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Excessive_crying\",\"text\":\"Excessive crying\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"140944AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Convulsions\",\"text\":\"Convulsions\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"113054AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Unable_to_breastfeed_or_swallow\",\"text\":\"Unable to breastfeed or swallow\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"159861AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Neck_stiffness\",\"text\":\"Neck stiffness\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"112721AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Fever\",\"text\":\"Fever\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"140238AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Bloating\",\"text\":\"Bloating\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"147132AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Redness_around_the_umbilical_cord_foul_smelling_discharge_from_the_umbilical_cord\",\"text\":\"Redness around the umbilical cord, foul-smelling discharge from the umbilical cord\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"132407AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Bacterial_conjunctivitis\",\"text\":\"Bacterial conjunctivitis\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"148026AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Severe_anaemia\",\"text\":\"Severe anaemia\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"162044AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Severe_abdominal_pain\",\"text\":\"Severe abdominal pain\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165271AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Pale_or_jaundiced\",\"text\":\"Pale or jaundiced\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"136443AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Cyanosis_blueness_of_lips\",\"text\":\"Cyanosis (blueness of lips)\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"143050AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Skin_rash_pustules\",\"text\":\"Skin rash / pustules\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"512AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Diarrhea\",\"text\":\"Diarrhea\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"142412AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Vomiting\",\"text\":\"Vomiting\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"122983AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Disabilities\",\"text\":\"Disabilities\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"162558AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Premature_baby\",\"text\":\"Premature baby\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"159908AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Care_of_HIV_exposed_infant\",\"text\":\"Care of HIV-exposed infant\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"164818AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Immunisation\",\"text\":\"Immunisation\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1914AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Other_symptom\",\"text\":\"Other symptom\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}}],\"required_status\":\"yes:Please specify client's problems\"},{\"name\":\"problem_other\",\"type\":\"text_input_edit_text\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"163182AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"properties\":{\"hint\":\"Other symptoms\",\"type\":\"name\"},\"required_status\":\"true:Please specify other symptoms\",\"subjects\":\"problem:map\"},{\"name\":\"service_before_referral\",\"meta_data\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"164378AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"type\":\"multi_choice_checkbox\",\"properties\":{\"text\":\"Pre-referral management given.\"},\"options\":[{\"name\":\"ORS\",\"text\":\"ORS\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"351AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Panadol\",\"text\":\"Panadol\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"70116AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"Other_treatment\",\"text\":\"Other treatment\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}},{\"name\":\"None\",\"text\":\"None\",\"is_exclusive\":true,\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"164369AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}}],\"required_status\":\"Pre-referral management field is required\"},{\"name\":\"service_before_referral_other\",\"type\":\"text_input_edit_text\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"164378AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"properties\":{\"hint\":\"Other Treatment\",\"type\":\"name\"},\"required_status\":\"true:Please specify other treatment given\",\"subjects\":\"service_before_referral:map\"},{\"name\":\"chw_referral_hf\",\"type\":\"spinner\",\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"chw_referral_hf\",\"openmrs_entity_parent\":\"\"},\"properties\":{\"text\":\"Choose referral facility\",\"searchable\":\"Choose referral facility\"},\"options\":[],\"required_status\":\"yes:Please specify referral facility\"},{\"name\":\"referral_appointment_date\",\"type\":\"datetime_picker\",\"properties\":{\"hint\":\"Please select the appointment date\",\"type\":\"date_picker\",\"display_format\":\"dd/MM/yyyy\"},\"meta_data\":{\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"referral_appointment_date\",\"openmrs_entity_parent\":\"\"},\"required_status\":\"true:Please specify the appointment date\"},{\"name\":\"referral_date\",\"meta_data\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163181AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"type\":\"hidden\"},{\"name\":\"referral_time\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"referral_time\",\"type\":\"hidden\"},{\"name\":\"referral_type\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"referral_type\",\"type\":\"hidden\"},{\"name\":\"referral_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"referral_status\",\"type\":\"hidden\"}]}]}");

        configuration.locale = new Locale("en");

        Mockito.when( context_.getResources()).thenReturn(resources);
        Mockito.when( resources.getConfiguration()).thenReturn(configuration);
        Mockito.when( CoreLibrary.getInstance().context().getClientFormRepository()).thenReturn(clientFormRepository);
        Mockito.when( clientFormRepository.getActiveClientFormByIdentifier("sick_child_referral_form")).thenReturn(clientForm);

        JSONObject form = formUtils.getFormJsonFromRepositoryOrAssets("sick_child_referral_form");

        Mockito.verify(clientFormRepository).getActiveClientFormByIdentifier("sick_child_referral_form");
        Assert.assertNotNull(form);
    }
}
