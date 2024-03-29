package org.smartregister.service.formsubmissionhandler;

import static org.mockito.Mockito.verify;
import static org.smartregister.util.FormSubmissionBuilder.create;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.service.MotherService;

public class PNCCloseHandlerTest extends BaseUnitTest {
    @Mock
    private MotherService motherService;

    private PNCCloseHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new PNCCloseHandler(motherService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToMotherService() throws Exception {
        FormSubmission submission = create().withFormName("pnc_close").withInstanceId("instance id 1").withVersion("122").build();

        handler.handle(submission);

        verify(motherService).close(submission);
    }
}
