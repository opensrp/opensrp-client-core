/* globals Handlebars, $, window */

function ReportIndicatorDetail(reportIndicatorDetailBridge, cssIdOf) {
    return {
        populateInto: function () {
            $(cssIdOf.rootElement).html(Handlebars.templates.report_indicator_detail(reportIndicatorDetailBridge.getReportIndicatorDetail()));
        },
        bindEveryItemToIndicatorCaseListView: function () {
            $(cssIdOf.indicator).click(function () {
                reportIndicatorDetailBridge.delegateToReportIndicatorCaseList($(this).data("month"));
            });
        }
    };
}

function ReportIndicatorDetailBridge() {
    var reportIndicatorDetailContext = window.context;
    if (typeof reportIndicatorDetailContext === "undefined" && typeof ReportIndicatorDetailContext !== "undefined") {
        reportIndicatorDetailContext = new ReportIndicatorDetailContext();
    }

    return {
        getReportIndicatorDetail: function () {
            return JSON.parse(reportIndicatorDetailContext.get());
        },
        delegateToReportIndicatorCaseList: function (month) {
            return reportIndicatorDetailContext.startReportIndicatorCaseList(month);
        }
    };
}

function ReportIndicatorDetailContext() {
    return {
        get: function () {
            return JSON.stringify({
                    categoryDescription: "Family Planning",
                    description: "IUD Adoption",
                    identifier: "IUD",
                    annualTarget: "40",
                    monthlySummaries: [
                        {
                            currentProgress: "14",
                            month: "3",
                            aggregatedProgress: "14",
                            year: "2012"
                        },
                        {
                            currentProgress: "8",
                            month: "4",
                            aggregatedProgress: "22",
                            year: "2012"
                        },
                        {
                            currentProgress: "3",
                            month: "5",
                            aggregatedProgress: "25",
                            year: "2012"
                        }
                    ]}
            );
        },
        startReportIndicatorCaseList: function () {
            window.location.href = "report_indicator_case_list.html";
        }
    };
}
