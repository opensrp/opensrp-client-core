package org.smartregister.cursoradapter;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

import static org.junit.Assert.assertEquals;

/**
 * Created by samuelgithengi on 11/3/20.
 */
public class RecyclerViewFragmentTest extends BaseRobolectricUnitTest {

    private RecyclerViewFragmentMock recyclerViewFragment;

    @Before
    public void setUp() {
        recyclerViewFragment = new RecyclerViewFragmentMock();
    }


    @Before
    public void setUpWithActivity() {
        AppCompatActivity activity = Robolectric.buildActivity(AppCompatActivity.class).create().get();
        recyclerViewFragment = new RecyclerViewFragmentMock();
        activity.getSupportFragmentManager().beginTransaction().add(recyclerViewFragment, "recyclerViewFragment").commit();
    }

    @Test
    public void testGetAndSetTableName() {
        recyclerViewFragment.setTablename("ec_events");
        assertEquals("ec_events", recyclerViewFragment.getTablename());
    }


    public static class RecyclerViewFragmentMock extends RecyclerViewFragment {

        @Override
        protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
            return null;
        }

        @Override
        protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
            return null;
        }

        @Override
        protected SmartRegisterClientsProvider clientsProvider() {
            return null;
        }

        @Override
        protected void onInitialization() {//do nothing
        }

        @Override
        protected void startRegistration() {//do nothing
        }

        @Override
        protected void onCreation() {//do nothing
        }
    }

}