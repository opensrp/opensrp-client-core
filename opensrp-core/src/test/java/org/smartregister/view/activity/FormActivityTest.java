package org.smartregister.view.activity;

import static org.mockito.Mockito.when;
import static org.smartregister.AllConstants.ENTITY_ID_PARAM;
import static org.smartregister.AllConstants.FIELD_OVERRIDES_PARAM;
import static org.smartregister.AllConstants.FORM_NAME_PARAM;
import static org.smartregister.view.activity.NativeECSmartRegisterActivityTest.locationJson;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.service.ZiggyService;
import org.smartregister.view.activity.mock.FormActivityMock;
import org.smartregister.view.controller.ANMLocationController;

/**
 * Created by kaderchowdhury on 12/11/17.
 */
public class FormActivityTest extends BaseUnitTest {

    private ActivityController<FormActivityMock> controller;

    private FormActivityMock activity;

    private org.smartregister.Context context_;

    @Mock
    private Context applicationContext;

    @Mock
    private ANMLocationController anmLocationController;

    @Mock
    private ZiggyService ziggyService;

    @BeforeClass
    public static void resetCoreLibrarySingleton() {
        CoreLibrary.destroyInstance();
    }

    @Before
    public void setUp() throws Exception {
        org.smartregister.Context context = org.smartregister.Context.getInstance();

        context.sharedRepositories();
        context_ = Mockito.spy(context);
        CoreLibrary.init(context_);
        when(context_.applicationContext()).thenReturn(applicationContext);
        when(context_.anmLocationController()).thenReturn(anmLocationController);
        when(anmLocationController.get()).thenReturn(locationJson);
        FormActivityMock.setContext(context_);
        when(context_.ziggyService()).thenReturn(ziggyService);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FormActivity.class);
        intent.putExtra(FORM_NAME_PARAM, "birthnotificationpregnancystatusfollowup");
        intent.putExtra(ENTITY_ID_PARAM, "entityID");
        intent.putExtra(FIELD_OVERRIDES_PARAM, "");
        controller = Robolectric.buildActivity(FormActivityMock.class, intent);
        activity = controller.get();
        controller.create()
                .start()
                .resume()
                .visible()
                .get();

    }

    @Test
    public void assertActivityNotNull() {
        Assert.assertNotNull(activity);
    }
}
