/* globals Handlebars, $, window */

function ANMNavigationPanel(anmNavigationBridge) {

    var populateDataInto = function (cssIdentifierOfRootElement, displayTemplate) {
        Handlebars.registerPartial("anm_navigation", Handlebars.templates.anm_navigation);
        $(cssIdentifierOfRootElement).html(displayTemplate(anmNavigationBridge.getANMInformation()));
    };

    var bindToReports = function (callbackToRunBeforeAnyAction, identifierOfElement) {
        runWithCallBack(callbackToRunBeforeAnyAction, identifierOfElement, function () {
            anmNavigationBridge.delegateToReports();
        });
    };

    var bindToVideos = function (callbackToRunBeforeAnyAction, identifierOfElement) {
        runWithCallBack(callbackToRunBeforeAnyAction, identifierOfElement, function () {
            anmNavigationBridge.delegateToVideos();
        });
    };

    var bindToFPSmartRegistry = function (callbackToRunBeforeAnyAction, identifierOfElement) {
        runWithCallBack(callbackToRunBeforeAnyAction, identifierOfElement, function () {
            anmNavigationBridge.delegateToFPSmartRegistry();
        });
    };

    var bindToECSmartRegistry = function (callbackToRunBeforeAnyAction, identifierOfElement) {
        runWithCallBack(callbackToRunBeforeAnyAction, identifierOfElement, function () {
            anmNavigationBridge.delegateToECSmartRegistry();
        });
    };

    var bindToANCSmartRegistry = function (callbackToRunBeforeAnyAction, identifierOfElement) {
        runWithCallBack(callbackToRunBeforeAnyAction, identifierOfElement, function () {
            anmNavigationBridge.delegateToANCSmartRegistry();
        });
    };

    var bindToPNCSmartRegistry = function (callbackToRunBeforeAnyAction, identifierOfElement) {
        runWithCallBack(callbackToRunBeforeAnyAction, identifierOfElement, function () {
            anmNavigationBridge.delegateToPNCSmartRegistry();
        });
    };

    var bindToChildSmartRegistry = function (callbackToRunBeforeAnyAction, identifierOfElement) {
        runWithCallBack(callbackToRunBeforeAnyAction, identifierOfElement, function () {
            anmNavigationBridge.delegateToChildSmartRegistry();
        });
    };

    var runWithCallBack = function (callbackToRunBeforeAnyAction, identifierOfElement, action) {
        $(identifierOfElement).click(function (e) {
            callbackToRunBeforeAnyAction();
            action(e);
        });
    };

    return {
        populateInto: function (cssIdentifierOfSidePanelElement, displayTemplate, callbackToRunBeforeAnyAction) {
            populateDataInto(cssIdentifierOfSidePanelElement, displayTemplate);
            bindToReports(callbackToRunBeforeAnyAction, "#reportsButton");
            bindToVideos(callbackToRunBeforeAnyAction, "#videosButton");
            bindToECSmartRegistry(callbackToRunBeforeAnyAction, "#ecSmartRegistryOption");
            bindToChildSmartRegistry(callbackToRunBeforeAnyAction, "#childMenuOption");
            bindToFPSmartRegistry(callbackToRunBeforeAnyAction, "#fpSmartRegistryOption");
            bindToANCSmartRegistry(callbackToRunBeforeAnyAction, "#ancSmartRegistryOption");
            bindToPNCSmartRegistry(callbackToRunBeforeAnyAction, "#pncSmartRegistryOption");
        }
    };
}

function ANMNavigationBridge() {
    var anmNavigationContext = window.navigationContext;
    if (typeof anmNavigationContext === "undefined" && typeof FakeANMNavigationContext !== "undefined") {
        anmNavigationContext = new FakeANMNavigationContext();
    }

    return {
        getANMInformation: function () {
            return JSON.parse(anmNavigationContext.get());
        },
        delegateToReports: function () {
            return anmNavigationContext.startReports();
        },
        delegateToVideos: function () {
            return anmNavigationContext.startVideos();
        },
        delegateToECSmartRegistry: function () {
            return anmNavigationContext.startECSmartRegistry();
        },
        delegateToFPSmartRegistry: function () {
            return anmNavigationContext.startFPSmartRegistry();
        },
        delegateToANCSmartRegistry: function () {
            return anmNavigationContext.startANCSmartRegistry();
        },
        delegateToPNCSmartRegistry: function () {
            return anmNavigationContext.startPNCSmartRegistry();
        },
        delegateToChildSmartRegistry: function () {
            return anmNavigationContext.startChildSmartRegistry();
        },
        goBack: function () {
            anmNavigationContext.goBack();
        },
        delegateToECProfile: function (entityId) {
            anmNavigationContext.startEC(entityId);
        },
        delegateToANCProfile: function (entityId) {
            anmNavigationContext.startANC(entityId);
        },
        delegateToPNCProfile: function (entityId) {
            anmNavigationContext.startPNC(entityId);
        },
        delegateToChildProfile: function (entityId) {
            anmNavigationContext.startChild(entityId);
        }
    };
}

function FakeANMNavigationContext() {
    return {
        get: function () {
            return JSON.stringify({
                anmName: "ANM X",
                pncCount: "4",
                ancCount: "5",
                childCount: "6",
                eligibleCoupleCount: "7",
                fpCount: "4"
            });
        },
        startReports: function () {
            window.location.href = "reports.html";
        },
        startVideos: function () {
            window.location.href = "smart_registry/videos.html";
        },
        startECSmartRegistry: function () {
            window.location = "smart_registry/ec_register.html";
        },
        startFPSmartRegistry: function () {
            window.location = "smart_registry/fp_register.html";
        },
        startANCSmartRegistry: function () {
            window.location = "smart_registry/anc_register.html";
        },
        startPNCSmartRegistry: function () {
            window.location = "smart_registry/pnc_register.html";
        },
        startChildSmartRegistry: function () {
            window.location = "smart_registry/child_register.html";
        },
        goBack: function () {
            window.location.href = "../home.html";
        },
        startEC: function () {
            window.location.href = "../ec_detail.html";
        },
        startANC: function () {
            window.location.href = "../anc_detail.html";
        },
        startPNC: function () {
            window.location.href = "../pnc_detail.html";
        },
        startChild: function () {
            window.location.href = "../child_detail.html";
        }
    };
}
