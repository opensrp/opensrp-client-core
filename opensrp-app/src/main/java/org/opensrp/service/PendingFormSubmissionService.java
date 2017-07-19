package org.opensrp.service;

import org.opensrp.repository.FormDataRepository;

public class PendingFormSubmissionService {

    private FormDataRepository formDataRepository;

    public PendingFormSubmissionService(FormDataRepository formDataRepository) {
        this.formDataRepository = formDataRepository;
    }


    public long pendingFormSubmissionCount() {
        return formDataRepository.getPendingFormSubmissionsCount();
    }
}