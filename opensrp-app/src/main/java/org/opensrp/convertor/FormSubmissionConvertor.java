package org.opensrp.convertor;

import org.ei.drishti.dto.form.FormSubmissionDTO;

import java.util.ArrayList;
import java.util.List;

import static org.opensrp.domain.SyncStatus.SYNCED;

public class FormSubmissionConvertor {
    public static List<org.opensrp.domain.form.FormSubmission> toDomain(List<FormSubmissionDTO> formSubmissionsDto) {
        List<org.opensrp.domain.form.FormSubmission> submissions = new ArrayList<org.opensrp.domain.form.FormSubmission>();
        for (FormSubmissionDTO formSubmission : formSubmissionsDto) {
            submissions.add(new org.opensrp.domain.form.FormSubmission(
                    formSubmission.instanceId(),
                    formSubmission.entityId(),
                    formSubmission.formName(),
                    formSubmission.instance(),
                    formSubmission.clientVersion(),
                    SYNCED,
                    formSubmission.formDataDefinitionVersion(),
                    formSubmission.serverVersion()
            ));
        }
        return submissions;
    }
}
