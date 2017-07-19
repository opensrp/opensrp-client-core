package org.opensrp.service.formsubmissionhandler;

import org.robolectric.RobolectricTestRunner;
import org.opensrp.domain.form.FormSubmission;
import org.opensrp.service.EligibleCoupleService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.opensrp.util.FormSubmissionBuilder.create;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class FPComplicationsHandlerTest {
    @Mock
    private EligibleCoupleService ecService;

    private FPComplicationsHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new FPComplicationsHandler(ecService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToECService() throws Exception {
        FormSubmission submission = create().withFormName("ec_registration").withInstanceId("instance id 1").withVersion("122").build();

        handler.handle(submission);

        verify(ecService).fpComplications(submission);
    }

}
