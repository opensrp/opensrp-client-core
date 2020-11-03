package org.smartregister.cursoradapter;

import androidx.appcompat.app.AppCompatActivity;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.R;
import org.smartregister.cursoradapter.mock.RecyclerViewFragmentMock;
import org.smartregister.view.activity.mock.BaseRegisterActivityMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by samuelgithengi on 11/3/20.
 */
public class RecyclerViewFragmentTest extends BaseRobolectricUnitTest {

    private RecyclerViewFragmentMock recyclerViewFragment;

    @Before
    public void setUp() {
        AppCompatActivity activity = Robolectric.buildActivity(BaseRegisterActivityMock.class).create().start().resume().get();
        recyclerViewFragment = new RecyclerViewFragmentMock();
        activity.getSupportFragmentManager().beginTransaction().add(recyclerViewFragment, "recyclerViewFragment").commit();
    }

    @Test
    public void testGetAndSetTableName() {
        recyclerViewFragment.setTablename("ec_events");
        assertEquals("ec_events", recyclerViewFragment.getTablename());
    }

    @Test
    public void testGetSearchView() {
        assertNotNull(recyclerViewFragment.getSearchView());
        assertEquals(R.id.edt_search, recyclerViewFragment.getSearchView().getId());
    }

    @Test
    public void testGetSearchCancelView() {
        assertNotNull(recyclerViewFragment.getSearchCancelView());
        assertEquals(R.id.btn_search_cancel, recyclerViewFragment.getSearchCancelView().getId());
    }


}