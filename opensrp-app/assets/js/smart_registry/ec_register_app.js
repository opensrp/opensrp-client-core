angular.module("smartRegistry.controllers")
    .controller("ECRegisterController", function ($scope, SmartHelper, ECService) {
        $scope.navigationBridge = new ANMNavigationBridge();
        $scope.bridge = new ECRegistryBridge();
        $scope.client_type = "ec";

        $scope.getClients = function () {
            var clients = $scope.bridge.getClients();
            ECService.preProcess(clients);
            return clients;
        };

        $scope.clients = $scope.getClients();

        $scope.sortOptions = {
            type: "sort",
            options: [
                {
                    label: "Name (A to Z)",
                    handler: "sortByName"
                },
                {
                    label: "EC Number",
                    handler: "sortByECNumber"
                },
                {
                    label: "High Priority (HP)",
                    handler: "sortByPriority"
                },
                {
                    label: "BPL",
                    handler: "sortByBPL"
                },
                {
                    label: "SC",
                    handler: "sortBySC"
                },
                {
                    label: "ST",
                    handler: "sortByST"
                }
            ]
        };

        $scope.sortByECNumber = function (client) {
            return client.ecNumber;
        };

        $scope.defaultSortOption = $scope.sortOptions.options[0];
        $scope.currentSortOption = $scope.defaultSortOption;
        $scope.sortList = $scope.sortByName;
        $scope.sortDescending = true;

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

        $scope.ecServiceOptions = {
            type: "ecService",
            options: [
                {
                    label: "All Eligible Couples",
                    id: "overview",
                    handler: "changeContentBasedOnServiceMode"
                }
            ]
        };

        $scope.defaultECServiceOption = $scope.ecServiceOptions.options[0];
        $scope.serviceModeOption = $scope.defaultECServiceOption;

        $scope.ecService = function (option) {
            $scope.serviceModeOption = option;
        };

        $scope.searchFilterString = "";

        $scope.contentTemplate = $scope.ecServiceOptions.options[0].id;

        $scope.searchCriteria = function (client, searchFilterString) {
            return ((client.name && client.name.toUpperCase().indexOf(searchFilterString.toUpperCase()) === 0)
                || (client.ecNumber && client.ecNumber.toString().indexOf(searchFilterString.toUpperCase()) === 0));
        };

        $scope.currentOptions = null;

        $scope.isModalOpen = false;

        $scope.isECFormModalOpen = false;

        $scope.openECFormModal = function (clientEntityId) {
            $scope.currentClientEntityId = clientEntityId;
            $scope.isECFormModalOpen = true;
        };

        $scope.closeECFormModal = function () {
            $scope.currentClientEntityId = null;
            $scope.isECFormModalOpen = false;
        };

        $scope.openProfile = function (clientId) {
            $scope.navigationBridge.delegateToECProfile(clientId);
        };

    });
