package org.opensrp.service.formSubmissionHandler;

import org.opensrp.domain.form.FormSubmission;
import org.opensrp.service.ChildService;

public class VitaminAHandler implements FormSubmissionHandler {
    private final ChildService childService;

    public VitaminAHandler(ChildService childService) {
        this.childService = childService;
    }

    @Override
    public void handle(FormSubmission submission) {
        childService.updateVitaminAProvided(submission);
    }
}
