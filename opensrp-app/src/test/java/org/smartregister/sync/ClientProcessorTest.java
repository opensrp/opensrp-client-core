package org.smartregister.sync;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.BaseUnitTest;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.view.activity.DrishtiApplication;

/**
 * Created by raihan on 11/6/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DrishtiApplication.class, ClientProcessor.class})
public class ClientProcessorTest extends BaseUnitTest {
}
