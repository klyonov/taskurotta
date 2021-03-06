angular.module('indexApp', ['coreApp','homeModule', 'queueModule',
    'taskModule','processModule','scheduleModule','interruptedModule','metricModule', 'notificationsModule',
    'documentationModule'])

    .config(function ($stateProvider, $urlRouterProvider, coreAppProvider) {
        console.log('indexApp.config');

        coreAppProvider.setRestUrl('/rest/console/');
        coreAppProvider.setPageSizes([5, 10, 30, 50, 100, 200]);

        //Dialog configs
        coreAppProvider.setDialogConfirmConfig({
            templateUrl: '/views/core/dialog-confirm.html',
            windowClass: 'approve',
            controller: 'ModalDialogController'
        });

        coreAppProvider.setDialogStacktraceConfig({
            templateUrl: '/views/core/dialog-stacktrace.html',
            windowClass: 'stack-trace',
            controller: 'ModalDialogController'
        });

        coreAppProvider.setDialogPropertiesConfig({
            templateUrl: '/views/core/dialog-properties.html',
            windowClass: 'bigModalDialog',
            controller: 'ModalDialogController'
        });

        //Home page configs
        $stateProvider.state('home', {
            url: '/home',
            views: {
                'navigation': {
                    templateUrl: '/views/navigation.html'
                },
                '': {
                    templateUrl: '/views/home/home.html',
                    controller: /*@ngInject*/ 'homeController'
                },
                'footer': {
                    templateUrl: '/views/footer.html'
                }
            }
        });

        //Queues pages configs
        $stateProvider.state('queues', {
            url: '/queues?{filter}&{balancePeriod}&{pageNum:int}&{pageSize:int}&{refreshRate:int}', // root route
            params: {balancePeriod: 'hour', pageNum: 1, pageSize: 10, refreshRate: 0},
            views: {
                'navigation': {
                    templateUrl: '/views/navigation.html'
                },
                '': {
                    templateUrl: '/views/queue/list.html',
                    controller: /*@ngInject*/ 'queueListController'
                },
                'footer': {
                    templateUrl: '/views/footer.html'
                }
        }
        });

        //Actors pages configs
        $stateProvider.state('actors', {
            url: '/actors?{filter}&{pageNum:int}&{pageSize:int}&{refreshRate:int}&{metrics}', // root route
            params: {pageNum: 1, pageSize: 10, refreshRate: 0},
            views: {
                'navigation': {
                    templateUrl: '/views/navigation.html'
                },
                '': {
                    templateUrl: '/views/actor/list.html',
                    controller: /*@ngInject*/ 'actorListController'
                },
                'footer': {
                    templateUrl: '/views/footer.html'
                }
            }
        });

        //Actors pages configs
        $stateProvider.state('actor', {
            url: '/actor?{actorId}&{refreshRate:int}', // root route
            params: {},
            views: {
                'navigation': {
                    templateUrl: '/views/navigation.html'
                },
                '': {
                    templateUrl: '/views/actor/card.html',
                    controller: /*@ngInject*/ 'actorController'
                },
                'footer': {
                    templateUrl: '/views/footer.html'
                }
            }
        });

        //Tasks pages configs
        $stateProvider.state('tasks', {
            url: '/tasks?{taskId}&{processId}&{iterationCount:int}&{pageNum:int}&{pageSize:int}&{refreshRate:int}',
            params: {pageNum: 1, pageSize: 10, refreshRate: 0, iterationCount:0},
            views: {
                'navigation': {
                    templateUrl: '/views/navigation.html'
                },
                '': {
                    templateUrl: '/views/task/list.html',
                    controller: /*@ngInject*/ 'taskListController'
                },
                'footer': {
                    templateUrl: '/views/footer.html'
                }
            }
        });

        $stateProvider.state('task', {
            url: '/tasks/:taskId/card?{processId}',
            params: {},
            views: {
                'navigation': {
                    templateUrl: '/views/navigation.html'
                },
                '': {
                    templateUrl: '/views/task/card.html',
                    controller: /*@ngInject*/ 'taskCardController'
                },
                'footer': {
                    templateUrl: '/views/footer.html'
                }
            }
        });

        var interruptedParams = {
            group: 'starter', refreshRate: 0,
            dateFrom: moment().subtract(1, 'days').format('YYYY-MM-DD'),
            dateTo: moment().format('YYYY-MM-DD'),
            withTime: false
        };

        //Interrupted tasks pages configs
        $stateProvider.state('interrupted', {
            url: '/interrupted?{group}&{starterId}&{actorId}&{exception}&{dateFrom}&{dateTo}&{withTime:bool}&{refreshRate:int}',
            params: interruptedParams,
            views: {
                'navigation': {
                    templateUrl: '/views/navigation.html'
                },
                '': {
                    templateUrl: '/views/interrupted/group.html',
                    controller: /*@ngInject*/ 'interruptedController'
                },
                'footer': {
                    templateUrl: '/views/footer.html'
                }
            }
        });

        $stateProvider.state('interrupted_list', {
            url: '/interrupted/list?{group}&{starterId}&{actorId}&{exception}&{dateFrom}&{dateTo}&{withTime:bool}&{refreshRate:int}',
            params: interruptedParams,
            views: {
                'navigation': {
                    templateUrl: '/views/navigation.html'
                },
                '': {
                    templateUrl: '/views/interrupted/list.html',
                    controller: /*@ngInject*/ 'interruptedController'
                },
                'footer': {
                    templateUrl: '/views/footer.html'
                }
            }
        });

        //Processes pages configs
        $stateProvider.state('processes', {
            url: '/processes?{processId}&{customId}&{status}&{type}&{pageNum:int}&{pageSize:int}&{refreshRate:int}',
            params: {pageNum: 1, pageSize: 10, refreshRate: 0},
            views: {
                'navigation': {
                    templateUrl: '/views/navigation.html'
                },
                '': {
                    templateUrl: '/views/process/list.html',
                    controller: /*@ngInject*/ 'processListController'
                },
                'footer': {
                    templateUrl: '/views/footer.html'
                }
            }
        });

        $stateProvider.state('process', {
            url: '/processes/:processId/card',
            params: {},
            views: {
                'navigation': {
                    templateUrl: '/views/navigation.html'
                },
                '': {
                    templateUrl: '/views/process/card.html',
                    controller: /*@ngInject*/ 'processCardController'
                },
                'footer': {
                    templateUrl: '/views/footer.html'
                }
            }
        });

        $stateProvider.state('process_create', {
            url: '/processes/create',
            params: {},
            views: {
                'navigation': {
                    templateUrl: '/views/navigation.html'
                },
                '': {
                    templateUrl: '/views/process/create.html',
                    controller: /*@ngInject*/ 'processCreateController'
                },
                'footer': {
                    templateUrl: '/views/footer.html'
                }
            }
        });

        //Schedules pages configs
        $stateProvider.state('schedules', {
            url: '/schedules?{pageNum:int}&{pageSize:int}&{refreshRate:int}',
            params: {pageNum: 1, pageSize: 10, refreshRate: 0},
            views: {
                'navigation': {
                    templateUrl: '/views/navigation.html'
                },
                '': {
                    templateUrl: '/views/schedule/list.html',
                    controller: /*@ngInject*/ 'scheduleListController'
                },
                'footer': {
                    templateUrl: '/views/footer.html'
                }
            }
        });

        $stateProvider.state('schedule', {
            url: '/schedules/:id/card',
            params: {},
            views: {
                'navigation': {
                    templateUrl: '/views/navigation.html'
                },
                '': {
                    templateUrl: '/views/schedule/card.html',
                    controller: /*@ngInject*/ 'scheduleCardController'
                },
                'footer': {
                    templateUrl: '/views/footer.html'
                }
            }
        });

        $stateProvider.state('schedule_create', {
            url: '/schedule/create',
            params: {},
            views: {
                'navigation': {
                    templateUrl: '/views/navigation.html'
                },
                '': {
                    templateUrl: '/views/schedule/card.html',
                    controller: /*@ngInject*/ 'scheduleCardController'
                },
                'footer': {
                    templateUrl: '/views/footer.html'
                }
            }
        });

        //Metrics
        $stateProvider.state('metrics', {
            url: '/metrics?{metric}&{scope}&{type}&{period}&{zeroes:bool}&{smooth:int}&' +
            '{refreshRate:int}&{dataset}&{showDataset:bool}&{zoom:bool}&{pan:bool}&{legend}&{points:bool}&{xaxis:num}&{yaxis:num}',
            params: { refreshRate: 0, legend: 'nw', points: true },
            resolve: {
                smoothRates: function () {
                    return [0, 3, 7, 20, 30];
                },
                legendPositions : function() {
                    return {ne:'northeast',nw:'northwest', se: 'southeast', sw : 'southwest' };
                }
            },
            views: {
                'navigation': {
                    templateUrl: '/views/navigation.html'
                },
                '': {
                    templateUrl: '/views/metric/list.html',
                    controller: /*@ngInject*/ 'metricListController'
                },
                'footer': {
                    templateUrl: '/views/footer.html'
                }
            }
        });


        //Notifications
        $stateProvider.state('subscriptions', {
            url: '/subscriptions?{pageNum:int}&{pageSize:int}&{refreshRate:int}',
            params: {},
            views: {
                'navigation': {
                    templateUrl: '/views/navigation.html'
                },
                '': {
                    templateUrl: '/views/notifications/subscriptions.html',
                    controller: /*@ngInject*/ 'subscriptionsListController'
                },
                'footer': {
                    templateUrl: '/views/footer.html'
                }
            }
        });

        $stateProvider.state('subscription', {
            url: '/subscriptions/:id/card',
            params: {},
            views: {
                'navigation': {
                    templateUrl: '/views/navigation.html'
                },
                '': {
                    templateUrl: '/views/notifications/subscription.html',
                    controller: /*@ngInject*/ 'subscriptionCardController'
                },
                'footer': {
                    templateUrl: '/views/footer.html'
                }
            }
        });

        $stateProvider.state('subscription_create', {
            url: '/subscription/create',
            params: {},
            views: {
                'navigation': {
                    templateUrl: '/views/navigation.html'
                },
                '': {
                    templateUrl: '/views/notifications/subscription.html',
                    controller: /*@ngInject*/ 'subscriptionCardController'
                },
                'footer': {
                    templateUrl: '/views/footer.html'
                }
            }
        });

        $stateProvider.state('documentation', {
            url: '/documentation?{anchor}',
            params: {},
            views: {
                'navigation': {
                    templateUrl: '/views/navigation.html'
                },
                '': {
                    templateUrl: '/views/documentation/documentation.html',
                    controller: /*@ngInject*/ 'documentationController'
                },
                'footer': {
                    templateUrl: '/views/footer.html'
                }
            }
        });

        $urlRouterProvider.otherwise('/home');
    });

if (!String.prototype.trim) {
    (function() {
        // Make sure we trim BOM and NBSP
        var rtrim = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g;
        String.prototype.trim = function() {
            return this.replace(rtrim, '');
        };
    })();
}