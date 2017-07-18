package org.opensrp.service.formSubmissionHandler;

import org.opensrp.domain.form.FormSubmission;
import org.opensrp.service.EligibleCoupleService;

public class RenewFPProductHandler implements FormSubmissionHandler {
    private EligibleCoupleService ecService;

    public RenewFPProductHandler(EligibleCoupleService ecService) {
        this.ecService = ecService;
    }

    @Override
    public void handle(FormSubmission submission) {
        ecService.renewFPProduct(submission);
    }
}
