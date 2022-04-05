package org.smartregister.cursoradapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.Context;
import org.smartregister.R;
import org.smartregister.view.activity.mock.BaseRegisterActivityMock;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.SortOption;

/**
 * Created by samuelgithengi on 11/3/20.
 */
public class RecyclerViewFragmentTest extends BaseRobolectricUnitTest {

    private RecyclerViewFragmentMock recyclerViewFragment;

    private AppCompatActivity activity;

    @Mock
    private SortOption sortOption;

    @Mock
    private FilterOption filterOption;

    @Mock
    private RecyclerViewPaginatedAdapter<? extends RecyclerView.ViewHolder> adapter;

    @Before
    public void setUp() {
        recyclerViewFragment = spy(new RecyclerViewFragmentMock());
    }

    public void initWithActivity() {
        activity = Robolectric.buildActivity(BaseRegisterActivityMock.class).create().start().resume().get();
        activity.getSupportFragmentManager().beginTransaction().add(recyclerViewFragment, "recyclerViewFragment").commit();
        Context.getInstance();
        Context.getInstance().updateApplicationContext(activity.getApplicationContext());
    }

    @After
    public void tearDown() {
        if (activity != null) {
            activity.getDelegate().onDestroy();
        }
    }

    @Test
    public void testGetAndSetTableName() {
        recyclerViewFragment.setTablename("ec_events");
        assertEquals("ec_events", recyclerViewFragment.getTablename());
    }

    @Test
    public void testGetSearchView() {
        initWithActivity();
        assertNotNull(recyclerViewFragment.getSearchView());
        assertEquals(R.id.edt_search, recyclerViewFragment.getSearchView().getId());
    }

    @Test
    public void testGetSearchCancelView() {
        initWithActivity();
        assertNotNull(recyclerViewFragment.getSearchCancelView());
        assertEquals(R.id.btn_search_cancel, recyclerViewFragment.getSearchCancelView().getId());
    }


    @Test
    public void testGetCurrentVillageFilter() {
        ReflectionHelpers.setField(recyclerViewFragment, "currentVillageFilter", filterOption);
        assertEquals(filterOption, recyclerViewFragment.getCurrentVillageFilter());
    }

    @Test
    public void testGetAndSetCurrentSearchFilter() {
        recyclerViewFragment.setCurrentSearchFilter(filterOption);
        assertEquals(filterOption, recyclerViewFragment.getCurrentSearchFilter());
    }


    @Test
    public void testGetCurrentSortOption() {
        ReflectionHelpers.setField(recyclerViewFragment, "currentSortOption", sortOption);
        assertEquals(sortOption, recyclerViewFragment.getCurrentSortOption());
    }


    @Test
    public void testGetAndSetClientAdapter() {
        recyclerViewFragment.setClientsAdapter(adapter);
        assertEquals(adapter, recyclerViewFragment.getClientsCursorAdapter());
    }

    @Test
    public void testOnCreateShouldSetupViewsAndInvokeResumption() {
        initWithActivity();
        verify(recyclerViewFragment).onInitialization();
        verify(recyclerViewFragment).setupSearchView(any(View.class));
        verify(recyclerViewFragment).onResumption();
    }

    @Test
    public void testUpdateDefaultOptions() throws Exception {
        TextView mockTextView = mock(TextView.class);
        ReflectionHelpers.setField(recyclerViewFragment, "appliedSortView", mockTextView);
        ReflectionHelpers.setField(recyclerViewFragment, "appliedVillageFilterView", mockTextView);
        ReflectionHelpers.setField(recyclerViewFragment, "serviceModeView", mockTextView);
        ReflectionHelpers.setField(recyclerViewFragment, "titleLabelView", mockTextView);

        assertNull(recyclerViewFragment.getCurrentVillageFilter());
        assertNull(recyclerViewFragment.getCurrentServiceModeOption());
        assertNull(recyclerViewFragment.getCurrentSortOption());

        WhiteboxImpl.invokeMethod(recyclerViewFragment, "updateDefaultOptions");

        assertNotNull(recyclerViewFragment.getCurrentVillageFilter());
        assertNotNull(recyclerViewFragment.getCurrentServiceModeOption());
        assertNotNull(recyclerViewFragment.getCurrentSortOption());
    }
}