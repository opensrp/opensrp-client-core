/* globals Handlebars, $, window, alert */

function Child(childBridge, formBridge) {
    return {
        populateInto: function (cssIdentifierOfRootElement) {
            $(cssIdentifierOfRootElement).html(Handlebars.templates.child_detail(childBridge.getCurrentChild()));
        },

        bindEveryItemToForm: function (cssIdentifierOfElement) {
            $(cssIdentifierOfElement).click(function () {
                formBridge.delegateToFormLaunchView($(this).data("form"), $(this).data("caseid"));
            });
        },

        bindToCamera: function (cssIdentifierOfElement) {
            $(cssIdentifierOfElement).click(function () {
                childBridge.takePhoto();
            });
        },

        bindToDefaultPhoto: function (cssIdentifierOfElement, defaultPhotoPath) {
            $(cssIdentifierOfElement).on('error', function (e) {
                e.currentTarget.src = defaultPhotoPath;
            });
        },

        bindPhoto: function (cssIdentifierOfElement) {
            var photo_path;
            var gender = childBridge.getCurrentChild().childDetails.gender;
            if (gender === "male") {
                photo_path = "../img/icons/child-infant@3x.png";
            }
            else {
                photo_path = "../img/icons/child-girlinfant@3x.png";
            }
            $(cssIdentifierOfElement).attr('src', photo_path);
        },

        reloadPhoto: function (cssIdentifierOfElement, caseId, photoPath) {
            $(cssIdentifierOfElement).attr('src', photoPath);
        }
    };
}

function ChildBridge() {
    var childContext = window.context;
    if (typeof childContext === "undefined" && typeof FakeChildContext !== "undefined") {
        childContext = new FakeChildContext();
    }

    return {
        getCurrentChild: function () {
            return JSON.parse(childContext.get());
        },

        takePhoto: function () {
            childContext.takePhoto();
        }
    };
}

function FakeChildContext() {
    return {
        get: function () {
            return JSON.stringify({
                    caseId: "1234",
                    thayiCardNumber: "TC Number 1",
                    coupleDetails: {
                        wifeName: "Mother 1",
                        husbandName: "Husband 1",
                        ecNumber: "EC Number 1",
                        isInArea: true
                    },
                    location: {
                        villageName: "village 1",
                        subcenter: "SubCenter 1"
                    },
                    childDetails: {
                        age: "2 days",
                        dateOfBirth: "2012-10-24",
                        gender: "female"
                    },
                    details: {
                        deliveryPlace: "Bherya PHC",
                        ashaName: "Shiwani",
                        ashaPhoneNumber: "987654321",
                        isHighRisk: true,
                        highRiskReason: "    obstructed_labor     eclampsia spontaneous_abortion     ",
                        childWeight: "4.3"
                    },
                    urgentTodos: [
                        {
                            message: "Child Visit 1",
                            formToOpen: "Child_SERVICES",
                            isCompleted: true,
                            visitCode: "Child 1",
                            todoDate: "2012-10-24"
                        },
                        {
                            message: "Child Visit 2",
                            formToOpen: "Child_SERVICES",
                            isCompleted: false,
                            visitCode: "Child 2",
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
                    ],
                    photo_path: "../img/icons/child-girlinfant@3x.png"
                }
            );
        },
        takePhoto: function () {
            alert("launching camera app.");
        }
    };
}