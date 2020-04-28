package org.smartregister.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.R;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

/**
 * Created by ndegwamartin on 2020-04-28.
 */
public class BaseRegisterFragmentTest extends BaseUnitTest {

    private BaseRegisterFragment baseRegisterFragment;

    @Mock
    private LayoutInflater layoutInflater;

    @Mock
    private ViewGroup container;

    @Mock
    private Bundle bundle;

    private Activity activity;

    @Mock
    private EditText searchView;

    @Mock
    private Context opensrpContext;

    @Mock
    private TextWatcher textWatcher;

    @Mock
    private View.OnKeyListener hideKeyboard;

    @Mock
    private View searchCancelView;

    @Mock
    protected BaseRegisterFragmentContract.Presenter presenter;

    @Mock
    public RecyclerViewPaginatedAdapter clientAdapter;

    @Captor
    private ArgumentCaptor<Boolean> qrCodeArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> openSRPIdArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        baseRegisterFragment = Mockito.mock(BaseRegisterFragment.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(baseRegisterFragment, "presenter", presenter);
        ReflectionHelpers.setField(baseRegisterFragment, "clientAdapter", clientAdapter);

        activity = Robolectric.buildActivity(AppCompatActivity.class).get();
    }

    @Test
    public void assertFragmentInstantiatesCorrectly() {

        Assert.assertNotNull(baseRegisterFragment);
    }

    @Test
    public void assertGetNavBarOptionsProviderNotNull() {
        SecuredNativeSmartRegisterActivity.NavBarOptionsProvider provider = baseRegisterFragment.getNavBarOptionsProvider();
        Assert.assertNotNull(provider);
    }

    @Test
    public void assertGetNavBarOptionsProviderReturnsCorrectValueFormSearchHint() {
        SecuredNativeSmartRegisterActivity.NavBarOptionsProvider provider = baseRegisterFragment.getNavBarOptionsProvider();

        Mockito.doReturn(opensrpContext).when(baseRegisterFragment).context();
        Mockito.doReturn(RuntimeEnvironment.application.getResources().getString(R.string.search_hint)).when(opensrpContext).getStringResource(R.string.search_hint);

        String hint = RuntimeEnvironment.application.getResources().getString(R.string.search_hint);
        Assert.assertEquals(hint, provider.searchHint());
    }

    @Test
    @Ignore
    public void onCreateView() {

        View parentLayout = LayoutInflater.from(RuntimeEnvironment.application.getApplicationContext()).inflate(R.layout.fragment_base_register, null, false);
        Mockito.doReturn(parentLayout).when(layoutInflater).inflate(R.layout.fragment_base_register, container, false);
        Mockito.doReturn(activity).when(baseRegisterFragment).getActivity();

        baseRegisterFragment.onCreateView(layoutInflater, container, bundle);
    }

    @Test
    public void assertGetLayoutReturnsCorrectLayout() {

        Assert.assertEquals(R.layout.fragment_base_register, baseRegisterFragment.getLayout());
    }

    @Test
    public void assertUpdateSearchViewAddsCorrectListnersToSearchView() {

        Mockito.doReturn(searchView).when(baseRegisterFragment).getSearchView();

        ReflectionHelpers.setField(baseRegisterFragment, "textWatcher", textWatcher);
        ReflectionHelpers.setField(baseRegisterFragment, "hideKeyboard", hideKeyboard);

        baseRegisterFragment.updateSearchView();

        Mockito.verify(searchView).removeTextChangedListener(textWatcher);
        Mockito.verify(searchView).addTextChangedListener(textWatcher);
        Mockito.verify(searchView).setOnKeyListener(hideKeyboard);
    }

    @Test
    public void assertUpdateSearchBarHintSetsCorrectValue() {

        Mockito.doReturn(searchView).when(baseRegisterFragment).getSearchView();

        baseRegisterFragment.updateSearchBarHint(TEST_RANDOM_STRING);

        Mockito.verify(searchView).setHint(TEST_RANDOM_STRING);
    }

    @Test
    public void setSearchTermInitsCorrectValue() {

        Mockito.doReturn(searchView).when(baseRegisterFragment).getSearchView();

        baseRegisterFragment.setSearchTerm(TEST_RANDOM_STRING);

        Mockito.verify(searchView).setText(TEST_RANDOM_STRING);
    }

    @Test
    public void assertOnQRCodeSucessfullyScannedInvokesFilterWithCorrectParams() {

        String OPENSRP_ID = "8232-372-8L";
        String OPENSRP_ID_NO_HYPHENS = "82323728L";

        baseRegisterFragment = Mockito.spy(baseRegisterFragment);

        Mockito.doReturn(searchCancelView).when(baseRegisterFragment).getSearchCancelView();

        Mockito.doNothing().when(baseRegisterFragment).filter(ArgumentMatchers.eq(OPENSRP_ID_NO_HYPHENS), ArgumentMatchers.eq(""), ArgumentMatchers.anyString(), ArgumentMatchers.eq(true));

        baseRegisterFragment.onQRCodeSucessfullyScanned(OPENSRP_ID);

        Mockito.verify(baseRegisterFragment).filter(openSRPIdArgumentCaptor.capture(), stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), qrCodeArgumentCaptor.capture());

        String capturedIdFilterParam = openSRPIdArgumentCaptor.getValue();

        Assert.assertEquals(OPENSRP_ID_NO_HYPHENS, capturedIdFilterParam);

        Boolean isQRCodeParam  = qrCodeArgumentCaptor.getValue();
        Assert.assertNotNull(isQRCodeParam);
        Assert.assertTrue(isQRCodeParam);

    }

    @Test
    public void assertOnQRCodeSucessfullyScannedInvokessetUniqueIDWithCorrectParams() {

        String OPENSRP_ID = "8232-372-8L";

        baseRegisterFragment = Mockito.spy(baseRegisterFragment);

        Mockito.doReturn(searchCancelView).when(baseRegisterFragment).getSearchCancelView();

        Mockito.doNothing().when(baseRegisterFragment).filter(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());

        baseRegisterFragment.onQRCodeSucessfullyScanned(OPENSRP_ID);

        Mockito.verify(baseRegisterFragment).setUniqueID(openSRPIdArgumentCaptor.capture());

        String capturedIdFilterParam = openSRPIdArgumentCaptor.getValue();

        Assert.assertEquals(OPENSRP_ID, capturedIdFilterParam);
    }

}