package org.smartregister.view.activity;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
import org.smartregister.configuration.ModuleConfiguration;
import org.smartregister.configuration.ModuleRegisterQueryProviderContract;
import org.smartregister.pojo.InnerJoinObject;
import org.smartregister.pojo.QueryTable;
import org.smartregister.service.ZiggyService;

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
        //
    }

    @Test
    public void testStartFormActivity() {
        //
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

}