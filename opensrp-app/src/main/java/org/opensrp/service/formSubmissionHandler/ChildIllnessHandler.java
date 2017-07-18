package org.opensrp.service.formSubmissionHandler;

import org.opensrp.domain.form.FormSubmission;
import org.opensrp.service.ChildService;

public class ChildIllnessHandler implements FormSubmissionHandler {
    private ChildService childService;

    public ChildIllnessHandler(ChildService childService) {
        this.childService = childService;
    }

    @Override
    public void handle(FormSubmission submission) {
        childService.updateIllnessStatus(submission);
    }
}
