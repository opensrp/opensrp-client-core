package org.opensrp.service.formsubmissionhandler;

import org.opensrp.domain.form.FormSubmission;
import org.opensrp.service.ChildService;

public class ChildCloseHandler implements FormSubmissionHandler {
    private final ChildService childService;

    public ChildCloseHandler(ChildService childService) {
        this.childService = childService;
    }

    @Override
    public void handle(FormSubmission submission) {
        childService.close(submission);
    }
}
