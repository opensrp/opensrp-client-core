/* globals Handlebars, $, window, alert */

function ANC(ancBridge, formBridge) {
    return {
        populateInto: function (cssIdentifierOfRootElement) {
            Handlebars.registerPartial("birth_plan", Handlebars.templates.birth_plan);
            $(cssIdentifierOfRootElement).html(Handlebars.templates.anc_detail(ancBridge.getCurrentANC()));
        },

        bindEveryItemToForm: function (cssIdentifierOfElement) {
            $(cssIdentifierOfElement).click(function () {
                formBridge.delegateToFormLaunchView($(this).data("form"), $(this).data("caseid"));
            });
        },

        bindToCamera: function (cssIdentifierOfElement) {
            $(cssIdentifierOfElement).click(function () {
                ancBridge.takePhoto();
            });
        },

        bindToDefaultPhoto: function (cssIdentifierOfElement, defaultPhotoPath) {
            $(cssIdentifierOfElement).on('error', function (e) {
                e.currentTarget.src = defaultPhotoPath;
            });
        },

        bindTimelineEventToShowMoreButton: function (timeLineEventListItem, showMoreButton, minNumberToShow) {
            $(timeLineEventListItem + ':gt(' + (minNumberToShow - 1) + ')').hide();
            if ($(timeLineEventListItem + ':not(:visible)').length === 0) {
                return;
            }
            $(showMoreButton).css('display', 'block').click(function () {
                var button = this;
                $(timeLineEventListItem + ':not(:visible):lt(' + minNumberToShow + ')').fadeIn(function () {
                    if ($(timeLineEventListItem + ':not(:visible)').length === 0) {
                        $(button).remove();
                    }
                });
            });
        },

        reloadPhoto: function (cssIdentifierOfElement, caseId, photoPath) {
            $(cssIdentifierOfElement).attr('src', photoPath);
        }
    };
}

function ANCBridge() {
    var ancContext = window.context;
    if (typeof ancContext === "undefined" && typeof FakeANCContext !== "undefined") {
        ancContext = new FakeANCContext();
    }

    return {
        getCurrentANC: function () {
            return JSON.parse(ancContext.get());
        },
        takePhoto: function () {
            ancContext.takePhoto();
        }
    };
}

function FakeANCContext() {
    return {
        get: function () {
            return JSON.stringify({
                    caseId: "1234",
                    thayiCardNumber: "TC Number 1",
                    coupleDetails: {
                        wifeName: "Woman 1",
                        husbandName: "Husband 1",
                        ecNumber: "EC Number 1",
                        isInArea: false,
                        caste: "st",
                        economicStatus: "bpl",
                        photo_path: "../../img/woman-placeholder.png"
                    },
                    location: {
                        villageName: "village 1",
                        subcenter: "subCenter 1"
                    },
                    pregnancyDetails: {
                        monthsPregnant: "8",
                        isEDDPassed: true,
                        edd: "2012-10-24",
                        isLastMonthOfPregnancy: true,
                        daysPastEdd: "23"
                    },
                    details: {
                        deliveryPlace: "Bherya PHC",
                        ashaName: "Shiwani",
                        ashaPhoneNumber: "9876543210",
                        isHighRisk: "yes",
                        highRiskReason: "    obstructed_labor     eclampsia spontaneous_abortion     ",
                        transportPlan: "ambulance",
                        deliveryFacility: "",
                        deliveryFacilityType: "chc",
                        contactNumber: "9999999999",
                        isThereABirthCompanion: "yes",
                        isHighRiskStatusReviewed: "yes"
                    },
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
                        },
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
                        },
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
                        },
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
                        },
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
                        },
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
