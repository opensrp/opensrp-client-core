package org.opensrp.service.formsubmissionhandler;

import org.opensrp.domain.form.FormSubmission;
import org.opensrp.service.ChildService;
import org.opensrp.service.MotherService;

public class DeliveryOutcomeHandler implements FormSubmissionHandler {
    private final MotherService motherService;
    private ChildService childService;

    public DeliveryOutcomeHandler(MotherService motherService, ChildService childService) {
        this.motherService = motherService;
        this.childService = childService;
    }

    @Override
    public void handle(FormSubmission submission) {
        motherService.deliveryOutcome(submission);
        childService.register(submission);
    }
}
