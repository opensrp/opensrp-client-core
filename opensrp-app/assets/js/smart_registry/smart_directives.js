'use strict';

/* Directives */


angular.module('smartRegistry.directives', [])
    .directive('srServiceButton', ['$filter', function ($filter) {
        return {
            templateUrl: 'directive_tpls/service_button.html',
            replace: true,
            restrict: 'E',
            scope: {
                schedule: '=srBind',
                clickFn: '&srClick',
                altClickFn: '&srClickAlt',
                srIcon: '@srIcon'
            },
            link: function (scope, elm, attrs) {
                scope.useNeedle = attrs.srIcon === 'needle';
                scope.useDropper = attrs.srIcon === 'dropper';
                scope.schedule_id = attrs.srBind.split(".")[2];
                elm.bind('click', scope.schedule.next ? scope.clickFn : scope.altClickFn);
            }
        }
    }])
    .directive('srEcStatusButton', ['$filter', function ($filter) {
        return {
            templateUrl: 'directive_tpls/ec_status_button.html',
            replace: true,
            restrict: 'E',
            scope: {
                client: '=',
                clickFn: '&ngClick'
            },
            link: function (scope, elm, attrs) {
                scope.statusType = scope.client.status.type === 'pnc/fp' ? attrs.statusType : scope.client.status.type;
                //elm.bind('click', scope.clickFn);
            }
        }
    }])
    .directive('srLastService', ['$filter', function ($filter) {
        return {
            templateUrl: 'directive_tpls/last_service.html',
            replace: true,
            restrict: 'E',
            scope: {
                services_provided: '=bind'
            },
            link: function (scope, elm, attrs) {
                if (scope.services_provided.length > 0) {
                    scope.last_service = scope.services_provided.sort(function (a, b) {
                        return a.date < b.date ? 1 : -1;
                    })[0];
                }
            }
        }
    }]);