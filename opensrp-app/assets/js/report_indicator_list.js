/* globals Handlebars, $, window */

function ReportIndicatorList(reportIndicatorListBridge, cssIdOf) {
    return {
        populateInto: function () {
            $(cssIdOf.rootElement).html(Handlebars.templates.report_indicator_list(reportIndicatorListBridge.getReportIndicatorList()));
        },
        bindEveryItemToIndicatorDetailView: function () {
            $(cssIdOf.indicator).click(function () {
                reportIndicatorListBridge.delegateToReportIndicatorDetail($(this).data("indicator"));
            });
        }
    };
}

function ReportIndicatorListBridge() {
    var reportIndicatorListContext = window.context;
    if (typeof reportIndicatorListContext === "undefined" && typeof ReportIndicatorListContext !== "undefined") {
        reportIndicatorListContext = new ReportIndicatorListContext();
    }

    return {
        getReportIndicatorList: function () {
            return JSON.parse(reportIndicatorListContext.get());
        },
        delegateToReportIndicatorDetail: function (indicator) {
            return reportIndicatorListContext.startReportIndicatorDetail(indicator);
        }
    };
}

function ReportIndicatorListContext() {
    return {
        get: function () {
            return JSON.stringify({
                    description: "Family Planning Services",
                    indicatorReports: [
                        {
                            indicatorIdentifier: "OCP",
                            description: "Oral Pills",
                            annualTarget: "60",
                            currentProgress: "10",
                            currentMonth: "4",
                            year: "2012",
                            aggregatedProgress: "40"
                        },
                        {
                            indicatorIdentifier: "IUD",
                            description: "IUD Adoption",
                            annualTarget: "40",
                            currentProgress: "10",
                            currentMonth: "4",
                            aggregatedProgress: "22",
                            year: "2012"
                        }
                    ]}
            );
        },
        startReportIndicatorDetail: function () {
            window.location.href = "report_indicator_detail.html";
        }
    };
}
