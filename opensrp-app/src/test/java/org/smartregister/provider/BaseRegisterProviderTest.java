package org.smartregister.provider;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.configuration.ActivityStarter;
import org.smartregister.configuration.MockConfigViewsLib;
import org.smartregister.configuration.MockLocationTagsConfiguration;
import org.smartregister.configuration.MockRegisterQueryProvider;
import org.smartregister.configuration.ModuleConfiguration;
import org.smartregister.configuration.ModuleMetadata;
import org.smartregister.configuration.ModuleRegister;
import org.smartregister.view.activity.BaseProfileActivity;
import org.smartregister.view.activity.FormActivity;

public class BaseRegisterProviderTest extends BaseUnitTest {

    private BaseRegisterProvider registerProvider;
    private Context context;
    private RegisterActionHandlerMock registerActionHandler = new RegisterActionHandlerMock();
    private PaginationViewHandlerMock paginationViewHandler = new PaginationViewHandlerMock();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        context = RuntimeEnvironment.application;
        initializeModuleConfiguration();
        registerProvider = new BaseRegisterProvider(context, registerActionHandler, paginationViewHandler);
    }

    @Test
    public void constructorInitializesRegisterProviderMetadata() {
        Assert.assertNotNull(Whitebox.getInternalState(registerProvider, "registerProviderMetadata"));
    }

    @Ignore("Fix this -> Init baseRegisterRowOptions")
    @Test
    public void constructorInitializesRegisterRowOptions() {
        Assert.assertNotNull(Whitebox.getInternalState(registerProvider, "baseRegisterRowOptions"));
    }

    private void initializeModuleConfiguration() {
        CoreLibrary.init(org.smartregister.Context.getInstance());
        ModuleConfiguration customLibraryConfiguration = new ModuleConfiguration.Builder("Test Opd Register",
                MockRegisterQueryProvider.class, new MockConfigViewsLib(), ActivityStarter.class)
                .setModuleMetadata(new ModuleMetadata(
                        new ModuleRegister(
                                "opd_registration",
                                "ec_client",
                                "Opd Registration",
                                "UPDATE OPD REGISTRATION", "custom-opd"),
                        new MockLocationTagsConfiguration(), FormActivity.class, BaseProfileActivity.class, false, ""
                ))
                .build();

        CoreLibrary.getInstance()
                .addModuleConfiguration(true, "custom-opd", customLibraryConfiguration);
    }

}
