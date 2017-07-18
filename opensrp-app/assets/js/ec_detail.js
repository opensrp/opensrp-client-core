/* globals Handlebars, $, window, alert */

function EC(ecBridge, formBridge) {
    return {
        populateInto: function (cssIdentifierOfRootElement) {
            $(cssIdentifierOfRootElement).html(Handlebars.templates.ec_detail(ecBridge.getCurrentEC()));
        },

        bindEveryItemToForm: function (cssIdentifierOfElement) {
            $(cssIdentifierOfElement).click(function () {
                formBridge.delegateToFormLaunchView($(this).data("form"), $(this).data("caseid"));
            });
        },

        bindToCamera: function (cssIdentifierOfElement) {
            $(cssIdentifierOfElement).click(function () {
                ecBridge.takePhoto();
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

function ECBridge() {
    var ecContext = window.context;
    if (typeof ecContext === "undefined" && typeof FakeECContext !== "undefined") {
        ecContext = new FakeECContext();
    }

    return {
        getCurrentEC: function () {
            return JSON.parse(ecContext.get());
        },
        takePhoto: function () {
            ecContext.takePhoto();
        }
    };
}

function FakeECContext() {
    return {
        get: function () {
            return JSON.stringify({
                    caseId: "CASE X",
                    ecNumber: "EC Number 1",
                    village: "village 1",
                    subcenter: "SubCenter 1",
                    isHighPriority: true,
                    coupleDetails: {
                        wifeName: "Woman 1",
                        husbandName: "Husband 1",
                        ecNumber: "EC Number 1",
                        isInArea: true
                    },
                    details: {
                        currentMethod: "female_sterilization",
                        familyPlanningMethodChangeDate: "2012-10-22",
                        headOfHousehold: "Head Person",
                        religion: "Religion X",
                        pregnancies: "3",
                        highPriorityReason: "Child_under_2 Parity_3_or_more Age",
                        caste: "st",
                        economicStatus: "bpl"
                    },
                    urgentTodos: [
                        {
                            message: "Alert 1",
                            formToOpen: "EC_FP_UPDATE",
                            isCompleted: true,
                            visitCode: "VISIT_CODE 1",
                            todoDate: "2012-10-24"
                        },
                        {
                            message: "Alert 2",
                            formToOpen: "EC_FP_UPDATE",
                            isCompleted: false,
                            visitCode: "VISIT_CODE 2",
                            todoDate: "2012-10-24"
                        }
                    ],
                    todos: [
                        {
                            message: "Family Planning follow up",
                            formToOpen: "EC_FP_UPDATE",
                            isCompleted: true,
                            visitCode: "VISIT_CODE 3",
                            todoDate: "2012-10-24"
                        },
                        {
                            message: "FP Resupply",
                            formToOpen: "EC_FP_UPDATE",
                            isCompleted: false,
                            visitCode: "VISIT_CODE 4",
                            todoDate: "2012-10-24"
                        }
                    ],
                    children: [
                        {
                            isFemale: true,
                            age: "1 year"
                        },
                        {
                            isFemale: false,
                            age: "2 years"
                        }
                    ],
                    timelineEvents: [
                        {
                            title: "Event 1",
                            details: [null, "Detail 2"],
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
                        },
                        {
                            title: "Event 4",
                            details: ["Detail 5", "Detail 6"],
                            type: "ECREGISTERED",
                            date: "3m 3d ago"
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
