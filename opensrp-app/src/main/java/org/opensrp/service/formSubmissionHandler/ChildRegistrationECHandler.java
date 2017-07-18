package org.opensrp.service.formSubmissionHandler;

import org.opensrp.domain.form.FormSubmission;
import org.opensrp.service.ChildService;

public class ChildRegistrationECHandler implements FormSubmissionHandler {
    private final ChildService childService;

    public ChildRegistrationECHandler(ChildService childService) {
        this.childService = childService;
    }

    @Override
    public void handle(FormSubmission submission) {
        childService.registerForEC(submission);
    }
}
