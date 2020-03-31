package org.smartregister.adapter;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import java.util.ArrayList;

public class ServiceLocationsAdapterTest extends BaseUnitTest {

    @Mock
    private Context applicationContext;

    private ArrayList<String> locationNames;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        locationNames = new ArrayList<>();
        locationNames.add("test1"); locationNames.add("test2"); locationNames.add("test3");
    }

    @Test
    public void testGetCount() {
        ServiceLocationsAdapter adapter = getAdapterWithFakeClients();

        Assert.assertEquals(adapter.getCount(), 3);
    }

    @Test
    public void testGetItemId() {
        ServiceLocationsAdapter adapter = getAdapterWithFakeClients();

        Assert.assertEquals(adapter.getItemId(0), 2321);
    }

    @Test
    public void testGetLocationAt() {
        ServiceLocationsAdapter adapter = getAdapterWithFakeClients();

        Assert.assertEquals(adapter.getLocationAt(0), "test1");
    }

    @Test
    public void testGetLocationNames() {
        ServiceLocationsAdapter adapter = getAdapterWithFakeClients();

        ArrayList<String> names = adapter.getLocationNames();

        Assert.assertEquals(names.get(0), "test1");
    }

    private ServiceLocationsAdapter getAdapterWithFakeClients() {
        return new ServiceLocationsAdapter(applicationContext, locationNames);
    }
}
