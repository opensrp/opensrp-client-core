package org.smartregister.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseUnitTest;
import org.smartregister.R;
import org.smartregister.util.Utils;
import org.smartregister.view.LocationPickerView;
import org.smartregister.view.contract.MeContract;

/**
 * Created by ndegwamartin on 2020-03-24.
 */

public class MeFragmentTest extends BaseUnitTest {
    @Mock
    protected MeContract.Presenter presenter;
    @Mock
    private View view;
    @Mock
    private FragmentActivity activity;
    @Mock
    private Bundle bundle;
    @Mock
    private RelativeLayout meLocationSection;
    @Mock
    private RelativeLayout settingSection;
    @Mock
    private RelativeLayout logoutSection;
    @Mock
    private LocationPickerView facilitySelection;
    private MeFragment meFragment;
    private LayoutInflater layoutInflater;

    @Mock
    private ViewGroup viewGroup;

    @Before
    public void setUp() throws Exception {

        meFragment = Mockito.mock(MeFragment.class, Mockito.CALLS_REAL_METHODS);

        ReflectionHelpers.setField(meFragment, "presenter", presenter);

        layoutInflater = LayoutInflater.from(ApplicationProvider.getApplicationContext());

        Mockito.doReturn(meLocationSection).when(view).findViewById(R.id.me_location_section);
        Mockito.doReturn(settingSection).when(view).findViewById(R.id.setting_section);
        Mockito.doReturn(logoutSection).when(view).findViewById(R.id.logout_section);
        Mockito.doReturn(facilitySelection).when(view).findViewById(R.id.facility_selection);
    }

    @Test
    public void assertFragmentInstantiatesSuccessufully() {

        Assert.assertNotNull(meFragment);
        Assert.assertNotNull(meFragment.presenter);
    }

    /*
    @Test
    public void testOnCreateInvokesInitializePresenterMethod() {

        meFragment.onCreate(bundle);
        Mockito.verify(meFragment).initializePresenter();
    }

     */

    @Test
    @Ignore
    public void testOnCreateViewReturnsCorrectLayoutView() {

        View layoutView = meFragment.onCreateView(layoutInflater, viewGroup, bundle);
        Assert.assertNotNull(layoutView);
        Mockito.verify(layoutInflater).inflate(R.layout.fragment_me, viewGroup, false);
    }

    @Test
    public void testOnViewCreatedInvokesRequiredSetupMethods() {

        try (MockedStatic<Utils> utilsMockedStatic = Mockito.mockStatic(Utils.class)) {
            utilsMockedStatic.when(() -> Utils.getBooleanProperty(AllConstants.PROPERTY.DISABLE_LOCATION_PICKER_VIEW)).thenReturn(true);

            Mockito.doReturn(ApplicationProvider.getApplicationContext().getResources()).when(activity).getResources();

            meFragment.onViewCreated(view, bundle);

            Mockito.verify(meFragment).setUpViews(view);
            Mockito.verify(meFragment).setClickListeners();

            Mockito.verify(presenter).updateInitials();
            Mockito.verify(presenter).updateName();
        }
    }


    @After
    public void tearDown() throws Exception {
        meFragment = null;
        layoutInflater = null;
    }
}
