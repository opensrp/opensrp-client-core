angular.module("smartRegistry.services")
    .service('ChildService', function ($filter, SmartHelper) {
        var schedules =
            [
                {
                    name: "bcg",
                    milestones: ['bcg'],
                    services: ["bcg"],
                    is_list: false
                },
                {
                    name: 'measles',
                    milestones: ['measles', 'measlesbooster'],
                    services: ['measles', 'measlesbooster']
                },
                {
                    name: "opv",
                    milestones: ['opv_0', 'opv_1', 'opv_2', 'opv_3'],
                    services: ['opv_0', 'opv_1', 'opv_2', 'opv_3'],
                    is_list: false
                },
                {
                    name: "opvbooster",
                    milestones: ['opvbooster'],
                    services: ['opvbooster'],
                    is_list: false
                },
                {
                    name: "dpt",
                    milestones: ['dptbooster_1', 'dptbooster_2'],
                    services: ['dptbooster_1', 'dptbooster_2'],
                    is_list: false
                },
                {
                    name: "pentavalent",
                    milestones: ['pentavalent_1', 'pentavalent_2', 'pentavalent_3'],
                    services: ['pentavalent_1', 'pentavalent_2', 'pentavalent_3']
                },
                {
                    name: "hepb",
                    milestones: [],
                    services: ["hepb_0"],
                    is_list: false
                },
                {
                    name: "vitamin_a",
                    milestones: [],
                    services: ['Vitamin A'],
                    is_list: true
                },
                {
                    name: "child_illness",
                    milestones: [],
                    services: ['Illness Visit'],
                    is_list: true
                }
            ];

        return {
            schedules: schedules,
            preProcess: function (clients) {
                clients.forEach(function (client) {
                        if (!client.visits)
                            client.visits = {};
                        schedules.forEach(function (schedule) {
                            SmartHelper.preProcessSchedule(client, schedule)
                        });
                        client.isBPL = client.economicStatus && client.economicStatus.toUpperCase() == 'BPL';
                        client.displayName = $filter('camelCase')($filter('humanize')(client.name));
                        client.displayAge = client.age || client.calculatedAge;
                        client.displayHusbandName = $filter('camelCase')($filter('humanize')(client.husbandName));
                        client.displayVillage = $filter('camelCase')($filter('humanize')(client.village));
                        client.isSC = client.caste && client.caste.toUpperCase() === "SC";
                        client.isST = client.caste && client.caste.toUpperCase() === "ST";
                    }
                );
            }

        }
    });
