package org.opensrp.service.formsubmissionhandler;

import org.opensrp.domain.form.FormSubmission;
import org.opensrp.service.MotherService;

public class ANCRegistrationHandler implements FormSubmissionHandler {
    private MotherService motherService;

    public ANCRegistrationHandler(MotherService motherService) {
        this.motherService = motherService;
    }

    @Override
    public void handle(FormSubmission submission) {
        motherService.registerANC(submission);
    }
}
