function VillageBridge() {
    var context = window.villageContext;
    if (typeof context === "undefined" && typeof FakeVillageContext !== "undefined") {
        context = new FakeVillageContext();
    }

    return {
        getVillages: function () {
            return JSON.parse(context.villages());
        }
    };
}

function FakeVillageContext() {
    return {
        villages: function () {
            return JSON.stringify(
                [
                    {name: "bherya"},
                    {name: "chikkahalli"},
                    {name: "somanahalli_colony"},
                    {name: "chikkabherya"},
                    {name: "basavanapura"},
                    {name: "munjanahalli"},
                    {name: "chikkabheriya"}
                ]
            )
        }
    }
}
