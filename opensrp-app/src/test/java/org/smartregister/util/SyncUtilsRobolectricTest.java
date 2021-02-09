package org.smartregister.util;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.service.UserService;

import java.io.IOException;


/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 09-02-2021.
 */
public class SyncUtilsRobolectricTest extends BaseRobolectricUnitTest {

    private SyncUtils syncUtils;
    private Context opensrpContext;
    private android.content.Context context;
    private UserService userService;

    @Before
    public void setUp() {
        opensrpContext = Mockito.spy(CoreLibrary.getInstance().context());
        userService = Mockito.spy(opensrpContext.userService());
        Mockito.doReturn(userService).when(opensrpContext).userService();

        context = Mockito.spy(RuntimeEnvironment.application);

        syncUtils = new SyncUtils(context);
        ReflectionHelpers.setField(syncUtils, "opensrpContext", opensrpContext);
    }

    @Ignore
    @Test
    public void logoutUser() throws AuthenticatorException, OperationCanceledException, IOException {
        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);
        Mockito.doNothing().when(context).startActivity(intentArgumentCaptor.capture());

        syncUtils.logoutUser(R.string.logout_text);

        Mockito.verify(userService).forceRemoteLogin(Mockito.anyString());
        Mockito.verify(userService).logoutSession();

        Intent intent = intentArgumentCaptor.getValue();
        Assert.assertEquals(Intent.ACTION_MAIN, intent.getAction());
        Assert.assertTrue(intent.getCategories().contains(Intent.CATEGORY_LAUNCHER));
        Assert.assertEquals("org.smartregister", intent.getPackage());

        Mockito.verify(context).startActivity(Mockito.any(Intent.class));

    }

    @Test
    public void getLogoutUserIntentShouldProvideAnonymouseIntentWhenLauncherActivityIsUnavailable() {
        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);
        Mockito.doNothing().when(context).startActivity(intentArgumentCaptor.capture());

        Intent intent = syncUtils.getLogoutUserIntent(R.string.logout_text);

        Assert.assertEquals(Intent.ACTION_MAIN, intent.getAction());
        Assert.assertTrue(intent.getCategories().contains(Intent.CATEGORY_LAUNCHER));
        Assert.assertEquals("org.smartregister.test", intent.getPackage());

    }
}
