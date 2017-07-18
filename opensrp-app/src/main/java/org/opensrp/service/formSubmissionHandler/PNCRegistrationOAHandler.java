package org.opensrp.service.formSubmissionHandler;

import org.opensrp.domain.form.FormSubmission;
import org.opensrp.service.ChildService;

public class PNCRegistrationOAHandler implements FormSubmissionHandler {
    private ChildService childService;

    public PNCRegistrationOAHandler(ChildService childService) {
        this.childService = childService;
    }

    @Override
    public void handle(FormSubmission submission) {
        childService.pncRegistrationOA(submission);
    }
}
