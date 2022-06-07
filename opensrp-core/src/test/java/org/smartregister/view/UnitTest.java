package org.smartregister.view;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.TestApplication;

/**
 * Created by Vincent Karuri on 02/02/2021
 */

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class)
public abstract class UnitTest {

    private AutoCloseable autoCloseable;

    @Before
    public void setUpSuper() {
        try {
            autoCloseable = MockitoAnnotations.openMocks(this);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @After
    public void tearDownSuper() throws Exception {
        if (autoCloseable != null)
            autoCloseable.close();
        Context.destroyInstance();
        CoreLibrary.destroyInstance();
        try {
            Mockito.validateMockitoUsage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
