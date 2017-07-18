package org.opensrp.service.formSubmissionHandler;

import org.opensrp.domain.form.FormSubmission;

public interface FormSubmissionHandler {
    public void handle(FormSubmission submission);
}
