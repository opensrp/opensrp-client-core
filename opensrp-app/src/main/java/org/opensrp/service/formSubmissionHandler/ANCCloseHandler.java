package org.opensrp.service.formSubmissionHandler;

import org.opensrp.domain.form.FormSubmission;
import org.opensrp.service.MotherService;

public class ANCCloseHandler implements FormSubmissionHandler {
    private final MotherService motherService;

    public ANCCloseHandler(MotherService motherService) {
        this.motherService = motherService;
    }

    @Override
    public void handle(FormSubmission submission) {
        motherService.close(submission);
    }
}
