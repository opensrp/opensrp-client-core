/* globals Handlebars, $, drishti */
/* jshint -W065 */

var months = [ "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" ];

Handlebars.registerHelper('ifNotZero', function (context, options) {
    if (context !== 0) {
        return options.fn(this);
    }
});

Handlebars.registerHelper('ifequal', function (val1, val2, options) {
    if (val1 === val2) {
        return options.fn(this);
    }
    else {
        return options.inverse(this);
    }
});

Handlebars.registerHelper('ifFalse', function (val, options) {
    if (typeof val === "undefined") {
        return options.inverse(this);
    } else if (val === false) {
        return options.inverse(this);
    } else if (val.toString().toUpperCase() === "no".toUpperCase()) {
        return options.inverse(this);
    } else if (val === "") {
        return options.inverse(this);
    } else if (val === "0") {
        return options.inverse(this);
    }
    else {
        return options.fn(this);
    }
});

var capitalize = function (text) {
    return text.slice(0, 1).toUpperCase() + text.slice(1);
};

var formatText = function (unformattedText) {
    if (typeof unformattedText === "undefined" || unformattedText === null) {
        return "";
    }
    return capitalize(unformattedText.trim()).replace(/_/g, " ");
};

Handlebars.registerHelper('capitalize', capitalize);

Handlebars.registerHelper('camelCaseAndConvertToListItems', function (textWithSpacesAndUnderscores) {
    if (typeof textWithSpacesAndUnderscores === "undefined") {
        return "";
    }
    return new Handlebars.SafeString($(textWithSpacesAndUnderscores.trim().split(" ")).map(function (index, element) {
        if (element.trim() !== "") {
            return "<li>" + capitalize(element.trim()).replace(/_/g, " ") + "</li>";
        }
    }).get().join(" "));
});

Handlebars.registerHelper('formatText', function (unformattedText) {
    if (typeof unformattedText === "undefined" || unformattedText === null) {
        return "";
    }
    return capitalize(unformattedText.trim()).replace(/_/g, " ");
});

Handlebars.registerHelper('friendlyFPName', function (currentMethod) {
    var map = {
        ocp: 'OCP',
        iud: 'IUCD',
        condom: 'Condom',
        female_sterilization: 'Female Sterilization',
        male_sterilization: 'Male Sterilization',
        none: 'None',
        traditional_methods: 'Traditional Methods',
        dmpa_injectable: 'DMPA Injectable',
        lam: 'LAM'
    };

    return map[currentMethod] || formatText(currentMethod);
});

Handlebars.registerHelper('formatDate', function (unformattedDate) {
    if (typeof unformattedDate === "undefined") {
        return "";
    }
    var parsedDate = $.datepicker.parseDate('yy-mm-dd', unformattedDate);
    return $.datepicker.formatDate('dd-mm-yy', parsedDate);
});

Handlebars.registerHelper('monthName', function (monthNumber) {
    if (!monthNumber) {
        return "";
    }
    return months[monthNumber - 1];
});

Handlebars.registerHelper('shortYear', function (year) {
    if (!year) {
        return "";
    }
    return year.substr(2, 2);
});

Handlebars.registerHelper('formatSocialVulnerability', function (caste, economicStatus) {
    var formattedText = "";
    if (caste && (caste.toUpperCase() === "SC" || caste.toUpperCase() === "ST")) {
        formattedText += caste.toUpperCase();
        if (economicStatus && economicStatus.toUpperCase() === "BPL"){
            formattedText += ", " + economicStatus.toUpperCase();
        }
    }
    else if (economicStatus && economicStatus.toUpperCase() === "BPL"){
        formattedText += economicStatus.toUpperCase();
    }

    return formattedText;
});

Handlebars.registerHelper('percentage', function (value1, value2) {
    if (value2 === "NA") {
        return "NA";
    }
    return (Math.floor((parseInt(value1) * 100)/parseInt(value2))).toString() + "%";
});

Handlebars.registerHelper('internationalize', function (key) {
    return drishti.it.getLabel(key);
});
