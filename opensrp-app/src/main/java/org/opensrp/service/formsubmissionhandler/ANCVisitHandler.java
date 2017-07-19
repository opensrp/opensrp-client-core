package org.opensrp.service.formsubmissionhandler;

import org.opensrp.domain.form.FormSubmission;
import org.opensrp.service.MotherService;

public class ANCVisitHandler implements FormSubmissionHandler {
    private MotherService motherService;

    public ANCVisitHandler(MotherService motherService) {
        this.motherService = motherService;
    }

    @Override
    public void handle(FormSubmission submission) {
        motherService.ancVisit(submission);
    }
}
