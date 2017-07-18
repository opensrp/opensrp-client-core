package org.ei.opensrp.service.formSubmissionHandler;

import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.MotherService;

public class IFAHandler implements FormSubmissionHandler {
    private final MotherService motherService;

    public IFAHandler(MotherService motherService) {
        this.motherService = motherService;
    }

    @Override
    public void handle(FormSubmission submission) {
        motherService.ifaTabletsGiven(submission);
    }
}
