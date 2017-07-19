package org.opensrp.service.formsubmissionhandler;

import org.opensrp.domain.form.FormSubmission;
import org.opensrp.service.MotherService;

public class ANCRegistrationOAHandler implements FormSubmissionHandler {
    private MotherService motherService;

    public ANCRegistrationOAHandler(MotherService motherService) {
        this.motherService = motherService;
    }

    @Override
    public void handle(FormSubmission submission) {
        motherService.registerOutOfAreaANC(submission);
    }
}
