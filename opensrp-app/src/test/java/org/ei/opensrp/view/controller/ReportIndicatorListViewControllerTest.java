package org.ei.opensrp.view.controller;

import android.content.Context;
import com.google.gson.Gson;
import org.ei.opensrp.domain.Report;
import org.ei.drishti.dto.MonthSummaryDatum;
import org.ei.opensrp.repository.AllReports;
import org.ei.opensrp.util.DateUtil;
import org.ei.opensrp.view.contract.CategoryReports;
import org.ei.opensrp.view.contract.IndicatorReport;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.ei.opensrp.domain.ReportsCategory.ANC_SERVICES;
import static org.ei.opensrp.domain.ReportsCategory.FPS;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportIndicatorListViewControllerTest {
    @Mock
    private Context context;

    @Mock
    private AllReports allReports;
    private ReportIndicatorListViewController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        DateUtil.fakeIt(LocalDate.parse("2012-10-10"));
        controller = new ReportIndicatorListViewController(context, allReports, FPS.value());
    }

    @Test
    public void shouldGetIndicatorReportsForGivenCategory() throws Exception {
        List<MonthSummaryDatum> monthlySummaries = asList(new MonthSummaryDatum("10", "2012", "2", "2", asList("123", "456")));
        Report iudReport = new Report("IUD", "40", new Gson().toJson(monthlySummaries));
        Report condomReport = new Report("CONDOM", "30", new Gson().toJson(monthlySummaries));
        when(allReports.allFor(anyList())).thenReturn(asList(iudReport, condomReport));

        String indicatorReports = controller.get();

        IndicatorReport iud = new IndicatorReport("IUD", "IUD Adoption", "40", "2", "10", "2012", "2");
        IndicatorReport condom = new IndicatorReport("CONDOM", "Condom Usage", "30", "2", "10", "2012", "2");
        String expectedIndicatorReports = new Gson().toJson(new CategoryReports("Family Planning Services", asList(iud, condom)));
        assertEquals(expectedIndicatorReports, indicatorReports);
    }

    @Test
    public void shouldIgnoreThoseReportsWhichHaveEmptyMonthlySummary() throws Exception {
        List<MonthSummaryDatum> monthlySummaries = asList(new MonthSummaryDatum("10", "2012", "2", "2", asList("123", "456")));
        Report iudReport = new Report("IUD", "40", new Gson().toJson(monthlySummaries));
        Report condomReport = new Report("CONDOM", "30", "[]");
        when(allReports.allFor(anyList())).thenReturn(asList(iudReport, condomReport));

        String indicatorReports = controller.get();

        IndicatorReport iud = new IndicatorReport("IUD", "IUD Adoption", "40", "2", "10", "2012", "2");
        String expectedIndicatorReports = new Gson().toJson(new CategoryReports("Family Planning Services", asList(iud)));
        assertEquals(expectedIndicatorReports, indicatorReports);
    }

    @Test
    public void shouldUseCurrentMonthDataForIndicatorReport() throws Exception {
        List<MonthSummaryDatum> monthlySummaries = asList(new MonthSummaryDatum("1", "2012", "2", "2", asList("123", "456")),
                new MonthSummaryDatum("10", "2012", "2", "4", asList("321", "654")));
        Report earlyANCRegistrationReport = new Report("ANC_LT_12", "40", new Gson().toJson(monthlySummaries));
        when(allReports.allFor(ANC_SERVICES.indicators())).thenReturn(asList(earlyANCRegistrationReport));

        controller = new ReportIndicatorListViewController(context, allReports, ANC_SERVICES.value());
        String reports = controller.get();

        IndicatorReport earlyANCRegistration = new IndicatorReport("EARLY_ANC_REGISTRATIONS", "Early ANC Registration", "40", "2", "10", "2012", "4");
        String expectedIndicatorReports = new Gson().toJson(new CategoryReports("ANC Services", asList(earlyANCRegistration)));
        assertEquals(expectedIndicatorReports, reports);
    }

    @Test
    public void shouldUseLatestMonthDataIfCurrentMonthDataNotAvailable() throws Exception {
        List<MonthSummaryDatum> monthlySummaries = asList(new MonthSummaryDatum("6", "2012", "2", "2", asList("123", "456")),
                new MonthSummaryDatum("8", "2012", "2", "4", asList("321", "654")));
        Report iudReport = new Report("IUD", "40", new Gson().toJson(monthlySummaries));
        when(allReports.allFor(FPS.indicators())).thenReturn(asList(iudReport));

        String indicatorReports = controller.get();

        IndicatorReport iud = new IndicatorReport("IUD", "IUD Adoption", "40", "0", "10", "2012", "4");
        String expectedIndicatorReports = new Gson().toJson(new CategoryReports("Family Planning Services", asList(iud)));
        assertEquals(expectedIndicatorReports, indicatorReports);
    }

    @Test
    public void shouldShowNAIfAnnualTargetNotAvailable() throws Exception {
        List<MonthSummaryDatum> monthlySummaries = asList(new MonthSummaryDatum("6", "2012", "2", "2", asList("123", "456")),
                new MonthSummaryDatum("8", "2012", "2", "4", asList("321", "654")));
        Report iudReport = new Report("IUD", null, new Gson().toJson(monthlySummaries));
        when(allReports.allFor(FPS.indicators())).thenReturn(asList(iudReport));

        String indicatorReports = controller.get();

        IndicatorReport iud = new IndicatorReport("IUD", "IUD Adoption", "NA", "0", "10", "2012", "4");
        String expectedIndicatorReports = new Gson().toJson(new CategoryReports("Family Planning Services", asList(iud)));
        assertEquals(expectedIndicatorReports, indicatorReports);
    }
}
