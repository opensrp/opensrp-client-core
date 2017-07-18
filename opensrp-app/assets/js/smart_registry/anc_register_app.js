/* globals angular, ANMNavigationBridge, ANCRegistryBridge */

angular.module("smartRegistry.controllers")
    .controller("ancRegisterController", function ($scope, ANCService) {
        $scope.navigationBridge = new ANMNavigationBridge();
        $scope.bridge = new ANCRegistryBridge();
        $scope.client_type = "woman";
        $scope.getClients = function () {
            return ANCService.preProcess($scope.bridge.getClients());
        };

        $scope.clients = $scope.getClients();

        $scope.sortOptions = {
            type: "sort",
            options: [
                {
                    label: "Name (A to Z)",
                    handler: "sortByName",
                    sortDescending: false
                },
                {
                    label: "EDD",
                    handler: "sortByEDD",
                    sortDescending: false
                },
                {
                    label: "HRP",
                    handler: "sortByRisk",
                    sortDescending: false
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

        $scope.defaultSortOption = $scope.sortOptions.options[0];
        $scope.currentSortOption = $scope.defaultSortOption;
        $scope.sortList = $scope.sortByName;
        $scope.sortDescending = true;

        $scope.sortByEDD = function (item) {
            return item.edd;
        };

        $scope.sortByDueDate = function (item) {
            return item.dueDate;
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

        $scope.ancServiceOptions = {
            type: "ancService",
            options: [
                {
                    label: "Overview",
                    id: "overview",
                    handler: "changeContentBasedOnServiceMode"
                },
                {
                    label: "ANC Visits",
                    id: "visits",
                    handler: "changeContentBasedOnServiceMode"
                },
                {
                    label: "Hb/IFA",
                    id: "hb_ifa",
                    handler: "changeContentBasedOnServiceMode"
                },
                {
                    label: "TT",
                    id: "tt",
                    handler: "changeContentBasedOnServiceMode"
                },
                {
                    label: "Delivery Plan",
                    id: "delivery",
                    handler: "changeContentBasedOnServiceMode"
                }
            ]
        };

        $scope.locationStatusMapping = {
            "out_of_area": 1,
            "left_the_place": 2
        };

        $scope.defaultAncServiceOption = $scope.ancServiceOptions.options[0];
        $scope.serviceModeOption = $scope.defaultAncServiceOption;

        $scope.ancService = function (option) {
            $scope.serviceModeOption = option;
        };

        $scope.searchFilterString = "";

        $scope.contentTemplate = $scope.ancServiceOptions.options[0].id;

        $scope.searchCriteria = function (client, searchFilterString) {
            return ((client.name && client.name.toUpperCase().indexOf(searchFilterString.toUpperCase()) === 0) ||
                (client.ec_number && client.ec_number.toUpperCase().indexOf(searchFilterString.toUpperCase()) === 0) ||
                (client.thayi && client.thayi.toUpperCase().indexOf(searchFilterString.toUpperCase()) === 0));
        };

        $scope.currentOptions = null;

        $scope.isModalOpen = false;

        $scope.sumIFATablets = function (ifaData) {
            var numTablets = 0;
            if (ifaData.IFA !== undefined) {
                ifaData.IFA.forEach(function (ifa) {
                    numTablets += parseInt(ifa.data.dose, 10) || 0;
                });
            }
            return numTablets;
        };

        $scope.openANCFormModal = function (client) {
            $scope.currentClientEntityId = client.entityId;
            $scope.currentClient = client;
            $scope.isANCFormModalOpen = true;
        };

        $scope.closeANCFormModal = function () {
            $scope.isANCFormModalOpen = false;
        };

        $scope.weeksPregnant = function (client) {
            var lmp = Date.parse(client.lmp);
            if (lmp) {
                var lmp_date = new Date(lmp);
                var today = new Date();
                return Math.floor((today - lmp_date) / 1000 / 60 / 60 / 24 / 7);
            }
        };

        $scope.microformSchedules = ['tt', 'ifa'];

        $scope.useMicroForm = function (schedule) {
            return $scope.microformSchedules.indexOf(schedule) !== -1;
        };

        $scope.milestoneForm = {
            'ANC 1': 'anc_visit',
            'ANC 2': 'anc_visit',
            'ANC 3': 'anc_visit',
            'ANC 4': 'anc_visit',
            'TT 1': 'tt_1',
            'TT 2': 'tt_2',
            'TT Booster': 'tt_booster',
            'IFA 1': 'ifa',
            'IFA 2': 'ifa',
            'IFA 3': 'ifa'
        };

        $scope.hbLegendClass = function (level) {
            var legend_class;
            if (level < 7) {
                legend_class = "hb-legend-dangerous";
            }
            else if (level >= 7 && level < 11) {
                legend_class = "hb-legend-high";
            }
            else {
                legend_class = "hb-legend-normal";
            }
            return legend_class;
        };

        $scope.openProfile = function (clientId) {
            $scope.navigationBridge.delegateToANCProfile(clientId);
        };
    });