package org.opensrp.service.formSubmissionHandler;

import org.opensrp.domain.form.FormSubmission;
import org.opensrp.service.EligibleCoupleService;

public class ECRegistrationHandler implements FormSubmissionHandler {
    private EligibleCoupleService ecService;

    public ECRegistrationHandler(EligibleCoupleService ecService) {
        this.ecService = ecService;
    }

    @Override
    public void handle(FormSubmission submission) {
        ecService.register(submission);
    }
}
