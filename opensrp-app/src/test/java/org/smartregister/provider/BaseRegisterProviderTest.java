package org.smartregister.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configuration.ActivityStarter;
import org.smartregister.configuration.BaseRegisterRowOptions;
import org.smartregister.configuration.MockConfigViewsLib;
import org.smartregister.configuration.MockLocationTagsConfiguration;
import org.smartregister.configuration.MockRegisterQueryProvider;
import org.smartregister.configuration.ModuleConfiguration;
import org.smartregister.configuration.ModuleMetadata;
import org.smartregister.configuration.ModuleRegister;
import org.smartregister.configuration.RegisterProviderMetadata;
import org.smartregister.holders.BaseRegisterViewHolder;
import org.smartregister.view.activity.BaseProfileActivity;
import org.smartregister.view.activity.FormActivity;

import java.util.Map;

public class BaseRegisterProviderTest extends BaseUnitTest {

    private BaseRegisterProvider registerProvider;
    private RegisterActionHandlerMock registerActionHandler = new RegisterActionHandlerMock();
    private PaginationViewHandlerMock paginationViewHandler = new PaginationViewHandlerMock();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Context context = RuntimeEnvironment.application;
        initializeModuleConfiguration();
        registerProvider = new BaseRegisterProvider(context, registerActionHandler, paginationViewHandler);
    }

    @Test
    public void constructorInitializesRegisterProviderMetadata() {
        Assert.assertNotNull(Whitebox.getInternalState(registerProvider, "registerProviderMetadata"));
    }

    @Test
    public void constructorInitializesRegisterRowOptions() {
        Assert.assertNotNull(Whitebox.getInternalState(registerProvider, "baseRegisterRowOptions"));
    }

    @Test
    public void getViewPopulatesPatientColumnAndClientRow() {
        Cursor cursor = Mockito.mock(Cursor.class);
        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);
        BaseRegisterRowOptions baseRegisterRowOptions = Mockito.mock(BaseRegisterRowOptions.class);
        BaseRegisterViewHolder viewHolder = Mockito.mock(BaseRegisterViewHolder.class);
        Whitebox.setInternalState(viewHolder, "childColumn", Mockito.mock(View.class));
        Whitebox.setInternalState(viewHolder, "dueButton", Mockito.mock(Button.class));

        RegisterProviderMetadata registerProviderMetadata = Mockito.mock(RegisterProviderMetadata.class);
        registerProvider = Mockito.mock(BaseRegisterProvider.class, Mockito.CALLS_REAL_METHODS);
        Whitebox.setInternalState(registerProvider, "baseRegisterRowOptions", baseRegisterRowOptions);
        Whitebox.setInternalState(registerProvider, "registerProviderMetadata", registerProviderMetadata);
        Whitebox.setInternalState(registerProvider, "context", RuntimeEnvironment.application);

        Mockito.when(registerProviderMetadata.getClientFirstName(ArgumentMatchers.any(Map.class))).thenReturn("First Name");

        registerProvider.getView(cursor, client, viewHolder);
        Mockito.verify(registerProvider, Mockito.times(1)).
                populatePatientColumn(ArgumentMatchers.any(CommonPersonObjectClient.class), ArgumentMatchers.any(BaseRegisterViewHolder.class));
        Mockito.verify(baseRegisterRowOptions).populateClientRow(ArgumentMatchers.eq(cursor),
                ArgumentMatchers.eq(client), ArgumentMatchers.eq(client), ArgumentMatchers.eq(viewHolder));
    }

    @Test
    public void fillSetsTextIfViewNotNull() {
        TextView textView = new TextView(RuntimeEnvironment.application);
        String text = "Hello Worlds";
        BaseRegisterProvider.fillValue(textView, text);
        Assert.assertEquals("Hello Worlds", textView.getText());
    }

    @Test
    public void inflaterReturnsNonNullLayoutInflater() {
        Assert.assertNotNull(registerProvider.inflater());
    }

    @Test
    public void canCreateCustomViewHolderWithRowOptionsConfig() {
        BaseRegisterRowOptions baseRegisterRowOptions = Mockito.mock(BaseRegisterRowOptions.class);
        ViewGroup parent = Mockito.mock(ViewGroup.class);
        View view = Mockito.mock(View.class);
        LayoutInflater inflater = Mockito.mock(LayoutInflater.class);

        registerProvider = Mockito.mock(BaseRegisterProvider.class, Mockito.CALLS_REAL_METHODS);
        Whitebox.setInternalState(registerProvider, "baseRegisterRowOptions", baseRegisterRowOptions);
        Whitebox.setInternalState(registerProvider, "inflater", inflater);

        Mockito.doReturn(view).when(inflater).inflate(ArgumentMatchers.anyInt(), ArgumentMatchers.any(ViewGroup.class), ArgumentMatchers.anyBoolean());
        Mockito.doReturn(true).when(baseRegisterRowOptions).isCustomViewHolder();

        registerProvider.createViewHolder(parent);
        Mockito.verify(baseRegisterRowOptions).createCustomViewHolder(ArgumentMatchers.eq(view));
    }

    private void initializeModuleConfiguration() {
        CoreLibrary.init(org.smartregister.Context.getInstance());
        ModuleConfiguration customLibraryConfiguration = new ModuleConfiguration.Builder("Test Module Register",
                MockRegisterQueryProvider.class, new MockConfigViewsLib(), ActivityStarter.class)
                .setModuleMetadata(new ModuleMetadata(
                        new ModuleRegister(
                                "test_registration_form",
                                "ec_test_client",
                                "Test Registration",
                                "UPDATE TEST REGISTRATION", "custom-config"),
                        new MockLocationTagsConfiguration(), FormActivity.class, BaseProfileActivity.class, false, ""
                ))
                .setRegisterRowOptions(BaseRegisterRowOptions.class)
                .build();

        CoreLibrary.getInstance()
                .addModuleConfiguration(true, "testModule", customLibraryConfiguration);
    }
}
