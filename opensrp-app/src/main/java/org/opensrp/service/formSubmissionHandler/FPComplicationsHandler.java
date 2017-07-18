package org.opensrp.service.formSubmissionHandler;

import org.opensrp.domain.form.FormSubmission;
import org.opensrp.service.EligibleCoupleService;

public class FPComplicationsHandler implements FormSubmissionHandler {
    private EligibleCoupleService ecService;

    public FPComplicationsHandler(EligibleCoupleService ecService) {
        this.ecService = ecService;
    }

    @Override
    public void handle(FormSubmission submission) {
        ecService.fpComplications(submission);
    }
}
