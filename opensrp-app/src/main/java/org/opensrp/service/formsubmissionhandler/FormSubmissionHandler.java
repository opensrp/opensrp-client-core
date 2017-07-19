package org.opensrp.service.formsubmissionhandler;

import org.opensrp.domain.form.FormSubmission;

public interface FormSubmissionHandler {
    public void handle(FormSubmission submission);
}
