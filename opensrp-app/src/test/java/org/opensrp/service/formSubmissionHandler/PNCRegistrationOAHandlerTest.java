package org.opensrp.service.formSubmissionHandler;

import org.robolectric.RobolectricTestRunner;
import org.opensrp.domain.form.FormSubmission;
import org.opensrp.service.ChildService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.opensrp.util.FormSubmissionBuilder.create;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class PNCRegistrationOAHandlerTest {
    @Mock
    private ChildService childService;

    private PNCRegistrationOAHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new PNCRegistrationOAHandler(childService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToChildService() throws Exception {
        FormSubmission submission = create().withFormName("pnc_registration_oa").withInstanceId("instance id 1").withVersion("122").build();

        handler.handle(submission);

        verify(childService).pncRegistrationOA(submission);
    }
}
