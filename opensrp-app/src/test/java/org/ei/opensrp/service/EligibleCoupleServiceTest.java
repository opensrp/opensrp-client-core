package org.ei.opensrp.service;

import org.robolectric.RobolectricTestRunner;
import org.ei.opensrp.domain.TimelineEvent;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.ei.opensrp.util.EasyMap.mapOf;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class EligibleCoupleServiceTest {
    @Mock
    private EligibleCoupleRepository eligibleCoupleRepository;
    @Mock
    private TimelineEventRepository timelineEventRepository;
    @Mock
    private AllTimelineEvents allTimelineEvents;
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private AllBeneficiaries allBeneficiaries;

    private EligibleCoupleService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new EligibleCoupleService(allEligibleCouples, allTimelineEvents, allBeneficiaries);
    }

    @Test
    public void shouldCreateTimelineEventWhenECIsRegistered() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("submissionDate")).thenReturn("2012-01-01");

        service.register(submission);

        verify(allTimelineEvents).add(TimelineEvent.forECRegistered("entity id 1", "2012-01-01"));
    }

    @Test
    public void shouldCloseEC() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");

        service.closeEligibleCouple(submission);

        verify(allEligibleCouples).close("entity id 1");
        verify(allBeneficiaries).closeAllMothersForEC("entity id 1");
    }

    @Test
    public void shouldNotCreateTimelineEventWhenECIsRegisteredWithoutSubmissionDate() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("submissionDate")).thenReturn(null);

        service.register(submission);

        verifyZeroInteractions(allTimelineEvents);
    }

    @Test
    public void shouldCreateTimelineEventAndUpdateEntityWhenFPChangeIsReported() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("currentMethod")).thenReturn("condom");
        when(submission.getFieldValue("newMethod")).thenReturn("ocp");
        when(submission.getFieldValue("familyPlanningMethodChangeDate")).thenReturn("2012-01-01");

        service.fpChange(submission);

        verify(allTimelineEvents).add(TimelineEvent.forChangeOfFPMethod("entity id 1", "condom", "ocp", "2012-01-01"));
        verify(allEligibleCouples).mergeDetails("entity id 1", mapOf("currentMethod", "ocp"));
    }

    @Test
    public void shouldUseFormSubmissionDateAsChangeDateWhenFPMethodIsChangedAndChangeDateIsBlank() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("currentMethod")).thenReturn("condom");
        when(submission.getFieldValue("newMethod")).thenReturn("none");
        when(submission.getFieldValue("submissionDate")).thenReturn("2012-02-01");

        service.fpChange(submission);

        verify(allTimelineEvents).add(TimelineEvent.forChangeOfFPMethod("entity id 1", "condom", "none", "2012-02-01"));
        verify(allEligibleCouples).mergeDetails("entity id 1", mapOf("currentMethod", "none"));
    }
}
