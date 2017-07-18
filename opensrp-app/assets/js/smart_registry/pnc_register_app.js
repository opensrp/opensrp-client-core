angular.module("smartRegistry.controllers")
    .controller("pncRegisterController", function ($scope, SmartHelper, ANCService, PNCService) {
        $scope.navigationBridge = new ANMNavigationBridge();
        $scope.bridge = new PNCRegistryBridge();
        $scope.client_type = "woman";

        $scope.getClients = function () {
            var clients = $scope.bridge.getClients();
            ANCService.preProcess(clients);
            PNCService.preProcess(clients);
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
                    label: "Date of Delivery",
                    handler: "sortByDeliveryDate",
                    sortDescending: true
                },
                {
                    label: "High Risk (HR)",
                    handler: "sortByRisk"
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

        $scope.sortByDeliveryDate = function (item) {
            return item.deliveryDate;
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

        $scope.locationStatusMapping = {
            "out_of_area": 1,
            "left_the_place": 2
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

        $scope.pncServiceOptions = {
            type: "pncService",
            options: [
                {
                    label: "Overview",
                    id: "overview",
                    handler: "changeContentBasedOnServiceMode"
                },
                /*{
                 label: "Benefits",
                 id: "benefits",
                 handler: "changeContentBasedOnServiceMode"
                 },*/
                {
                    label: "PNC Visits",
                    id: "pnc_visits",
                    handler: "changeContentBasedOnServiceMode"
                }
            ]
        };

        $scope.defaultPNCServiceOption = $scope.pncServiceOptions.options[0];
        $scope.serviceModeOption = $scope.defaultPNCServiceOption;

        $scope.pncService = function (option) {
            $scope.serviceModeOption = option;
        };

        $scope.searchFilterString = "";

        $scope.contentTemplate = $scope.pncServiceOptions.options[0].id;

        $scope.searchCriteria = function (client, searchFilterString) {
            return ((client.name && client.name.toUpperCase().indexOf(searchFilterString.toUpperCase()) === 0) ||
                (client.ec_number && client.ec_number.toUpperCase().indexOf(searchFilterString.toUpperCase()) === 0) ||
                (client.thayi && client.thayi.toUpperCase().indexOf(searchFilterString.toUpperCase()) === 0));
        };

        $scope.currentOptions = null;

        $scope.isModalOpen = false;

        $scope.isPNCFormModalOpen = false;

        $scope.openPNCFormModal = function (clientEntityId) {
            $scope.currentClientEntityId = clientEntityId;
            $scope.isPNCFormModalOpen = true;
        };

        $scope.closePNCFormModal = function () {
            $scope.currentClientEntityId = null;
            $scope.isPNCFormModalOpen = false;
        };

        $scope.daysPP = function (client) {
            return Math.floor(SmartHelper.daysBetween(new Date(Date.parse(client.deliveryDate)), $scope.getToday()));
        };

        $scope.pncComplications = function(pncComplications) {
            return pncComplications ? pncComplications.replace('no_problems', '') : '';
        };

        $scope.getToday = function () {
            return new Date();
        };

        $scope.isPNCOutsideFirst7Days = function (service) {
            return service.name === "PNC" && SmartHelper.daysBetween(
                new Date(Date.parse($scope.client.deliveryDate)), new Date(Date.parse(service.date))) > 7;
        };

        $scope.drawSevenDayGraphic = function (client, selector) {
            var svg = d3.selectAll(selector);
            // TODO: perhaps we could skip drawing if g exists for performance since we've already drawn
            var g = svg.select('g');
            g.remove();
            g = svg.append('g');
            var colors = {
                red: '#d13f3f',
                yellow: '#EDCA00',
                green: '#25aa4a',
                grey: '#B6B6B6'
            };
            var active_color = colors[client.visits.first_7_days.active_color || 'yellow'];

            (function (client, svg, color, red, yellow, green, grey) {
                var x_offset = 18;
                var x_scale = 28;
                var y_offset = 30;
                var thickness = 4, thickness_scaled = thickness * 0.5;
                var radius = 10, radius_scaled = radius * 0.7;
                var text_width = 13;
                var tick_height = 12;

                svg.selectAll('.lines')
                    .data(client.lines)
                    .enter()
                    .append('rect')
                    .attr('x', function (d) {
                        return ((d.start - 1) * x_scale) + x_offset;
                    })
                    .attr('y', y_offset - thickness / 2)
                    .attr('width', function (d) {
                        return (d.end - d.start) * x_scale;
                    })
                    .attr('height', thickness)
                    .attr('fill', function (d) {
                        return d.type === 'expected' ? grey : color;
                    });

                svg.selectAll('.ticks')
                    .data(client.ticks)
                    .enter()
                    .append('rect')
                    .attr('x', function (d) {
                        return ((d.day - 1) * x_scale) + x_offset;
                    })
                    .attr('y', y_offset - tick_height / 2)
                    .attr('width', thickness_scaled)
                    .attr('height', tick_height)
                    .attr('fill', function (d) {
                        return d.type === 'expected' ? grey : color;
                    });

                svg.selectAll('.circles')
                    .data(client.circles)
                    .enter()
                    .append('circle')
                    .attr('cx', function (d) {
                        return ((d.day - 1) * x_scale) + x_offset;
                    })
                    .attr('cy', y_offset)
                    .attr('fill', function (d) {
                        return d.type === 'actual' ? 'black' : (d.colored ? color : grey);
                    })
                    .attr('r', function (d) {
                        return d.type === 'expected' ? radius : radius_scaled;
                    });

                svg.selectAll('.statuses')
                    .data(client.statuses)
                    .enter()
                    .append('text')
                    .text(function (d) {
                        return d.status === 'missed' ? 'X' : 'V';
                    })
                    .text(function (d) {
                        return d.status === 'missed' ? '\uf00d' : '\uf00c';
                    })
                    .style('font-family', 'FontAwesome')
                    .attr('x', function (d) {
                        return (((d.day - 1) * x_scale) + x_offset) - text_width / 2;
                    })
                    .attr('y', function (d) {
                        return y_offset - radius * 1.5;
                    })
                    .attr('fill', function (d) {
                        return d.status === 'done' ? green : red;
                    });

                svg.selectAll('.day_nos')
                    .data(client.day_nos)
                    .enter()
                    .append('text')
                    .text(function (d) {
                        return d.day;
                    })
                    .attr('x', function (d) {
                        return (((d.day - 1) * x_scale) + x_offset) - text_width / 3;
                    })
                    .attr('y', (y_offset + radius) * 1.4)
                    .attr('fill', function (d) {
                        return d.type === 'actual' ? 'black' : grey;
                    });
            })(client.visits.first_7_days, g, active_color, colors.red, colors.yellow, colors.green, colors.grey);
        };

        $scope.openProfile = function (clientId) {
            $scope.navigationBridge.delegateToPNCProfile(clientId);
        };
    });
