angular.module("smartRegistry.controllers")
    .controller("fpRegisterController", function ($scope, FPService) {
        $scope.navigationBridge = new ANMNavigationBridge();
        $scope.bridge = new FPRegistryBridge();
        $scope.client_type = "woman";
        $scope.getClients = function () {
            return FPService.preProcessClients($scope.bridge.getClients());
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
                    label: "High Priority (HP)",
                    handler: "sortByPriority"
                },
                {
                    label: "EC Number",
                    handler: "sortByECNumber"
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

        $scope.sortByECNumber = function (client) {
            return parseInt(client.ec_number, 10) || 0;
        };

        $scope.defaultVillageOptions = {
            type: "filterVillage",
            options: [
                {
                    label: "All",
                    id: "",
                    handler: ""
                }
            ]
        };
        $scope.defaultVillageFilterHandler = "filterByVillageName";

        $scope.defaultVillage = $scope.defaultVillageOptions.options[0];
        $scope.villageFilterOption = $scope.defaultVillage;
        $scope.filterByVillageName = function (client, villageOption) {
            return (client.village.toUpperCase() === villageOption.id.toUpperCase());
        };

        $scope.defaultFPOptions = $scope.ecsWithFPMethodServiceModeOptions;
        $scope.ecsWithFPMethodServiceModeOptions = {
            type: "filterFPMethod",
            options: [
                {
                    label: "All Methods",
                    id: "filterByFPMethodBeingUsed",
                    handler: "filterByFPMethodBeingUsed"
                },
                {
                    label: "Condom",
                    id: "condom",
                    handler: "filterByFPMethod",
                    sideEffect: "condomSideEffect"
                },
                {
                    label: "DMPA/Injectable",
                    id: "dmpa_injectable",
                    handler: "filterByFPMethod",
                    sideEffect: "injectableSideEffect"
                },
                {
                    label: "IUCD",
                    id: "iud",
                    handler: "filterByFPMethod",
                    sideEffect: "iudSidEffect"
                },
                {
                    label: "OCP",
                    id: "ocp",
                    handler: "filterByFPMethod",
                    sideEffect: "ocpSideEffect"
                },
                {
                    label: "Female Sterilization",
                    id: "female_sterilization",
                    handler: "filterByFPMethod",
                    sideEffect: "sterilizationSideEffect"
                },
                {
                    label: "Male Sterilization",
                    id: "male_sterilization",
                    handler: "filterByFPMethod",
                    sideEffect: "sterilizationSideEffect"
                },
                {
                    label: "Others",
                    id: "others",
                    handler: "filterByFPMethodOther",
                    sideEffect: "otherSideEffect"
                }
            ]
        };
        $scope.ecsWithoutFPMethodServiceModeOptions = {
            type: "filterFPMethod",
            options: [
                {
                    label: "All EC",
                    id: "filterByNoFPMethod",
                    handler: "filterByNoFPMethod"
                },
                {
                    label: "High Priority (HP)",
                    id: "hp",
                    handler: "filterByPriority"
                },
                {
                    label: "2+ Children",
                    id: "2+_Children",
                    handler: "filterByNumberOfChildrenGreaterThanOne"
                },
                {
                    label: "1 Child",
                    id: "1_Child",
                    handler: "filterByNumberOfChildrenEqualToOne"
                }

            ]
        };
        $scope.defaultFPMethodOption = $scope.ecsWithFPMethodServiceModeOptions.options[0];
        $scope.serviceModeOption = $scope.defaultFPMethodOption;
        var fpMethodTemplate = "fp_methods";
        var hpECWithoutFPTemplate = "ec_without_fp";
        $scope.filterByPriority = function (client) {
            $scope.contentTemplate = hpECWithoutFPTemplate;
            return !doesClientUseFpMethod(client) && client.isHighPriority;
        };
        $scope.filterByNumberOfChildrenGreaterThanOne = function (client) {
            $scope.contentTemplate = hpECWithoutFPTemplate;
            return !doesClientUseFpMethod(client) && client.num_living_children >= "2";
        };
        $scope.filterByNumberOfChildrenEqualToOne = function (client) {
            $scope.contentTemplate = hpECWithoutFPTemplate;
            return !doesClientUseFpMethod(client) && client.num_living_children === "1";
        };
        $scope.filterByFPMethod = function (client, optionId) {
            $scope.contentTemplate = fpMethodTemplate;
            return client.fp_method === optionId;
        };
        $scope.filterByFPMethodOther = function (client) {
            $scope.contentTemplate = fpMethodTemplate;
            return client.fp_method === "lam" ||
                client.fp_method === "traditional_methods" ||
                client.fp_method === "centchroman";
        };
        $scope.filterByNoFPMethod = function (client) {
            $scope.contentTemplate = hpECWithoutFPTemplate;
            return !doesClientUseFpMethod(client);
        };
        $scope.filterByFPMethodBeingUsed = function (client) {
            $scope.contentTemplate = fpMethodTemplate;
            return doesClientUseFpMethod(client);
        };
        var doesClientUseFpMethod = function (client) {
            return (client.fp_method && client.fp_method !== "none");
        };

        $scope.currentOptions = null;
        $scope.currentFPOption = null;
        $scope.contentTemplate = fpMethodTemplate;
        $scope.isModalOpen = false;
        $scope.isFPModalOpen = false;
        $scope.isFPMethodsOptionSelected = true;
        $scope.filterFPMethod = function (option) {
            $scope.serviceModeOption = option;
        };
        $scope.selectFPMethodOption = function (fpMethodOptionSelected) {
            $scope.isFPMethodsOptionSelected = fpMethodOptionSelected;
            $scope.isFPPrioritizationOptionSelected = !fpMethodOptionSelected;
        };
        $scope.openFPModal = function (option) {
            $scope.isFPModalOpen = true;
            $scope.isModalOpen = false;
            $scope.currentFPOption = option;
        };

        $scope.closeFPModal = function () {
            $scope.isFPModalOpen = false;
            $scope.isModalOpen = false;
        };

        $scope.searchFilterString = "";
        $scope.searchCriteria = function (client, searchFilterString) {
            return ((client.name && client.name.toUpperCase().indexOf(searchFilterString.toUpperCase()) === 0) ||
                (client.ec_number && client.ec_number.toUpperCase().indexOf(searchFilterString.toUpperCase()) === 0));
        };

        $scope.getSideEffect = function (client) {
            // get the fp method
            var fp_method = client.fp_method;
            if (fp_method !== undefined) {
                // get matching side effect
                var option = $scope.ecsWithFPMethodServiceModeOptions.options.find(function (option) {
                    return option.id === fp_method;
                });
                if (option) {
                    return client[option.sideEffect];
                }
            }
        };

        $scope.openFPChangeModal = function (clientEntityId) {
            $scope.currentClientEntityId = clientEntityId;
            $scope.isFPChangeModalOpen = true;
        };

        $scope.closeFPChangeModal = function () {
            $scope.isFPChangeModalOpen = false;
        };

        $scope.openProfile = function (clientId) {
            $scope.navigationBridge.delegateToECProfile(clientId);
        };
    });
