package org.ei.opensrp.service.formSubmissionHandler;

import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.MotherService;

public class DeliveryPlanHandler implements FormSubmissionHandler {
    private final MotherService motherService;

    public DeliveryPlanHandler(MotherService motherService) {
        this.motherService = motherService;
    }

    @Override
    public void handle(FormSubmission submission) {
        motherService.deliveryPlan(submission);
    }
}
