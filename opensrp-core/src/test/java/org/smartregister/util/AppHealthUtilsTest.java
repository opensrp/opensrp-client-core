package org.smartregister.util;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.repository.AllSharedPreferences;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.smartregister.BaseRobolectricUnitTest.ASYNC_TIMEOUT;

/**
 * Created by ndegwamartin on 29/06/2021.
 */
@PrepareForTest({Toast.class,Utils.class})
public class AppHealthUtilsTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private Context context;

    @Mock
    private View view;

    private static final String PACKAGE_NAME = "org.smartregister.test";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(context.getPackageName()).thenReturn(PACKAGE_NAME);
    }

    @Test
    public void testConstructorBindsLongClickListenerToViewParam() {
        Mockito.doReturn(ApplicationProvider.getApplicationContext()).when(view).getContext();
        new AppHealthUtils(view);
        Mockito.verify(view).setOnLongClickListener(ArgumentMatchers.any(View.OnLongClickListener.class));
    }

    @Test
    public void testShowAppHealthSelectDialog() {
        AlertDialog alertDialog = AppHealthUtils.showAppHealthSelectDialog(ApplicationProvider.getApplicationContext());
        Assert.assertNotNull(alertDialog);
        Assert.assertNotNull(alertDialog.getListView());
        Assert.assertNotNull(alertDialog.getListView().getAdapter());
        Assert.assertEquals(ApplicationProvider.getApplicationContext().getString(R.string.download_database), alertDialog.getListView().getAdapter().getItem(0));
        Assert.assertEquals(ApplicationProvider.getApplicationContext().getString(R.string.view_sync_stats), alertDialog.getListView().getAdapter().getItem(1));
    }

    @Test
    public void testCreateCopyDBNameReturnsCorrectTag() {
        CoreLibrary coreLibrary = Mockito.mock(CoreLibrary.class);
        org.smartregister.Context opensrpContext = Mockito.mock(org.smartregister.Context.class);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);

        String userName = "testuser";
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        Mockito.doReturn(opensrpContext).when(coreLibrary).context();
        Mockito.doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();
        Mockito.doReturn(userName).when(allSharedPreferences).fetchRegisteredANM();

        String copyDBName = AppHealthUtils.createCopyDBName(context);
        Assert.assertNotNull(copyDBName);
        LocalDate today = LocalDate.now();
        Assert.assertEquals(String.format("%s_%s_%d-%02d-%02d", context.getApplicationContext().getPackageName(), userName, today.getYear(), today.getMonthValue(), today.getDayOfMonth()), copyDBName.substring(0, copyDBName.lastIndexOf("-")));
        Assert.assertTrue(copyDBName.contains(".db"));
    }

    @Test
    public void testRefreshFileSystemWhenAndroidVersionIsKitKatOrBelow() {
        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);

        AppHealthUtils.refreshFileSystem(context, true);

        Mockito.verify(context).sendBroadcast(intentArgumentCaptor.capture());

        Intent captured = intentArgumentCaptor.getValue();
        Assert.assertNotNull(captured);
        Assert.assertEquals(Intent.ACTION_MEDIA_MOUNTED, captured.getAction());
    }

    @Test
    public void testRefreshFileSystemWhenAndroidVersionIsAboveKitKat() {
        AppHealthUtils.refreshFileSystem(context, false);
        Mockito.verify(context, Mockito.never()).sendBroadcast(any(Intent.class));
    }

    @Test
    public void testTriggerDBCopying() throws Exception {
        CoreLibrary coreLibrary = Mockito.mock(CoreLibrary.class);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        org.smartregister.Context mockedContext = Mockito.mock(org.smartregister.Context.class);
        PowerMockito.mockStatic(Toast.class);
        PowerMockito.mockStatic(Utils.class);
       // PowerMockito.mockStatic(CoreLibrary.class);
        Context context = Mockito.mock(Context.class);

        Toast toastMock = Mockito.mock(Toast.class);
        Mockito.when(Toast.makeText(any(Context.class),ArgumentMatchers.anyString(),ArgumentMatchers.anyInt())).thenReturn(toastMock);
//        Mockito.when(Utils.getBooleanProperty(ArgumentMatchers.anyString())).thenReturn(false);
//        Mockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
//        Mockito.when(coreLibrary.context()).thenReturn(mockedContext);
//        Mockito.when(mockedContext.allSharedPreferences()).thenReturn(allSharedPreferences);
//        Mockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn("ANM");


        AppHealthUtils.triggerDBCopying(context);
        Thread.sleep(ASYNC_TIMEOUT);
        ArgumentCaptor<Context> contextArgumentCaptor = ArgumentCaptor.forClass(Context.class);
        Assert.assertNotNull(contextArgumentCaptor.getValue().getString(Mockito.anyInt()));
    }
}