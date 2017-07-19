package org.smartregister.service.formsubmissionhandler;

import org.smartregister.domain.form.FormSubmission;

public interface FormSubmissionHandler {
    public void handle(FormSubmission submission);
}
