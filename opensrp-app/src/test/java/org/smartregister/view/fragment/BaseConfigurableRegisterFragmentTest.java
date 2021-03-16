package org.smartregister.view.fragment;

import android.app.Application;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.configuration.MockRegisterQueryProvider;
import org.smartregister.configuration.ModuleConfiguration;
import org.smartregister.configuration.ToolbarOptions;
import org.smartregister.shadows.ConfigurationInstancesHelperShadow;
import org.smartregister.view.activity.FormActivity;

@Config(shadows = {ConfigurationInstancesHelperShadow.class})
public class BaseConfigurableRegisterFragmentTest extends BaseUnitTest {

    private BaseConfigurableRegisterFragment registerFragment;
    private ModuleConfiguration moduleConfiguration;
    private Application context;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        context = RuntimeEnvironment.application;
        registerFragment = Mockito.mock(BaseConfigurableRegisterFragment.class, Mockito.CALLS_REAL_METHODS);
        setupModuleConfiguration();
    }

    @After
    public void tearDown() {
        registerFragment = null;
    }

    @Test
    public void settingModuleConfigInitsFragmentConfigs() {
        registerFragment.setModuleConfiguration(moduleConfiguration);
        Assert.assertNotNull(Whitebox.getInternalState(registerFragment, "moduleRegisterQueryProvider"));
        Assert.assertNotNull(Whitebox.getInternalState(registerFragment, "moduleConfiguration"));
        Assert.assertNotNull(Whitebox.getInternalState(registerFragment, "toolbarOptions"));
    }

    @Test
    public void getLayoutReturnsNewLayoutIfNewToolbarEnabled() {
        ToolbarOptions toolbarOptions = Mockito.mock(ToolbarOptions.class);
        Whitebox.setInternalState(registerFragment, "toolbarOptions", toolbarOptions);
        Mockito.doReturn(true).when(toolbarOptions).isNewToolbarEnabled();
        Assert.assertEquals(R.layout.configurable_fragment_base_register, registerFragment.getLayout());
    }

    @Test
    public void getDueOnlyTextReturnsCorrectString() {
        Mockito.doReturn(context).when(registerFragment).getContext();
        Assert.assertEquals("Due only", registerFragment.getDueOnlyText());
    }

    @Test
    public void getToolbarTitleReturnsRegisterTitle() {
        Whitebox.setInternalState(registerFragment, "moduleConfiguration", moduleConfiguration);
        Mockito.doReturn("Test Register").when(moduleConfiguration).getRegisterTitle();
        Assert.assertEquals("Test Register", registerFragment.getToolBarTitle());
        Mockito.verify(moduleConfiguration, Mockito.times(1)).getRegisterTitle();
    }

    private void setupModuleConfiguration() {
        String moduleName = "Test-Module";
        moduleConfiguration = Mockito.mock(ModuleConfiguration.class, Mockito.CALLS_REAL_METHODS);
        Mockito.doReturn(MockRegisterQueryProvider.class).when(moduleConfiguration).getRegisterQueryProvider();
        Mockito.doReturn(FormActivity.class).when(moduleConfiguration).getJsonFormActivity();
        Mockito.doReturn(ToolbarOptions.class).when(moduleConfiguration).getToolbarOptions();
        CoreLibrary.getInstance().addModuleConfiguration(moduleName, moduleConfiguration);
    }

}
