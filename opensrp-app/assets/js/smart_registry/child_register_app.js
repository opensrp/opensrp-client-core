angular.module("smartRegistry.controllers")
    .controller("childRegisterController", function ($scope, SmartHelper, ChildService) {
        $scope.navigationBridge = new ANMNavigationBridge();
        $scope.bridge = new ChildRegistryBridge();
        $scope.client_type = "child";

        $scope.getClients = function () {
            var clients = $scope.bridge.getClients();
            ChildService.preProcess(clients);
            return clients;
        };

        $scope.clients = $scope.getClients();

        $scope.sortOptions = {
            type: "sort",
            options: [
                {
                    label: "Name (A to Z)",
                    handler: "sortByMothersName",
                    sortDescending: false,
                    secondarySortKey: 'motherName'
                },
                {
                    label: "Age",
                    handler: "sortByChildsAge",
                    sortDescending: true,
                    secondarySortKey: 'motherName'
                },
                {
                    label: "HR",
                    handler: "sortByRisk",
                    sortDescending: false,
                    secondarySortKey: 'motherName'
                },
                {
                    label: "BPL",
                    handler: "sortByBPL",
                    secondarySortKey: 'motherName'
                },
                {
                    label: "SC",
                    handler: "sortBySC",
                    secondarySortKey: 'motherName'
                },
                {
                    label: "ST",
                    handler: "sortByST",
                    secondarySortKey: 'motherName'
                }
            ]
        };

        $scope.defaultSortOption = $scope.sortOptions.options[0];
        $scope.currentSortOption = $scope.defaultSortOption;
        $scope.sortList = $scope.sortByName;
        $scope.sortDescending = true;

        $scope.sortByMothersName = function (client) {
            return client.motherName;
        };

        $scope.sortByChildsAge = function (item) {
            return item.dob;
        };

        $scope.sortByRisk = function (item) {
            return !item.isHighRisk;
        };

        $scope.defaultVillageOptions = {
            type: "filterVillage",
            options: [
                {
                    label: "All",
                    id: "",
                    handler: "filterByInAreaLocationStatus"
                },
                {
                    label: "O/A",
                    id: "out_of_area",
                    handler: "filterByLocationStatus"
                },
                {
                    label: "L/P",
                    id: "left_the_place",
                    handler: "filterByLocationStatus"
                }
            ]
        };

        $scope.defaultVillageFilterHandler = "filterByVillageName";

        $scope.defaultVillage = $scope.defaultVillageOptions.options[0];
        $scope.villageFilterOption = $scope.defaultVillage;
        $scope.filterByInAreaLocationStatus = function (client) {
            return client.locationStatus !== "left_the_place";
        };
        $scope.filterByVillageName = function (client, option) {
            return client.village.toUpperCase() === option.id.toUpperCase();
        };
        $scope.filterByLocationStatus = function (client, option) {
            return client.locationStatus === option.id;
        };

        $scope.childServiceOptions = {
            type: "childService",
            options: [
                {
                    label: "Overview",
                    id: "overview",
                    handler: "changeContentBasedOnServiceMode"
                },
                {
                    label: "Immunization 0-9",
                    id: "immunization_0_9",
                    handler: "changeContentBasedOnServiceMode"
                },
                {
                    label: "Immunization 9+",
                    id: "immunization_9_plus",
                    handler: "changeContentBasedOnServiceMode"
                }/*,
                 {
                 label: "Growth",
                 id: "growth",
                 handler: "changeContentBasedOnServiceMode"
                 }*/
            ]
        };

        $scope.defaultChildServiceOption = $scope.childServiceOptions.options[0];
        $scope.serviceModeOption = $scope.defaultChildServiceOption;

        $scope.childService = function (option) {
            $scope.serviceModeOption = option;
        };

        $scope.searchFilterString = "";

        $scope.contentTemplate = $scope.childServiceOptions.options[0].id;

        $scope.searchCriteria = function (client, searchFilterString) {
            return ((client.motherName && client.motherName.toUpperCase().indexOf(searchFilterString.toUpperCase()) === 0)
                || (client.ecNumber && client.ecNumber.toUpperCase().indexOf(searchFilterString.toUpperCase()) === 0)
                || (client.thayiCardNumber && client.thayiCardNumber.toUpperCase().indexOf(searchFilterString.toUpperCase()) === 0));
        };

        $scope.currentOptions = null;

        $scope.isModalOpen = false;

        $scope.isChildFormModalOpen = false;

        $scope.openChildFormModal = function (clientEntityId) {
            $scope.currentClientEntityId = clientEntityId;
            $scope.isChildFormModalOpen = true;
        };

        $scope.closeChildFormModal = function () {
            $scope.currentClientEntityId = null;
            $scope.isChildFormModalOpen = false;
        };

        $scope.nameOrMothers = function (client) {
            return client.name || "B/o " + client.motherName;
        };

        $scope.childsAge = function (client) {
            return SmartHelper.childsAge(new Date(Date.parse(client.dob)), new Date())
        };

        $scope.openProfile = function (clientId) {
            $scope.navigationBridge.delegateToChildProfile(clientId);
        };
    });
