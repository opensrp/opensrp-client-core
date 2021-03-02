package org.smartregister.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.client.utils.contract.ClientFormContract;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configuration.ModuleConfiguration;
import org.smartregister.configuration.ModuleMetadata;
import org.smartregister.configuration.ModuleRegister;
import org.smartregister.configuration.ModuleRegisterQueryProviderContract;
import org.smartregister.pojo.InnerJoinObject;
import org.smartregister.pojo.QueryTable;
import org.smartregister.service.ZiggyService;
import org.smartregister.view.contract.BaseRegisterContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 16-02-2021.
 */
public class BaseConfigurableRegisterActivityTest extends BaseRobolectricUnitTest {

    private BaseConfigurableRegisterActivity baseConfigurableRegisterActivity;

    private ActivityController<BaseConfigurableRegisterActivity> controller;

    @Mock
    private ZiggyService ziggyService;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private BaseRegisterContract.Presenter presenter;

    @BeforeClass
    public static void resetCoreLibrary() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
    }

    @Before
    public void setUp() {
        org.mockito.MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(CoreLibrary.getInstance().context(), "ziggyService", ziggyService);

        String moduleName = "PNC";
        Intent intent = new Intent();
        intent.putExtra(AllConstants.IntentExtra.MODULE_NAME, moduleName);

        controller = Robolectric.buildActivity(BaseConfigurableRegisterActivity.class, intent);
        BaseConfigurableRegisterActivity spyActivity = Mockito.spy((BaseConfigurableRegisterActivity) ReflectionHelpers.getField(controller, "component"));
        ReflectionHelpers.setField(controller, "component", spyActivity);

        AppCompatDelegate delegate = AppCompatDelegate.create(RuntimeEnvironment.application, spyActivity, spyActivity);
        Mockito.doReturn(delegate).when(spyActivity).getDelegate();

        ActionBar actionBar = Mockito.mock(ActionBar.class);
        Mockito.doReturn(actionBar).when(spyActivity).getSupportActionBar();

        Mockito.doReturn(RuntimeEnvironment.application.getPackageManager()).when(spyActivity).getPackageManager();
    }

    private void createStartAndResumeActivity() {
        controller.create()
                .start()
                .resume();
        baseConfigurableRegisterActivity = Mockito.spy(controller.get());

        presenter = Mockito.spy(baseConfigurableRegisterActivity.presenter);
        baseConfigurableRegisterActivity.presenter = presenter;
    }

    @Test
    public void onCreateShouldExtractModuleNameAndConfiguration() {
        String moduleName = "PNC";
        ModuleConfiguration moduleConfiguration = Mockito.mock(ModuleConfiguration.class);
        Mockito.doReturn(MyRegisterQueryConfiguration.class).when(moduleConfiguration).getRegisterQueryProvider();
        CoreLibrary.getInstance().addModuleConfiguration(moduleName, moduleConfiguration);

        // Call the method under test
        createStartAndResumeActivity();

        // Make assertions
        Assert.assertEquals(moduleName, baseConfigurableRegisterActivity.moduleName);
        Assert.assertEquals(moduleConfiguration, baseConfigurableRegisterActivity.moduleConfiguration);
    }

    @Test
    public void onCreateShouldThrowExceptionWhenModuleNameNotPassedInExtras() {
        expectedEx.expect(IllegalStateException.class);
        expectedEx.expectMessage("Module name was not passed to the activity! Kindly use ModuleLibrary.getInstance().startRegisterActivity() to start the activity");

        controller = Robolectric.buildActivity(BaseConfigurableRegisterActivity.class);

        createStartAndResumeActivity();
    }

    @Test
    public void onCreateShouldThrowExceptionWhenModuleNameProvidedAndModuleConfigurationNotProvided() {
        expectedEx.expect(IllegalStateException.class);
        expectedEx.expectMessage("The module configuration for PNC could not be found! Kindly make sure that this is configured correctly through CoreLibrary.getInstance().addModuleConfiguration()");

        createStartAndResumeActivity();
    }

    @Test
    public void startFormActivity() {
        setupModuleConfiguration();
        createStartAndResumeActivity();

        JSONObject jsonObject = new JSONObject();

        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);
        Mockito.doNothing().when(baseConfigurableRegisterActivity).startActivityForResult(intentArgumentCaptor.capture()
                , Mockito.eq(AllConstants.RequestCode.START_JSON_FORM));

        // Call the method under test
        baseConfigurableRegisterActivity.startFormActivity(jsonObject);

        // Perform assertions
        Intent intent = intentArgumentCaptor.getValue();
        Form form = (Form) intent.getSerializableExtra(AllConstants.IntentExtra.JsonForm.FORM);

        Assert.assertEquals("{}", intent.getStringExtra(AllConstants.IntentExtra.JsonForm.JSON));
        Assert.assertEquals(R.color.form_actionbar, form.getActionBarBackground());
        Assert.assertFalse(form.isWizard());
        Assert.assertTrue(form.isHideSaveLabel());
        Assert.assertEquals("", form.getNextLabel());
    }

    @Test
    public void onStartActivityWithActionShouldCallStartFormActivity() {
        setupModuleConfiguration();
        createStartAndResumeActivity();

        // Add the intent extra for the action-registration
        String moduleName = "PNC";
        Intent intent = new Intent();
        intent.putExtra(AllConstants.IntentExtra.MODULE_NAME, moduleName);
        intent.putExtra(AllConstants.IntentExtra.JsonForm.ACTION, AllConstants.IntentExtra.JsonForm.ACTION_REGISTRATION);

        String formName = "registration";
        String baseEntityId = "base-entity-id";
        String table = "ec_client";

        intent.putExtra(AllConstants.IntentExtra.JsonForm.BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(AllConstants.IntentExtra.JsonForm.ENTITY_TABLE, table);

        Mockito.doReturn(intent).when(baseConfigurableRegisterActivity).getIntent();

        Mockito.doNothing().when(baseConfigurableRegisterActivity).startFormActivity(formName, baseEntityId, null, null, table);

        // Add the module metadata
        ModuleConfiguration moduleConfiguration = Mockito.mock(ModuleConfiguration.class);
        ModuleMetadata moduleMetadata = Mockito.mock(ModuleMetadata.class);
        ModuleRegister moduleRegister = Mockito.mock(ModuleRegister.class);

        Mockito.doReturn(formName).when(moduleRegister).getRegistrationFormName();
        Mockito.doReturn(moduleRegister).when(moduleMetadata).getModuleRegister();
        Mockito.doReturn(moduleMetadata).when(moduleConfiguration).getModuleMetadata();
        Mockito.doReturn(MyRegisterQueryConfiguration.class).when(moduleConfiguration).getRegisterQueryProvider();

        Mockito.doReturn(moduleConfiguration).when(baseConfigurableRegisterActivity).getModuleConfiguration();
        Mockito.doReturn(null).when(presenter).getInjectedFieldValues(Mockito.nullable(CommonPersonObjectClient.class));

        // Call the method under test
        baseConfigurableRegisterActivity.onStartActivityWithAction();

        // Perform verifications
        Mockito.verify(baseConfigurableRegisterActivity).startFormActivity(formName, baseEntityId, null, null, table);
    }

    @Test
    public void startFormActivityWithParcelableDataShouldCallStartActivityForResult() {
        setupModuleConfiguration();
        createStartAndResumeActivity();

        // Add Module metadata to module configuration
        ModuleConfiguration moduleConfiguration = baseConfigurableRegisterActivity.getModuleConfiguration();
        Mockito.doReturn(MyJsonFormActivity.class).when(moduleConfiguration).getJsonFormActivity();


        JSONObject jsonObject = new JSONObject();

        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);
        Mockito.doNothing().when(baseConfigurableRegisterActivity).startActivityForResult(intentArgumentCaptor.capture()
                , Mockito.eq(AllConstants.RequestCode.START_JSON_FORM));

        HashMap<String, String> parcelableData =  new HashMap<>();
        parcelableData.put("gender", "male");
        parcelableData.put("health_status", "ok");

        // Call the method under test
        baseConfigurableRegisterActivity.startFormActivity(jsonObject, parcelableData);


        // Perform assertions
        Intent intent = intentArgumentCaptor.getValue();
        Form form = (Form) intent.getSerializableExtra(AllConstants.IntentExtra.JsonForm.FORM);

        Assert.assertEquals("{}", intent.getStringExtra(AllConstants.IntentExtra.JsonForm.JSON));
        Assert.assertEquals(R.color.form_actionbar, form.getActionBarBackground());
        Assert.assertFalse(form.isWizard());
        Assert.assertTrue(form.isHideSaveLabel());
        Assert.assertEquals("", form.getPreviousLabel());
        Assert.assertEquals("", form.getNextLabel());
        Assert.assertEquals("", form.getName());
        Assert.assertFalse(form.isHideNextButton());
        Assert.assertFalse(form.isHidePreviousButton());
        Assert.assertEquals("PNC", intent.getStringExtra(AllConstants.IntentExtra.MODULE_NAME));
        Assert.assertEquals("ok", intent.getStringExtra("health_status"));
        Assert.assertEquals("male", intent.getStringExtra("gender"));

        Assert.assertEquals(MyJsonFormActivity.class.getName(), intent.getComponent().getClassName());
    }

    @Test
    public void startFormActivityShouldCallPresenterStartFormWithEntityTable() {
        setupModuleConfiguration();
        createStartAndResumeActivity();

        String formName = "registration";
        String entityId = "entityId";
        String metadata = "the-meta-data";
        String entityTable = "ec_client";

        HashMap<String, String> injectedFieldValues = new HashMap<>();
        injectedFieldValues.put("gender", "male");
        injectedFieldValues.put("health_status", "ok");

        Mockito.doNothing().when(presenter).startForm(Mockito.eq(formName), Mockito.eq(entityId), Mockito.eq(metadata), Mockito.nullable(String.class), Mockito.eq(injectedFieldValues), Mockito.eq(entityTable));

        baseConfigurableRegisterActivity.startFormActivity(formName, entityId, metadata, injectedFieldValues, entityTable);
        Mockito.doNothing().when(presenter).startForm(Mockito.eq(formName), Mockito.eq(entityId), Mockito.eq(metadata), Mockito.nullable(String.class), Mockito.eq(injectedFieldValues), Mockito.eq(entityTable));
    }

    @Test
    public void startFormActivityShouldCallPresenterStartFormWithoutEntityTable() {
        setupModuleConfiguration();
        createStartAndResumeActivity();

        String formName = "registration";
        String entityId = "entityId";
        String metadata = "the-meta-data";

        Mockito.doNothing().when(presenter).startForm(Mockito.eq(formName), Mockito.eq(entityId), Mockito.eq(metadata), Mockito.nullable(String.class), Mockito.nullable(HashMap.class), Mockito.nullable(String.class));

        baseConfigurableRegisterActivity.startFormActivity(formName, entityId, metadata);

        Mockito.verify(presenter).startForm(Mockito.eq(formName), Mockito.eq(entityId), Mockito.eq(metadata), Mockito.nullable(String.class), Mockito.nullable(HashMap.class), Mockito.nullable(String.class));
    }

    private void setupModuleConfiguration() {
        String moduleName = "PNC";
        ModuleConfiguration moduleConfiguration = Mockito.mock(ModuleConfiguration.class);
        Mockito.doReturn(MyRegisterQueryConfiguration.class).when(moduleConfiguration).getRegisterQueryProvider();
        Mockito.doReturn(MyJsonFormActivity.class).when(moduleConfiguration).getJsonFormActivity();
        CoreLibrary.getInstance().addModuleConfiguration(moduleName, moduleConfiguration);
    }

    public static class MyRegisterQueryConfiguration extends ModuleRegisterQueryProviderContract {

        @NonNull
        @Override
        public String getObjectIdsQuery(@Nullable String filters, @Nullable String mainCondition) {
            return null;
        }

        @NonNull
        @Override
        public String[] countExecuteQueries(@Nullable String filters, @Nullable String mainCondition) {
            return new String[0];
        }

        @NonNull
        @Override
        public String mainSelectWhereIDsIn() {
            return null;
        }

        @Override
        public String mainSelectWhereIdsIn(@NonNull InnerJoinObject[] tableColsInnerJoins, @NonNull QueryTable[] tableCols) {
            return null;
        }

        @Override
        public String mainSelect(@NonNull InnerJoinObject[] tableColsInnerJoins, @NonNull QueryTable[] tableCols) {
            return null;
        }
    }

    public static class MyJsonFormActivity extends Activity implements ClientFormContract.View {

        @Nullable
        @Override
        public JSONObject getSubForm(String s, String s1, Context context, boolean b) throws Exception {
            return null;
        }

        @Nullable
        @Override
        public BufferedReader getRules(@NonNull Context context, @NonNull String s) throws IOException {
            return null;
        }

        @Override
        public void handleFormError(boolean b, @NonNull String s) {
            // TODO -> Do nothing
        }

        @Override
        public void setVisibleFormErrorAndRollbackDialog(boolean b) {
            // TODO -> Do nothing
        }

        @Override
        public boolean isVisibleFormErrorAndRollbackDialog() {
            return false;
        }
    }

}