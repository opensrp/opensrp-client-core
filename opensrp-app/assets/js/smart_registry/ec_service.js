angular.module("smartRegistry.services")
    .service('ECService', function (SmartHelper, $filter) {
        return {
            preProcess: function (clients) {
                clients.forEach(function (client) {
                        // calculate age from DOB
                        client.calculatedAge = SmartHelper.ageFromDOB(
                            new Date(Date.parse(client.dateOfBirth)), new Date());
                        client.children.forEach(function (child) {
                            child.calulatedAge = SmartHelper.childsAge(
                                new Date(Date.parse(child.dateOfBirth)), new Date());
                        });
                        client.familyPlanningMethodChangeDate = $filter('date')(client.familyPlanningMethodChangeDate, 'dd/MM/yy');
                        client.status.date = $filter('date')(client.status.date, 'dd/MM/yy');
                        client.status.edd = $filter('date')(client.status.edd, 'dd/MM/yy');
                        client.isBPL = client.economicStatus && (client.economicStatus.toUpperCase() == 'BPL');
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
