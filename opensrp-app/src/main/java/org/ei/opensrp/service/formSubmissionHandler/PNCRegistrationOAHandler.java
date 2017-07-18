package org.ei.opensrp.service.formSubmissionHandler;

import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.ChildService;

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
