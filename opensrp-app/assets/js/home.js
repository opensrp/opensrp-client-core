/* globals Handlebars, window */

function Home(anmNavigation, homeBridge) {
    return {
        populateInto: function (cssIdentifierOfSidePanelElement, callbackToRunBeforeAnyAction) {
            anmNavigation.populateInto(cssIdentifierOfSidePanelElement, Handlebars.templates.home, callbackToRunBeforeAnyAction);
        },
        pageHasFinishedLoading: function () {
            homeBridge.pageHasFinishedLoading();
        }
    };
}

function HomeBridge() {
    var homeContext = window.context;
    if (typeof homeContext === "undefined" && typeof FakeHomeContext !== "undefined") {
        homeContext = new FakeHomeContext();
    }

    return {
        pageHasFinishedLoading: function () {
            return homeContext.pageHasFinishedLoading();
        }

    };
}

function FakeHomeContext() {
    return {
        pageHasFinishedLoading: function () {
        }
    };
}
