/* globals Handlebars, $, window, alert */

function PNC(pncBridge, formBridge) {
    return {
        populateInto: function (cssIdentifierOfRootElement) {
            $(cssIdentifierOfRootElement).html(Handlebars.templates.pnc_detail(pncBridge.getCurrentPNC()));
        },

        bindEveryItemToForm: function (cssIdentifierOfElement) {
            $(cssIdentifierOfElement).click(function () {
                formBridge.delegateToFormLaunchView($(this).data("form"), $(this).data("caseid"));
            });
        },

        bindToCamera: function (cssIdentifierOfElement) {
            $(cssIdentifierOfElement).click(function () {
                pncBridge.takePhoto();
            });
        },

        bindToDefaultPhoto: function (cssIdentifierOfElement, defaultPhotoPath) {
            $(cssIdentifierOfElement).on('error', function (e) {
                e.currentTarget.src = defaultPhotoPath;
            });
        },

        reloadPhoto: function (cssIdentifierOfElement, caseId, photoPath) {
            $(cssIdentifierOfElement).attr('src', photoPath);
        }
    };
}

function PNCBridge() {
    var pncContext = window.context;
    if (typeof pncContext === "undefined" && typeof FakePNCContext !== "undefined") {
        pncContext = new FakePNCContext();
    }

    return {
        getCurrentPNC: function () {
            return JSON.parse(pncContext.get());
        },

        takePhoto: function () {
            pncContext.takePhoto();
        }
    };
}

function FakePNCContext() {
    return {
        get: function () {
            return JSON.stringify({
                    caseId: "1234",
                    thayiCardNumber: "TC Number 1",
                    coupleDetails: {
                        wifeName: "Woman 1",
                        husbandName: "Husband 1",
                        ecNumber: "EC Number 1",
                        isInArea: true,
                        caste: "st",
                        economicStatus: "bpl"
                    },
                    location: {
                        villageName: "village 1",
                        subcenter: "SubCenter 1"
                    },
                    pncDetails: {
                        daysPostpartum: "23",
                        dateOfDelivery: "2012-10-24"
                    },
                    details: {
                        deliveryPlace: "Bherya PHC",
                        ashaName: "Shiwani",
                        ashaPhoneNumber: "987654321",
                        isHighRisk: true,
                        highRiskReason: "    obstructed_labor     eclampsia spontaneous_abortion     ",
                        deliveryComplications: "prolonged_labour something_bad"
                    },
                    urgentTodos: [
                        {
                            message: "PNC Visit 1",
                            formToOpen: "PNC_SERVICES",
                            isCompleted: true,
                            visitCode: "PNC 1",
                            todoDate: "2012-10-24"
                        },
                        {
                            message: "PNC Visit 2",
                            formToOpen: "PNC_SERVICES",
                            isCompleted: false,
                            visitCode: "PNC 2",
                            todoDate: "2012-10-24"
                        }
                    ],
                    todos: [
                        {
                            message: "Child Immunization 1",
                            formToOpen: "CHILD_IMMUNIZATION",
                            isCompleted: true,
                            visitCode: "VISIT_CODE 1",
                            todoDate: "2012-10-24"
                        },
                        {
                            message: "Child Immunization 2",
                            formToOpen: "CHILD_IMMUNIZATION",
                            isCompleted: false,
                            visitCode: "VISIT_CODE 2",
                            todoDate: "2012-10-24"
                        }
                    ],
                    timelineEvents: [
                        {
                            title: "Event 1",
                            details: ["Detail 1", "Detail 2"],
                            type: "PREGNANCY",
                            date: "1y 2m ago"
                        },
                        {
                            title: "Event 2",
                            details: ["Detail 1", "Detail 2"],
                            type: "FPCHANGE",
                            date: "1y 2m ago"
                        },
                        {
                            title: "Event 3",
                            details: ["Detail 3", "Detail 4"],
                            type: "ANCVISIT",
                            date: "2m 3d ago"
                        }
                    ]
                }
            );
        },
        takePhoto: function () {
            alert("launching camera app.");
        }
    };
}
