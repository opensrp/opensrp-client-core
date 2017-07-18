package org.ei.opensrp.service.formSubmissionHandler;

import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.ChildService;
import org.ei.opensrp.service.MotherService;

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
