package org.smartregister.view.activity;

import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.service.ZiggyService;
import org.smartregister.view.activity.mock.BaseRegisterActivityMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by samuelgithengi on 6/30/20.
 */
public class BaseRegisterActivityTest extends BaseRobolectricUnitTest {

    private BaseRegisterActivity activity;

    @Mock
    private ZiggyService ziggyService;

    @Before
    public void setUp() {
        Whitebox.setInternalState(CoreLibrary.getInstance().context(), "ziggyService", ziggyService);
        activity = Robolectric.buildActivity(BaseRegisterActivityMock.class).create().start().resume().get();
    }

    @Test
    public void testOnCreate() {

        assertNotNull(activity.presenter);
        assertNotNull(activity.getRegisterFragment());
        assertNotNull(Whitebox.getInternalState(activity, "mPagerAdapter"));
    }

    @Test
    public void testRegisterBottomNavigation() {
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottom_navigation);
        assertNotNull(bottomNavigationView);
        assertEquals(5, bottomNavigationView.getMenu().size());

        MenuItem item = bottomNavigationView.getMenu().findItem(R.string.action_me);
        assertEquals(activity.getString(R.string.me), item.getTitle());
        assertNotNull(item.getIcon());
        assertNotNull(Whitebox.getInternalState(bottomNavigationView, "selectedListener"));
    }
}
