package org.smartregister.cursoradapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.R;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.SortOption;

/**
 * Created by samuelgithengi on 11/3/20.
 */
public class RecyclerViewFragmentTest extends BaseRobolectricUnitTest {

    private RecyclerViewFragmentMock recyclerViewFragment;

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

    @Test
    public void testGetAndSetTableName() {
        recyclerViewFragment.setTablename("ec_events");
        assertEquals("ec_events", recyclerViewFragment.getTablename());
    }

    @Test
    public void testGetSearchView2() {
        FragmentScenario<RecyclerViewFragmentMock> scenario = FragmentScenario.launchInContainer(RecyclerViewFragmentMock.class);
        assertNotNull(scenario);
        final RecyclerViewFragmentMock[] recyclerViewFragmentMockRef = new RecyclerViewFragmentMock[1];
        scenario.onFragment(fragment -> recyclerViewFragmentMockRef[0] = fragment);

        assertNotNull(recyclerViewFragmentMockRef[0].getSearchView());
        assertEquals(R.id.edt_search, recyclerViewFragmentMockRef[0].getSearchView().getId());
    }

    @Test
    public void testGetSearchCancelView() {
        FragmentScenario<RecyclerViewFragmentMock> scenario = FragmentScenario.launchInContainer(RecyclerViewFragmentMock.class);
        scenario.onFragment(fragment -> {

            assertNotNull(fragment.getSearchCancelView());
            assertEquals(R.id.btn_search_cancel, fragment.getSearchCancelView().getId());
        });

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
        FragmentScenario<RecyclerViewFragmentMock> scenario = FragmentScenario.launchInContainer(RecyclerViewFragmentMock.class);
        scenario.onFragment(fragment -> {

            RecyclerViewFragmentMock fragmentSpy = spy(fragment);
            fragmentSpy.onCreateView(LayoutInflater.from(ApplicationProvider.getApplicationContext()), (ViewGroup) fragmentSpy.getView().getParent(), mock(Bundle.class));
            verify(fragmentSpy).onInitialization();
            verify(fragmentSpy).setupSearchView(any(View.class));
            verify(fragmentSpy).onResumption();
        });
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