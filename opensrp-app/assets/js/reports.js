/* globals $, window */

function Reports(reportsBridge) {
    return {
        bindEveryItemToItsIndicatorListView: function (cssIdentifierOfElement) {
            $(cssIdentifierOfElement).click(function () {
                reportsBridge.delegateToIndicatorListView($(this).data("target"));
            });
        }
    };
}

function ReportsBridge() {
    var reportsContext = window.context;
    if (typeof reportsContext === "undefined" && typeof FakeReportsContext !== "undefined") {
        reportsContext = new FakeReportsContext();
    }

    return {
        delegateToIndicatorListView: function (category) {
            return reportsContext.startIndicatorListViewFor(category);
        }
    };
}

function FakeReportsContext() {
    return {
        startIndicatorListViewFor: function () {
                window.location.href = "report_indicator_list.html";
        }
    };
}
