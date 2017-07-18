var fpMethodMap = {
    condom: 'Condom',
    dmpa_injectable: 'DMPA/Injectable',
    iud: 'IUCD',
    ocp: 'OCP',
    female_sterilization: 'Female Sterilization',
    male_sterilization: 'Male Sterilization',
    centchroman: 'Centchroman',
    traditional_methods: 'Traditional Methods',
    lam: 'LAM',
    none: 'None'
};

var friendlyNameMap = {
    vitamin_a: 'Vitamin A',
    bcg: 'BCG',
    measles: 'Measles',
    measlesbooster: 'Measles Booster',
    dpt: 'DPT Booster',
    opv: 'OPV',
    opv_0: 'OPV 0',
    opv_1: 'OPV 1',
    opv_2: 'OPV 2',
    opv_3: 'OPV 3',
    opvbooster: 'OPV Booster',
    pentavalent: 'Pentavalent',
    pentavalent_1: 'Pentavalent 1',
    pentavalent_2: 'Pentavalent 2',
    pentavalent_3: 'Pentavalent 3',
    dptbooster_1: 'DPT Boost. 1',
    dptbooster_2: 'DPT Boost. 2',
    hepb_0: "Hep. B",
    je: "JE",
    mmr: "MMR"
};

var friendlyAbbrevMap = {
    hepb: "Hep. B",
    pentavalent_1: 'Pentav. 1',
    pentavalent_2: 'Pentav. 2',
    pentavalent_3: 'Pentav. 3',
    dptbooster_1: 'DPT B. 1',
    dptbooster_2: 'DPT B. 2',
    opvbooster: 'OPV Booster',
    measlesbooster: 'Measles B'
};

angular.module("smartRegistry.filters")
    .filter('humanize', function () {
        return function (input) {
            try {
                var text = input.toString();
                text = text.replace(/_/g, " ");
                text = text.slice(0, 1).toUpperCase() + text.slice(1);
                return text;
            }
            catch (err) {
                return "";
            }
        }
    })
    .filter('camelCase', function () {
        return function (input) {
            try {
                var text = input.toString();
                var texts = [];
                text.split(" ").forEach(function (text) {
                    texts.push(text.slice(0, 1).toUpperCase() + text.slice(1));
                });
                return texts.join(" ");
            }
            catch (err) {
                return "";
            }
        }
    })
    .filter('fpMethodName', function () {
        return function (input) {
            return fpMethodMap[input] ? fpMethodMap[input] : input;

        }
    })
    .filter('commaSeparated', function () {
        return function (input) {
            try {
                var text = input.toString().trim();
                return text.split(/[\s]+/).join(", ");
            }
            catch (err) {
                return "";
            }
        }
    })
    .filter("dateFallsWithin", [function () {
        return function (input, start_date, date_field, period, invert) {
            return input.filter(function (item) {
                var end_date = item[date_field];
                var val = ((Date.parse(end_date) - Date.parse(start_date)) / 1000 / 60 / 60 / 24) <= period;
                // if invert is undefined or false return val, else return  !val
                if (!invert)
                    return val;
                else
                    return !val;
            });

        }
    }])
    .filter("slice", [function () {
        return function (input, start, end) {
            return input.slice(start, end);
        }
    }])
    .filter('friendlyName', function () {
        return function (input) {
            return friendlyNameMap[input] ? friendlyNameMap[input] : input;
        }
    })
    .filter('friendlyAbbrev',function () {
        return function (input) {
            return friendlyAbbrevMap[input] || friendlyNameMap[input] || input;
        }
    }).filter('startFrom', function () {
        return function (input, start) {
            start = +start;
            return input.slice(start);
        };
    });