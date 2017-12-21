package org.smartregister.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.event.Event;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.smartregister.domain.FetchStatus.fetched;
import static org.smartregister.domain.FetchStatus.fetchedFailed;

@RunWith(RobolectricTestRunner.class)
public class CacheTest {
    @Mock
    private CacheableData<String> cacheableData;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldGetAndCacheValueOnlyWhenItDoesNotExist() throws Exception {
        Cache<String> cache = new Cache<String>();
        when(cacheableData.fetch()).thenReturn("value");

        assertEquals("value", cache.get("key", cacheableData));

        verify(cacheableData).fetch();

        assertEquals("value", cache.get("key", cacheableData));

        verify(cacheableData, times(1)).fetch();
    }

    @Test
    public void shouldClearCacheWhenActionsAreUpdated() throws Exception {
        Cache<String> cache = new Cache<String>();
        when(cacheableData.fetch()).thenReturn("value");

        cache.get("key", cacheableData);
        Event.ON_DATA_FETCHED.notifyListeners(fetched);

        assertEquals("value", cache.get("key", cacheableData));
        verify(cacheableData, times(2)).fetch();

        Event.ON_DATA_FETCHED.notifyListeners(fetchedFailed);
        assertEquals("value", cache.get("key", cacheableData));
        verify(cacheableData, times(2)).fetch();
    }

    @Test
    public void shouldClearCacheWhenFormIsSubmitted() throws Exception {
        Cache<String> cache = new Cache<String>();
        when(cacheableData.fetch()).thenReturn("value");

        cache.get("key", cacheableData);
        Event.FORM_SUBMITTED.notifyListeners("ec_registration");

        assertEquals("value", cache.get("key", cacheableData));
        verify(cacheableData, times(2)).fetch();
    }
}
