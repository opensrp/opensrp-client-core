package org.smartregister.view.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.adapter.SmartRegisterPaginatedAdapter;
import org.smartregister.customshadows.FontTextViewShadow;
import org.smartregister.util.mock.DrawableMock;
import org.smartregister.view.activity.mock.SecuredNativeSmartRegisterFragmentActivityMock;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.fragment.mock.SecuredNativeSmartRegisterFragmentMock;

/**
 * Created by kaderchowdhury on 14/11/17.
 */
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*"})
@PrepareForTest({CoreLibrary.class})
@Config(shadows = {FontTextViewShadow.class})
public class SecuredNativeSmartRegisterFragmentTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private ActivityController<SecuredNativeSmartRegisterFragmentActivityMock> controller;

    @InjectMocks
    private SecuredNativeSmartRegisterFragmentActivityMock activity;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private org.smartregister.Context context_;

    @Mock
    private View searchCancelButton;

    @Mock
    private EditText searchView;

    @Mock
    private SmartRegisterPaginatedAdapter clientsAdapter;

    @Mock
    private View clientsProgressView;

    @Mock
    private View clientsView;

    @Mock
    private TextView serviceModeView;

    private Drawable drawable;


    @Before
    public void setUp() throws Exception {
        org.mockito.MockitoAnnotations.initMocks(this);

        Intent intent = new Intent(RuntimeEnvironment.application, SecuredNativeSmartRegisterFragmentActivityMock.class);
        controller = Robolectric.buildActivity(SecuredNativeSmartRegisterFragmentActivityMock.class, intent);

        CoreLibrary.init(context_);

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context_);
        PowerMockito.when(context_.updateApplicationContext(Mockito.any(android.content.Context.class))).thenReturn(context_);
        Mockito.when(context_.IsUserLoggedOut()).thenReturn(false);

        activity = controller.create().start().resume().get();

        drawable = new DrawableMock();

    }

    @Test
    public void testActivityShouldNotBeNull() {

        Assert.assertNotNull(activity);
    }

    @After
    public void tearDown() {
        destroyController();
        activity = null;
        controller = null;
    }

    private void destroyController() {
        try {
            activity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can

        } catch (Exception e) {
            Log.e(getClass().getCanonicalName(), e.getMessage());
        }
    }

    @Test
    public void assertActivityInitsSecuredNativeSmartRegisterFragmentCorrectly() {


        Assert.assertNotNull(activity.getSupportFragmentManager().getFragments());
        Assert.assertTrue(activity.getSupportFragmentManager().getFragments().size() > 0);

        SecuredNativeSmartRegisterFragment securedNativeSmartRegisterFragment = (SecuredNativeSmartRegisterFragment) activity.getSupportFragmentManager().getFragments().get(0);

        Assert.assertNotNull(securedNativeSmartRegisterFragment.getCurrentSearchFilter());
        Assert.assertNotNull(securedNativeSmartRegisterFragment.getCurrentVillageFilter());
        Assert.assertNotNull(securedNativeSmartRegisterFragment.getCurrentSortOption());
        Assert.assertNotNull(securedNativeSmartRegisterFragment.getCurrentServiceModeOption());
    }

    @Test
    public void testSetupSearchView() {
        SecuredNativeSmartRegisterFragment securedNativeSmartRegisterFragment = (SecuredNativeSmartRegisterFragment) activity.getSupportFragmentManager().getFragments().get(0);

        View parent = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.smart_register_activity, null, false);

        View parentSpy = Mockito.spy(parent);

        Mockito.doReturn(searchView).when(parentSpy).findViewById(R.id.edt_search);
        Mockito.doReturn(searchCancelButton).when(parentSpy).findViewById(R.id.btn_search_cancel);

        securedNativeSmartRegisterFragment.setupSearchView(parentSpy);

        Assert.assertNotNull(securedNativeSmartRegisterFragment.getSearchView());
        Assert.assertNotNull(securedNativeSmartRegisterFragment.getSearchCancelView());

        Mockito.verify(parentSpy).findViewById(R.id.edt_search);
        Mockito.verify(parentSpy).findViewById(R.id.btn_search_cancel);

        Mockito.verify(searchView).setHint(SecuredNativeSmartRegisterFragmentMock.TEST_SEARCH_HINT);
        Mockito.verify(searchView).addTextChangedListener(ArgumentMatchers.any(TextWatcher.class));
        Mockito.verify(searchCancelButton).setOnClickListener(ArgumentMatchers.any(SecuredNativeSmartRegisterFragment.SearchCancelHandler.class));

    }


    @Test
    public void testServiceModeViewSetsServiceModeViewWithCorrectCompoundDrawableValues() {

        SecuredNativeSmartRegisterFragment securedNativeSmartRegisterFragment = (SecuredNativeSmartRegisterFragment) activity.getSupportFragmentManager().getFragments().get(0);

        Whitebox.setInternalState(securedNativeSmartRegisterFragment, "serviceModeView", serviceModeView);

        securedNativeSmartRegisterFragment.setServiceModeViewDrawableRight(drawable);

        Mockito.verify(serviceModeView).setCompoundDrawables(null, null, drawable, null);


    }

    @Test
    public void testSetupSearchViewOnTextChangeListenerRefreshesClientsAdapterList() {
        SecuredNativeSmartRegisterFragment securedNativeSmartRegisterFragment = (SecuredNativeSmartRegisterFragment) activity.getSupportFragmentManager().getFragments().get(0);
        securedNativeSmartRegisterFragment.setClientsAdapter(clientsAdapter);

        View parent = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.smart_register_activity, null, false);
        View parentSpy = Mockito.spy(parent);

        Mockito.doReturn(searchCancelButton).when(parentSpy).findViewById(R.id.btn_search_cancel);

        securedNativeSmartRegisterFragment.setupSearchView(parentSpy);
        securedNativeSmartRegisterFragment.getSearchView().setText("My Testing Search Text");

        Mockito.verify(clientsAdapter).refreshList(ArgumentMatchers.any(FilterOption.class), ArgumentMatchers.any(ServiceModeOption.class), ArgumentMatchers.any(FilterOption.class), ArgumentMatchers.any(SortOption.class));

        Mockito.verify(searchCancelButton).setVisibility(ArgumentMatchers.anyInt());

    }


    @Test
    public void testSetupSearchViewOnTextChangeListenerSetsCorrectStateForSearchCancelButton() {
        SecuredNativeSmartRegisterFragment securedNativeSmartRegisterFragment = (SecuredNativeSmartRegisterFragment) activity.getSupportFragmentManager().getFragments().get(0);
        securedNativeSmartRegisterFragment.setClientsAdapter(clientsAdapter);

        View parent = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.smart_register_activity, null, false);
        View parentSpy = Mockito.spy(parent);

        Mockito.doReturn(searchCancelButton).when(parentSpy).findViewById(R.id.btn_search_cancel);

        securedNativeSmartRegisterFragment.setupSearchView(parentSpy);
        securedNativeSmartRegisterFragment.getSearchView().setText("My Testing Search Text");

        Mockito.verify(searchCancelButton).setVisibility(View.VISIBLE);

        securedNativeSmartRegisterFragment.getSearchView().setText("");

        Mockito.verify(searchCancelButton).setVisibility(View.INVISIBLE);


    }

    @Test
    public void testGotoNextPageInvokesRequiredMethods() {
        SecuredNativeSmartRegisterFragment securedNativeSmartRegisterFragment = (SecuredNativeSmartRegisterFragment) activity.getSupportFragmentManager().getFragments().get(0);

        Assert.assertNotEquals(clientsAdapter, securedNativeSmartRegisterFragment.getClientsAdapter());
        securedNativeSmartRegisterFragment.setClientsAdapter(clientsAdapter);

        Assert.assertEquals(clientsAdapter, securedNativeSmartRegisterFragment.getClientsAdapter());

        securedNativeSmartRegisterFragment.gotoNextPage();

        Mockito.verify(clientsAdapter).nextPage();
        Mockito.verify(clientsAdapter).notifyDataSetChanged();

    }

    @Test
    public void testGoBackToPreviousPageInvokesRequiredMethods() {
        SecuredNativeSmartRegisterFragment securedNativeSmartRegisterFragment = (SecuredNativeSmartRegisterFragment) activity.getSupportFragmentManager().getFragments().get(0);

        Assert.assertNotEquals(clientsAdapter, securedNativeSmartRegisterFragment.getClientsAdapter());
        securedNativeSmartRegisterFragment.setClientsAdapter(clientsAdapter);

        Assert.assertEquals(clientsAdapter, securedNativeSmartRegisterFragment.getClientsAdapter());

        securedNativeSmartRegisterFragment.goBackToPreviousPage();
        Mockito.verify(clientsAdapter).previousPage();
        Mockito.verify(clientsAdapter).notifyDataSetChanged();

    }

}
