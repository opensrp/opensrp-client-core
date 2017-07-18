function ANMLocationBridge() {
    var context = window.anmLocationContext;
    if (typeof context === "undefined" && typeof FakeANMLocationContext !== "undefined") {
        context = new FakeANMLocationContext();
    }

    return {
        get: function () {
            return JSON.parse(context.get());
        }
    };
}

function FakeANMLocationContext() {
    return {
        get: function () {
            return JSON.stringify({
                "district": "mysore",
                "phcName": "Bherya",
                "phcIdentifier": "bherya",
                "subCenter": "munjanahalli",
                "villages": [
                    "chikkabherya",
                    "kavalu_hosur",
                    "munjanahalli"
                ]
            });
        }
    };
}