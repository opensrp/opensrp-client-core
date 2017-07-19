package org.opensrp.view.controller;

import android.content.Context;
import android.content.Intent;
import com.google.gson.Gson;
import org.opensrp.AllConstants;
import org.opensrp.domain.EligibleCouple;
import org.opensrp.repository.AllEligibleCouples;
import org.opensrp.repository.AllTimelineEvents;
import org.opensrp.util.TimelineEventComparator;
import org.opensrp.view.activity.CameraLaunchActivity;
import org.opensrp.view.contract.Child;
import org.opensrp.view.contract.CoupleDetails;
import org.opensrp.view.contract.ECDetail;
import org.opensrp.view.contract.TimelineEvent;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import android.webkit.JavascriptInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.opensrp.AllConstants.ENTITY_ID;
import static org.opensrp.AllConstants.WOMAN_TYPE;

public class EligibleCoupleDetailController {
    private final Context context;
    private String caseId;
    private final AllEligibleCouples allEligibleCouples;
    private final AllTimelineEvents allTimelineEvents;

    public EligibleCoupleDetailController(Context context, String caseId, AllEligibleCouples allEligibleCouples,
                                          AllTimelineEvents allTimelineEvents) {
        this.context = context;
        this.caseId = caseId;
        this.allEligibleCouples = allEligibleCouples;
        this.allTimelineEvents = allTimelineEvents;
    }

    @JavascriptInterface
    public String get() {
        EligibleCouple eligibleCouple = allEligibleCouples.findByCaseID(caseId);

        ECDetail ecContext = new ECDetail(caseId, eligibleCouple.village(), eligibleCouple.subCenter(), eligibleCouple.ecNumber(),
                eligibleCouple.isHighPriority(), null, eligibleCouple.photoPath(), new ArrayList<Child>(), new CoupleDetails(eligibleCouple.wifeName(),
                eligibleCouple.husbandName(), eligibleCouple.ecNumber(), eligibleCouple.isOutOfArea()),
                eligibleCouple.details()).
                addTimelineEvents(getEvents());

        return new Gson().toJson(ecContext);
    }

    @JavascriptInterface
    public void takePhoto() {
        Intent intent = new Intent(context, CameraLaunchActivity.class);
        intent.putExtra(AllConstants.TYPE, WOMAN_TYPE);
        intent.putExtra(ENTITY_ID, caseId);
        context.startActivity(intent);
    }

    @JavascriptInterface
    private List<TimelineEvent> getEvents() {
        List<org.opensrp.domain.TimelineEvent> events = allTimelineEvents.forCase(caseId);
        List<TimelineEvent> timelineEvents = new ArrayList<TimelineEvent>();
        Collections.sort(events, new TimelineEventComparator());

        for (org.opensrp.domain.TimelineEvent event : events) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd-MM-YYYY");
            timelineEvents.add(new TimelineEvent(event.type(), event.title(), new String[]{event.detail1(), event.detail2()}, event.referenceDate().toString(dateTimeFormatter)));
        }

        return timelineEvents;
    }
}
