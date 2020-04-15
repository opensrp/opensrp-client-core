package org.smartregister.view.viewholder;

import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.view.activity.SecuredActivity;

/**
 * Created by ndegwamartin on 2020-04-14.
 */
public class OnClickFormLauncherTest extends BaseUnitTest {

    @Mock
    private SecuredActivity activity;

    @Mock
    private View view;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOnClickInvokesStartFormActivityWithCorrectParams() {

        String testMetadata = "{\"fieldOverrides\": \"metadata-override-values\"}";

        Mockito.doNothing().when(activity).startFormActivity(TEST_FORM_NAME, TEST_BASE_ENTITY_ID, testMetadata);

        OnClickFormLauncher onClickFormLauncher = new OnClickFormLauncher(activity, TEST_FORM_NAME, TEST_BASE_ENTITY_ID, testMetadata);
        Assert.assertNotNull(onClickFormLauncher);

        onClickFormLauncher.onClick(view);

        Mockito.verify(activity).startFormActivity(TEST_FORM_NAME, TEST_BASE_ENTITY_ID, testMetadata);

    }

    @Test
    public void testOnClickInvokesStartFormActivityWithCorrectParamsNoMetadata() {


        Mockito.doNothing().when(activity).startFormActivity(TEST_FORM_NAME, TEST_BASE_ENTITY_ID, null);

        OnClickFormLauncher onClickFormLauncher = new OnClickFormLauncher(activity, TEST_FORM_NAME, TEST_BASE_ENTITY_ID);
        Assert.assertNotNull(onClickFormLauncher);

        onClickFormLauncher.onClick(view);

        Mockito.verify(activity).startFormActivity(TEST_FORM_NAME, TEST_BASE_ENTITY_ID, null);

    }
}
