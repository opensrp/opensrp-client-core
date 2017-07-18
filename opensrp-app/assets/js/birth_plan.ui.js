/* globals Handlebars */

Handlebars.registerHelper('imageBasedOnYesOrNo', function (val) {
    if (!val || val.toString().toUpperCase() !== "yes".toUpperCase()) {
        return '<img class="no" />';
    }
    return '<img class="yes" />';
});

Handlebars.registerHelper('formatBooleanToYesOrNo', function (val) {
    if (!val || val.toString().toUpperCase() !== "yes".toUpperCase()) {
        return "No";
    }
    return "Yes";
});

Handlebars.registerHelper('imageForDeliveryFacility', function (val) {
    var SUB_DISTRICT_HOSPITAL = "sdh";
    var DISTRICT_HOSPITAL = "dh";
    var HOME = "home";

    if (val.isHighRisk.toString().toUpperCase() === "yes".toUpperCase() &&
        !(val.deliveryFacilityType === SUB_DISTRICT_HOSPITAL || val.deliveryFacilityType === DISTRICT_HOSPITAL)) {
        return '<img class="no" />';
    }
    else if (val.deliveryFacilityType === HOME) {
        return '<img class="no" />';
    }
    else {
        return '<img class="yes" />';
    }
});

Handlebars.registerHelper('imageBasedOnValueIsEmptyOrNot', function (val) {
    if (val) {
        return '<img class="yes" />';
    } else {
        return '<img class="no" />';
    }
});

Handlebars.registerHelper('formatDeliveryFacilityType', function (val) {
    var mapOfDeliveryFacilityType = {
        "sub_center": "Sub Center",
        "phc": "PHC",
        "chc": "CHC",
        "sdh": "SDH",
        "dh": "DH",
        "private_facility": "Private Facility",
        "home": "Home"
    };
    var deliveryFacilityType = mapOfDeliveryFacilityType[val.deliveryFacilityType];
    if (val.deliveryFacility){
        return val.deliveryFacility + (deliveryFacilityType === undefined ? "" : ", " + deliveryFacilityType);
    }
    return deliveryFacilityType;
});

Handlebars.registerHelper('formatTransportPlan', function (val) {
    var mapOfDeliveryTransportPlan = {
        "family_vehicle": "Family vehicle",
        "borrowed_vehicle": "Borrowed vehicle",
        "hired_vehicle": "Hired vehicle",
        "ambulance": "Ambulance (108)",
        "others": "Others",
        "": ""
    };
    return mapOfDeliveryTransportPlan[val];
});

Handlebars.registerHelper('shouldDisplayBirthPlan', function (val, options) {
    if (val.details.deliveryFacilityType) {
        if (val.details.deliveryFacilityType !== "") {
            return options.fn(this);
        }
    } else {
        if (val.pregnancyDetails.isLastMonthOfPregnancy) {
            return options.inverse(this);
        }
        else {
            return null;
        }
    }
});