package org.opensrp.service.formsubmissionhandler;

import org.opensrp.domain.form.FormSubmission;
import org.opensrp.service.EligibleCoupleService;

public class FPChangeHandler implements FormSubmissionHandler {
    private EligibleCoupleService ecService;

    public FPChangeHandler(EligibleCoupleService ecService) {
        this.ecService = ecService;
    }

    @Override
    public void handle(FormSubmission submission) {
        ecService.fpChange(submission);
    }
}
