angular.module("smartRegistry.services")
    .service('SmartHelper', function ($filter) {
        var daysBetween = function (start_date, end_date) {
            return (end_date - start_date) / 1000 / 60 / 60 / 24;
        };

        var alert_status = {
            NORMAL: "normal",
            URGENT: "urgent",
            COMPLETE: "complete",
            UPCOMING: "upcoming"
        };

        return {
            daysBetween: daysBetween,
            ageFromDOB: function(dob, ref_date){
                return Math.floor(daysBetween(dob, ref_date)/365);
            },
            zeroPad: function(num, size) {
                if(size === undefined)
                    size = 2;
                var s = "00" + num;
                return s.substr(s.length-size);
            },
            childsAge: function(dob, ref_date) {
                var days_since = daysBetween(dob, ref_date);
                var DAYS_THRESHOLD = 28;
                var WEEKS_THRESHOLD = 119;
                var MONTHS_THRESHOLD = 720;
                if(days_since < DAYS_THRESHOLD)
                {
                    return Math.floor(days_since) + "d";
                }
                else if(days_since < WEEKS_THRESHOLD)
                {
                    return Math.floor(days_since/7) + "w";
                }
                else if(days_since < MONTHS_THRESHOLD)
                {
                    return Math.floor(days_since/30) + "m";
                }
                else
                {
                    return Math.floor(days_since/365) + "y";
                }
            },
            preProcessSchedule: function(client, schedule){
                var i;
                var visit = {};
                var alertsForCurrentSchedule = client.alerts.filter(function (alert) {
                    return schedule.milestones.indexOf(alert.name) > -1;
                });

                for (i = schedule.milestones.length - 1; i > -1; i--) {
                    var milestone = schedule.milestones[i];
                    var milestone_alert = alertsForCurrentSchedule.find(function (schedule_alert) {
                        return schedule_alert.name === milestone;
                    });
                    if (milestone_alert !== undefined ||
                        (milestone_alert !== undefined && visit.next !== undefined &&
                            milestone_alert.status !== alert_status.COMPLETE)) {
                        var next_milestone = {};
                        next_milestone.name = milestone_alert.name;
                        next_milestone.status = milestone_alert.status;
                        next_milestone.visit_date = $filter('date')(milestone_alert.date, 'dd/MM');
                        visit.next = next_milestone;
                        visit[next_milestone.name] = {
                            status: next_milestone.status,
                            visit_date: $filter('date')(next_milestone.visit_date, 'dd/MM')
                        };
                        // only break if status is not complete so we can keep looking for other in-complete milestones
                        if(milestone_alert.status !== alert_status.COMPLETE)
                            break;
                    }
                }

                var servicesForCurrentSchedule = client.services_provided.filter(function (service_provided) {
                    return schedule.services.indexOf(service_provided.name) !== -1;
                });
                for (i = schedule.services.length - 1; i > -1; i--) {
                    var service_name = schedule.services[i];
                    var services_provided = servicesForCurrentSchedule.filter(function (service) {
                        return service.name === service_name;
                    });

                    if (services_provided.length > 0) {
                        if(schedule.is_list)
                        {
                            var services = [];
                            services_provided.forEach(function(service_provided){
                                var service = {};
                                service.status = alert_status.COMPLETE;
                                service.visit_date = $filter('date')(service_provided.date, 'dd/MM');
                                service.data = service_provided.data;
                                services.push(service);
                            });
                            visit[service_name] = services;
                            if (visit.previous === undefined) {
                                var previous = services_provided.sort(function(a, b){
                                    if(a.date < b.date)
                                        return 1;
                                    else if(a.date > b.date)
                                        return -1;
                                    else
                                        return 0;
                                })[0];
                                visit.previous = {
                                    name: previous.name,
                                    status: alert_status.COMPLETE,
                                    visit_date: $filter('date')(previous.date, 'dd/MM'),
                                    data: previous.data
                                };
                            }
                        }
                        else
                        {
                            var service = {};
                            service.status = alert_status.COMPLETE;
                            service.visit_date = $filter('date')(services_provided[0].date, 'dd/MM');
                            service.data = services_provided[0].data;
                            visit[services_provided[0].name] = service;

                            if (visit.previous === undefined &&
                                (visit.next === undefined ||
                                    (visit.next !== undefined && visit.next.name !== services_provided[0].name))) {
                                visit.previous = services_provided[0].name;
                                visit.previous = {
                                    name: services_provided[0].name,
                                    status: alert_status.COMPLETE,
                                    visit_date: $filter('date')(services_provided[0].date, 'dd/MM'),
                                    data: services_provided[0].data
                                };
                            }
                        }
                    }
                }
                client.visits[schedule.name] = visit;
            }
        };
    });
