package org.smartregister.interactor;

import static org.mockito.ArgumentMatchers.eq;
import static org.smartregister.AllConstants.DatabaseKeys.DB_VERSION;
import static org.smartregister.AllConstants.DatabaseKeys.VALIDATION_STATUS;
import static org.smartregister.AllConstants.DeviceInfo.MANUFACTURER;
import static org.smartregister.AllConstants.DeviceInfo.MODEL;
import static org.smartregister.AllConstants.DeviceInfo.OS_VERSION;
import static org.smartregister.AllConstants.SyncInfo.APP_BUILD_DATE;
import static org.smartregister.AllConstants.SyncInfo.APP_VERSION_CODE;
import static org.smartregister.AllConstants.SyncInfo.APP_VERSION_NAME;
import static org.smartregister.AllConstants.SyncInfo.INVALID_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.USER_LOCALITY;
import static org.smartregister.AllConstants.SyncInfo.USER_NAME;
import static org.smartregister.AllConstants.SyncInfo.USER_TEAM;
import static org.smartregister.AllConstants.SyncInfo.VALID_EVENTS;

import androidx.test.core.app.ApplicationProvider;

import net.sqlcipher.Cursor;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.service.UserService;
import org.smartregister.view.interactor.StatsFragmentInteractor;
import org.smartregister.view.presenter.StatsFragmentPresenter;

import java.util.Map;

public class StatsFragmentInteractorTest extends BaseUnitTest {

    private StatsFragmentInteractor statsFragmentInteractor;

    @Before
    public void setUp() {
        StatsFragmentPresenter presenter = Mockito.mock(StatsFragmentPresenter.class, Mockito.CALLS_REAL_METHODS);
        statsFragmentInteractor = new StatsFragmentInteractor(presenter);
    }

    @Test
    public void testPopulateUserInfoPopulatesCorrectValues() {
        CoreLibrary coreLibrary = Mockito.mock(CoreLibrary.class);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);

        Context context = Mockito.mock(Context.class);
        UserService userService = Mockito.mock(UserService.class);
        AllSharedPreferences sharedPreferences = Mockito.mock(AllSharedPreferences.class);

        Mockito.doReturn(context).when(coreLibrary).context();
        Mockito.doReturn(userService).when(context).userService();
        Mockito.doReturn(sharedPreferences).when(userService).getAllSharedPreferences();
        Mockito.doReturn("user").when(sharedPreferences).fetchRegisteredANM();
        Mockito.doReturn("team").when(sharedPreferences).fetchDefaultTeam(Mockito.anyString());
        Mockito.doReturn("locality").when(sharedPreferences).fetchCurrentLocality();

        ReflectionHelpers.callInstanceMethod(statsFragmentInteractor, "populateUserInfo");

        Map<String, String> syncInfoMap = ReflectionHelpers.getField(statsFragmentInteractor, "syncInfoMap");

        Assert.assertEquals("user", syncInfoMap.get(USER_NAME));
        Assert.assertEquals("team", syncInfoMap.get(USER_TEAM));
        Assert.assertEquals("locality", syncInfoMap.get(USER_LOCALITY));
    }

    @Test
    public void testPopulateBuildInfoPopulatesCorrectValues() {
        CoreLibrary coreLibrary = Mockito.mock(CoreLibrary.class);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);

        Context context = Mockito.mock(Context.class);
        android.content.Context appContext = Mockito.spy(ApplicationProvider.getApplicationContext());

        Mockito.doReturn(context).when(coreLibrary).context();
        Mockito.doReturn(appContext).when(context).applicationContext();
        Mockito.doReturn("org.smartregister").when(appContext).getPackageName();

        ReflectionHelpers.callInstanceMethod(statsFragmentInteractor, "populateBuildInfo");

        Map<String, String> syncInfoMap = ReflectionHelpers.getField(statsFragmentInteractor, "syncInfoMap");

        Assert.assertEquals("6.1.0-SNAPSHOT", syncInfoMap.get(APP_VERSION_NAME));
        Assert.assertEquals("1", syncInfoMap.get(APP_VERSION_CODE));
        Assert.assertEquals("0", syncInfoMap.get(DB_VERSION));
    }

    @Test
    public void testPopulateDeviceInfoPopulatesCorrectValues() {
        ReflectionHelpers.callInstanceMethod(statsFragmentInteractor, "populateDeviceInfo");

        Map<String, String> syncInfoMap = ReflectionHelpers.getField(statsFragmentInteractor, "syncInfoMap");

        Assert.assertEquals("robolectric", syncInfoMap.get(MANUFACTURER));
        Assert.assertEquals("robolectric", syncInfoMap.get(MODEL));
        Assert.assertEquals("28 Apr 2020", syncInfoMap.get(APP_BUILD_DATE));
        Assert.assertEquals("Q", syncInfoMap.get(OS_VERSION));
    }

    @Test
    public void testPopulateValidatedEventsInfoAddValidEventsCountWhenStatusIsValid() {
        Cursor cursor = Mockito.mock(Cursor.class);

        Mockito.doReturn(1).when(cursor).getColumnIndex(eq(VALIDATION_STATUS));
        Mockito.doReturn(BaseRepository.TYPE_Valid).when(cursor).getString(eq(1));
        Mockito.doReturn(10).when(cursor).getInt(eq(0));

        ReflectionHelpers.callInstanceMethod(statsFragmentInteractor, "populateValidatedEventsInfo",
                ReflectionHelpers.ClassParameter.from(Cursor.class, cursor));

        Map<String, String> syncInfoMap = ReflectionHelpers.getField(statsFragmentInteractor, "syncInfoMap");
        Assert.assertEquals("10", syncInfoMap.get(VALID_EVENTS));

    }

    @Test
    public void testPopulateValidatedEventsInfoAddInvalidEventsCountWhenStatusIsInvalid() {
        Cursor cursor = Mockito.mock(Cursor.class);

        Mockito.doReturn(1).when(cursor).getColumnIndex(eq(VALIDATION_STATUS));
        Mockito.doReturn(BaseRepository.TYPE_InValid).when(cursor).getString(eq(1));
        Mockito.doReturn(10).when(cursor).getInt(eq(0));

        ReflectionHelpers.callInstanceMethod(statsFragmentInteractor, "populateValidatedEventsInfo",
                ReflectionHelpers.ClassParameter.from(Cursor.class, cursor));

        Map<String, String> syncInfoMap = ReflectionHelpers.getField(statsFragmentInteractor, "syncInfoMap");
        Assert.assertEquals("10", syncInfoMap.get(INVALID_EVENTS));

    }

    @After
    public void destroy() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
    }
}
