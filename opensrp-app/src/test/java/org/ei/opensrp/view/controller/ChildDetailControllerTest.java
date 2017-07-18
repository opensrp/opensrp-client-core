package org.ei.opensrp.view.controller;

import android.content.Context;
import com.google.gson.Gson;
import org.ei.opensrp.domain.Child;
import org.ei.opensrp.domain.EligibleCouple;
import org.ei.opensrp.domain.Mother;
import org.ei.opensrp.domain.TimelineEvent;
import org.ei.opensrp.repository.AllBeneficiaries;
import org.ei.opensrp.repository.AllEligibleCouples;
import org.ei.opensrp.repository.AllTimelineEvents;
import org.ei.opensrp.util.DateUtil;
import org.ei.opensrp.view.contract.BirthDetails;
import org.ei.opensrp.view.contract.ChildDetail;
import org.ei.opensrp.view.contract.CoupleDetails;
import org.ei.opensrp.view.contract.LocationDetails;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChildDetailControllerTest {
    @Mock
    Context context;
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private AllBeneficiaries allBeneficiaries;
    @Mock
    private AllTimelineEvents allTimelineEvents;

    private String caseId = "1234-5678-1234";
    private ChildDetailController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        DateUtil.fakeIt(new LocalDate(2012, 8, 1));
        controller = new ChildDetailController(context, caseId, allEligibleCouples, allBeneficiaries, allTimelineEvents);
    }

    @Test
    public void shouldGetChildDetailsAsJSON() throws Exception {
        TimelineEvent birthEvent = TimelineEvent.forChildBirthInChildProfile(caseId, "2011-10-21", null, null);
        TimelineEvent ancEvent = TimelineEvent.forMotherPNCVisit(caseId, "2", "2011-12-22", "bps 1", "bpd 1", "temp 1", "hb 1");
        TimelineEvent eventVeryCloseToCurrentDate = TimelineEvent.forMotherPNCVisit(caseId, "2", "2012-07-29", "bps 2", "bpd 2", "temp 2", "hb 2");

        HashMap<String, String> details = new HashMap<String, String>();
        details.put("ashaName", "Shiwani");
        details.put("dateOfDelivery", "2012-07-28");
        details.put("isHighRisk", "yes");
        details.put("highRiskReason", "Anaemia");

        when(allBeneficiaries.findChild(caseId)).thenReturn(new Child(caseId, "Mother-Case-Id", "TC 1", "2012-07-28", "male", details).withPhotoPath("photo path"));
        when(allBeneficiaries.findMother("Mother-Case-Id")).thenReturn(new Mother(caseId, "EC CASE 1", "TC 1", "2011-10-22").withDetails(details));
        when(allEligibleCouples.findByCaseID("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "Woman 1", "Husband 1", "EC Number 1", "Village 1", "Subcenter 1", new HashMap<String, String>()));
        when(allTimelineEvents.forCase(caseId)).thenReturn(asList(birthEvent, ancEvent, eventVeryCloseToCurrentDate));

        ChildDetail expectedDetail = new ChildDetail(caseId, "TC 1",
                new CoupleDetails("Woman 1", "Husband 1", "EC Number 1", false),
                new LocationDetails("Village 1", "Subcenter 1"),
                new BirthDetails("2012-07-28", "4 days", "male"), "photo path")
                .addTimelineEvents(asList(eventFor(eventVeryCloseToCurrentDate, "29-07-2012"), eventFor(ancEvent, "22-12-2011"), eventFor(birthEvent, "21-10-2011")))
                .addExtraDetails(details);

        String actualJson = controller.get();
        ChildDetail actualDetail = new Gson().fromJson(actualJson, ChildDetail.class);

        assertEquals(expectedDetail, actualDetail);
    }

    private org.ei.opensrp.view.contract.TimelineEvent eventFor(TimelineEvent pregnancyEvent, String expectedRelativeTime) {
        return new org.ei.opensrp.view.contract.TimelineEvent(pregnancyEvent.type(), pregnancyEvent.title(), new String[]{pregnancyEvent.detail1(), pregnancyEvent.detail2()}, expectedRelativeTime);
    }
}
