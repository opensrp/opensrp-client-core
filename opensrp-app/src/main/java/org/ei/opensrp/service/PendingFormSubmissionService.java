package org.ei.opensrp.service;

import org.ei.opensrp.repository.FormDataRepository;

public class PendingFormSubmissionService {

    private FormDataRepository formDataRepository;

    public PendingFormSubmissionService(FormDataRepository formDataRepository) {
        this.formDataRepository = formDataRepository;
    }


    public long pendingFormSubmissionCount() {
        return formDataRepository.getPendingFormSubmissionsCount();
    }
}