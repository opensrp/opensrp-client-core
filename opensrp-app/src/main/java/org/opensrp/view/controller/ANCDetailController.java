package org.opensrp.view.controller;

import android.content.Context;
import android.content.Intent;
import com.google.gson.Gson;
import org.opensrp.AllConstants;
import org.opensrp.domain.EligibleCouple;
import org.opensrp.domain.Mother;
import org.opensrp.repository.AllBeneficiaries;
import org.opensrp.repository.AllEligibleCouples;
import org.opensrp.repository.AllTimelineEvents;
import org.opensrp.util.DateUtil;
import org.opensrp.util.TimelineEventComparator;
import org.opensrp.view.activity.CameraLaunchActivity;
import org.opensrp.view.contract.*;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import android.webkit.JavascriptInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.opensrp.AllConstants.*;

public class ANCDetailController {
    public static final int DURATION_OF_PREGNANCY_IN_WEEKS = 40;
    private final Context context;
    private final String caseId;
    private final AllEligibleCouples allEligibleCouples;
    private final AllBeneficiaries allBeneficiaries;
    private final AllTimelineEvents allTimelineEvents;

    public ANCDetailController(Context context, String caseId, AllEligibleCouples allEligibleCouples, AllBeneficiaries allBeneficiaries, AllTimelineEvents allTimelineEvents) {
        this.context = context;
        this.caseId = caseId;
        this.allEligibleCouples = allEligibleCouples;
        this.allBeneficiaries = allBeneficiaries;
        this.allTimelineEvents = allTimelineEvents;
    }

    @JavascriptInterface
    public String get() {
        Mother mother = allBeneficiaries.findMotherWithOpenStatus(caseId);
        EligibleCouple couple = allEligibleCouples.findByCaseID(mother.ecCaseId());

        LocalDate lmp = LocalDate.parse(mother.referenceDate());
        String edd = lmp.plusWeeks(DURATION_OF_PREGNANCY_IN_WEEKS).toString();
        Months numberOfMonthsPregnant = Months.monthsBetween(lmp, DateUtil.today());
        String photoPath = isBlank(couple.photoPath()) ? DEFAULT_WOMAN_IMAGE_PLACEHOLDER_PATH : couple.photoPath();

        int months = numberOfMonthsPregnant.getMonths();
        LocalDate eddDate = LocalDate.parse(edd);
        Days daysPastEdd = Days.daysBetween(eddDate, DateUtil.today());
        ANCDetail detail = new ANCDetail(caseId, mother.thayiCardNumber(),
                new CoupleDetails(couple.wifeName(), couple.husbandName(), couple.ecNumber(),
                        couple.isOutOfArea())
                        .withCaste(couple.details().get("caste"))
                        .withEconomicStatus(couple.details().get("economicStatus"))
                        .withPhotoPath(photoPath),
                new LocationDetails(couple.village(), couple.subCenter()),
                new PregnancyDetails(String.valueOf(months), edd, daysPastEdd.getDays()))
                .addTimelineEvents(getEvents())
                .addExtraDetails(mother.details());

        return new Gson().toJson(detail);
    }

    @JavascriptInterface
    public void takePhoto() {
        Intent intent = new Intent(context, CameraLaunchActivity.class);
        intent.putExtra(AllConstants.TYPE, WOMAN_TYPE);
        Mother mother = allBeneficiaries.findMotherWithOpenStatus(caseId);
        intent.putExtra(ENTITY_ID, mother.ecCaseId());
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
