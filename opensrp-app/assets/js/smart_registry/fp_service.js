angular.module("smartRegistry.services")
    .service('FPService', function ($filter) {
        var constants = {
            CONDOM_REFILL: "Condom Refill",
            DPMA_INJECTABLE_REFILL: "DMPA Injectable Refill",
            OCP_REFILL: "OCP Refill",
            MALE_STERILIZATION_FOLLOW_UP_1: "Male sterilization Followup 1",
            MALE_STERILIZATION_FOLLOW_UP_2: "Male sterilization Followup 2",
            FEMALE_STERILIZATION_FOLLOW_UP_1: "Female sterilization Followup 1",
            FEMALE_STERILIZATION_FOLLOW_UP_2: "Female sterilization Followup 2",
            FEMALE_STERILIZATION_FOLLOW_UP_3: "Female sterilization Followup 3",
            IUD_FOLLOW_UP_1: "IUD Followup 1",
            IUD_FOLLOW_UP_2: "IUD Followup 2",
            FP_FOLLOW_UP: "FP Followup",
            REFERRAL_FOLLOW_UP: "FP Referral Followup"
        };

        var refill_types = [
            constants.CONDOM_REFILL,
            constants.DPMA_INJECTABLE_REFILL,
            constants.OCP_REFILL
        ];

        var follow_up_types = [
            constants.MALE_STERILIZATION_FOLLOW_UP_1,
            constants.MALE_STERILIZATION_FOLLOW_UP_2,
            constants.FEMALE_STERILIZATION_FOLLOW_UP_1,
            constants.FEMALE_STERILIZATION_FOLLOW_UP_2,
            constants.FEMALE_STERILIZATION_FOLLOW_UP_3,
            constants.IUD_FOLLOW_UP_1,
            constants.IUD_FOLLOW_UP_2
        ];

        var alert_name_to_fp_method_map = {};
        alert_name_to_fp_method_map[constants.CONDOM_REFILL] = "condom";
        alert_name_to_fp_method_map[constants.DPMA_INJECTABLE_REFILL] = "dmpa_injectable";
        alert_name_to_fp_method_map[constants.OCP_REFILL] = "ocp";
        alert_name_to_fp_method_map[constants.MALE_STERILIZATION_FOLLOW_UP_1] = "male_sterilization";
        alert_name_to_fp_method_map[constants.MALE_STERILIZATION_FOLLOW_UP_2] = "male_sterilization";
        alert_name_to_fp_method_map[constants.FEMALE_STERILIZATION_FOLLOW_UP_1] = "female_sterilization";
        alert_name_to_fp_method_map[constants.FEMALE_STERILIZATION_FOLLOW_UP_2] = "female_sterilization";
        alert_name_to_fp_method_map[constants.FEMALE_STERILIZATION_FOLLOW_UP_3] = "female_sterilization";
        alert_name_to_fp_method_map[constants.IUD_FOLLOW_UP_1] = "iud";
        alert_name_to_fp_method_map[constants.IUD_FOLLOW_UP_2] = "iud";

        return {
            constants: constants,
            preProcessClients: function (clients) {
                clients.forEach(function (client) {
                    // find a referral alert if it exists
                    var referral_alert = client.alerts.find(function (a) {
                        return a.name === constants.REFERRAL_FOLLOW_UP;
                    });
                    var fp_followup_alert = client.alerts.find(function (a) {
                        return a.name === constants.FP_FOLLOW_UP;
                    });

                    if (referral_alert !== undefined) {
                        client.refill_follow_ups = {
                            name: referral_alert.name,
                            alert_index: client.alerts.indexOf(referral_alert),
                            type: "referral"
                        };
                    }
                    else if (fp_followup_alert !== undefined) {
                        client.refill_follow_ups = {
                            name: fp_followup_alert.name,
                            alert_index: client.alerts.indexOf(fp_followup_alert),
                            type: "follow-up"
                        };
                    }
                    else {
                        // find a normal follow-up alert that matches the fp method
                        var follow_up_alert = client.alerts.find(function (a) {
                            return follow_up_types.indexOf(a.name) > -1 &&
                                client.fp_method === alert_name_to_fp_method_map[a.name];
                        });

                        if (follow_up_alert !== undefined) {
                            client.refill_follow_ups = {
                                name: follow_up_alert.name,
                                alert_index: client.alerts.indexOf(follow_up_alert),
                                type: "follow-up"
                            };
                        }
                        else {
                            var refill_alert = client.alerts.find(function (a) {
                                return refill_types.indexOf(a.name) > -1 &&
                                    client.fp_method === alert_name_to_fp_method_map[a.name];
                            });

                            if (refill_alert !== undefined) {
                                client.refill_follow_ups = {
                                    name: refill_alert.name,
                                    alert_index: client.alerts.indexOf(refill_alert),
                                    type: "refill"
                                };
                            }
                        }
                    }
                    client.familyPlanningMethodChangeDate = $filter('date')(client.familyPlanningMethodChangeDate, 'dd/MM/yy');
                    client.complicationDate = $filter('date')(client.complicationDate, 'dd/MM/yy');
                    client.isBPL = client.economicStatus && (client.economicStatus.toUpperCase() == 'BPL');
                    client.displayName = $filter('camelCase')($filter('humanize')(client.name));
                    client.displayAge = client.age || client.calculatedAge;
                    client.displayHusbandName = $filter('camelCase')($filter('humanize')(client.husbandName));
                    client.displayVillage = $filter('camelCase')($filter('humanize')(client.village));
                    client.isSC = client.caste && client.caste.toUpperCase() === "SC";
                    client.isST = client.caste && client.caste.toUpperCase() === "ST";
                });
                return clients;
            }
        };
    });
