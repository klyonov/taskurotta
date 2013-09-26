var consoleControllers = angular.module("console.controllers", ['console.services']);

consoleControllers.controller("rootController", function ($rootScope, $scope, $location, $log, $window) {

    $scope.isActiveTab = function (rootPath) {
        var result = "";
        if ($location.url().indexOf(rootPath) == 0) result = "active";
        return result;
    };

    $scope.encodeURI = function (value) {
        return encodeURIComponent(value);
    };

    $scope.back = function () {
        $window.history.back();
    };
});

consoleControllers.controller("queueListController", function ($scope, $$data, $$timeUtil, $log) {

    $scope.feedback = "";

    //Init paging object
    $scope.queuesPage = {
        pageSize: 5,
        pageNumber: 1,
        totalCount: 0,
        items: []
    };

    $scope.totalTasks = function () {
        var result = 0;
        if($scope.queuesPage.items) {
            for (var i = 0; i < $scope.queuesPage.items.length; i++) {
                result = result + $scope.queuesPage.items[i].count;
            }
        }
        return result;
    };

    //Updates queues states  by polling REST resource
    $scope.update = function () {

        $$data.getQueueList($scope.queuesPage.pageNumber, $scope.queuesPage.pageSize).then(function (value) {
            $scope.queuesPage = angular.fromJson(value.data || {});
            $log.info("queueListController: successfully updated queues state: " + angular.toJson($scope.queuesPage));
        }, function (errReason) {
            $scope.feedback = angular.toJson(errReason);
            $log.error("queueListController: queue state update failed: " + $scope.feedback);
        });

    };

    //Initialization:
    $scope.update();

});

consoleControllers.controller("queueCardController", function ($scope, $$data, $$timeUtil, $log, $routeParams) {

    $scope.feedback = "";

    //Init paging object
    $scope.queueTasksPage = {
        pageSize: 5,
        pageNumber: 1,
        totalCount: 0,
        items: []
    };

    $scope.queueName = $routeParams.queueName;

    //Updates queue items by polling REST resource
    $scope.update = function () {
        $$data.getQueueContent($scope.queueName, $scope.queueTasksPage.pageNumber, $scope.queueTasksPage.pageSize).then(function (value) {
            $scope.queueTasksPage = angular.fromJson(value.data || {});
            $log.info("queueContentController: successfully updated queue content");
        }, function (errReason) {
            $scope.feedback = errReason;
            $log.error("queueContentController: queue content update failed: " + errReason);
        });

    };

    //Initialization:
    $scope.update();

});

consoleControllers.controller("processListController", function ($scope, $$data, $$timeUtil, $log) {
    //Init paging object
    $scope.processesPage = {
        pageSize: 5,
        pageNumber: 1,
        totalCount: 0,
        items: []
    };

    //Updates queues states  by polling REST resource
    $scope.update = function () {

        $$data.getProcessesList($scope.processesPage.pageNumber, $scope.processesPage.pageSize).then(function (value) {
            $scope.processesPage = angular.fromJson(value.data || {});
            $log.info("processListController: successfully updated processes list");
        }, function (errReason) {
            $scope.feedback = errReason;
            $log.error("processListController: process list update failed: " + errReason);
        });

    };

    //Initialization:
    $scope.update();
});

consoleControllers.controller("processCardController", function ($scope, $$data, $$timeUtil, $log, $routeParams) {//id=
    $scope.process = {};
    $scope.taskTree = {};
    $scope.processId = $routeParams.processId;
    $scope.feedback = "";

    $scope.update = function () {
        $$data.getProcess($routeParams.processId).then(function (value) {
            $scope.process = angular.fromJson(value.data || {});
            $log.info("processCardController: successfully updated process[" + $routeParams.processId + "] content");

            $$data.getProcessTree($routeParams.processId, $scope.process.startTaskUuid).then(function (value) {
                $scope.taskTree = angular.fromJson(value.data || {});
                $log.info("processCardController: successfully updated process[" + $routeParams.processId + "]/["+$scope.process.startTaskUuid+"] tree");
            }, function (errReason) {
                $scope.feedback = angular.toJson(errReason);
                $log.error("processCardController: process[" + $routeParams.processId + "] tree update failed: " + $scope.feedback);
            });

        }, function (errReason) {
            $scope.feedback = angular.toJson(errReason);
            $log.error("processCardController: process[" + $routeParams.processId + "] update failed: " + $scope.feedback);
        });
    };

    $scope.update();
});

consoleControllers.controller("processSearchController", function ($scope, $$data, $$timeUtil, $log, $routeParams, $location) {//params: customId, processId
    $scope.customId = $routeParams.customId || '';
    $scope.processId = $routeParams.processId || '';
    $scope.processes = [];

    $scope.update = function () {
        $$data.findProcess($scope.processId, $scope.customId).then(function (value) {
            $scope.processes = angular.fromJson(value.data || []);
            $location.search("customId", $scope.customId);
            $location.search("processId", $scope.processId);
            $log.info("processSearchController: successfully found["+$scope.processes.length+"] processes");
        }, function (errReason) {
            $scope.feedback = angular.fromJson(errReason);
            $log.error("processSearchController: process search failed: " + $scope.feedback);
        });
    };

    $scope.update();

});

consoleControllers.controller("taskListController", function ($scope, $$data, $log) {

    $scope.feedback = "";

    //Init paging object
    $scope.tasksPage = {
        pageSize: 5,
        pageNumber: 1,
        totalCount: 0,
        items: []
    };

    //Updates queues states  by polling REST resource
    $scope.update = function () {

        $$data.listTasks($scope.tasksPage.pageNumber, $scope.tasksPage.pageSize).then(function (value) {
            $scope.tasksPage = angular.fromJson(value.data || {});
            $log.info("taskListController: successfully updated tasks page");
        }, function (errReason) {
            $scope.feedback = angular.toJson(errReason);
            $log.error("queueListController: tasks page update failed: " + $scope.feedback);
        });

    };

    //Initialization:
    $scope.update();

});

consoleControllers.controller("taskCardController", function ($scope, $$data, $routeParams, $log) {
    $scope.task = {};
    $scope.taskTree = {};
    $scope.taskDecision = {};
    $scope.taskId = $routeParams.taskId;
    $scope.processId = $routeParams.processId;

    $scope.feedback = "";

    $scope.update = function () {
        $$data.getTask($routeParams.taskId, $routeParams.processId).then(function (value) {
            $scope.task = angular.fromJson(value.data || {});
            $log.info("taskController: successfully updated task[" + $routeParams.taskId + "] content");

            $$data.getTaskTree($routeParams.taskId, $routeParams.processId).then(function (value) {
                $scope.taskTree = angular.fromJson(value.data || {});
                $log.info("taskController: successfully updated task tree[" + $routeParams.taskId + "] content");

                $$data.getTaskDecision($routeParams.taskId, $routeParams.processId).then(function (value) {
                    $scope.taskDecision = angular.fromJson(value.data || {});
                    $log.info("taskController: successfully updated task decision[" + $routeParams.taskId + "] content");
                }, function (errReason) {
                    $scope.feedback = angular.toJson(errReason);
                    $log.error("taskController: task[" + $routeParams.taskId + "] tree update failed: " + $scope.feedback);
                });

            }, function (errReason) {
                $scope.feedback = angular.toJson(errReason);
                $log.error("taskController: task[" + $routeParams.taskId + "] tree update failed: " + $scope.feedback);
            });

        }, function (errReason) {
            $scope.feedback = angular.toJson(errReason);
            $log.error("taskController: task[" + $routeParams.taskId + "] update failed: " + $scope.feedback);
        });
    };

    $scope.update();

});

consoleControllers.controller("taskSearchController", function ($scope, $routeParams, $$data, $log, $location) {
    $scope.taskId = $routeParams.taskId || '';
    $scope.processId = $routeParams.processId || '';
    $scope.tasks = [];

    $scope.update = function () {
        if($scope.taskId || $scope.processId) {
            $$data.findTasks($scope.processId, $scope.taskId).then(function (value) {
                $scope.tasks = angular.fromJson(value.data || []);
                $location.search("processId", $scope.processId);
                $location.search("taskId", $scope.taskId);
                $log.info("taskSearchController: found [" + $scope.tasks.length + "] tasks");
            }, function (errReason) {
                $scope.feedback = angular.toJson(errReason);
                $log.error("taskSearchController: task search update failed: " + $scope.feedback);
            });
        }
    };

    $scope.update();

});

consoleControllers.controller("hoveringQueuesController", function ($scope, $$data, $$timeUtil, $log) {

    $scope.feedback = "";

    //Init paging object
    $scope.queues = [];

    $scope.periodSize = 2;

    $scope.totalTasks = function () {
        var result = 0;
        for (var i = 0; i < $scope.queues.length; i++) {
            result = result + $scope.queues[i].count;
        }
        return result;
    };

    //Updates queues states  by polling REST resource
    $scope.update = function () {

        $$data.getHoveringQueues($scope.periodSize).then(function (value) {
            $scope.queues = angular.fromJson(value.data || {});
            $log.info("queueListController: successfully updated queue state");
        }, function (errReason) {
            $scope.feedback = errReason;
            $log.error("queueListController: queue state update failed: " + errReason);
        });

    };

    //Initialization:
    $scope.update();

});

consoleControllers.controller("repeatedTasksController", function ($scope, $routeParams, $$data, $log) {
    $scope.iterationCount = 5;
    $scope.tasks = [];

    $scope.update = function () {
        $$data.getRepeatedTasks($scope.iterationCount).then(function (value) {
            $scope.tasks = angular.fromJson(value.data || {});
            $log.info("repeatedTasksController: tasks loaded successfully");
        }, function (errReason) {
            $scope.feedback = errReason;
            $log.error("repeatedTasksController: load repeated tasks failed: " + errReason);
        });
    };

    $scope.update();

});

consoleControllers.controller("metricsController", function ($scope, $$data, $log, $location) {
    $scope.collapse = {
        filter: false,
        plot: false
    };
    $scope.actorIds = [];
    $scope.metricsOptions = {};

    //selected objects
    $scope.selection = {
        showDatasets: false,
        omitZeroes: false,
        datasets: {},
        metric: {},
        scopeMode: {},
        dataMode: {},
        periodMode: {}
    };

    $scope.getSelectedDataSets = function() {
        var result = "";
        for(var ds in $scope.selection.datasets) {
            if($scope.selection.datasets[ds]) {
                if(result.length > 0) {
                    result = result + ",";
                }
                result = result + ds;
            }
        }
        return result;
    };


    $scope.getDatasetList = function() {
        if($scope.metricsOptions.dataSetDesc && $scope.selection.metric.value) {
            return $scope.metricsOptions.dataSetDesc[$scope.selection.metric.value];
        }
    };

    $scope.getDataSetUrl = function() {
        var dataset = $scope.getSelectedDataSets();
        var type = $scope.selection.dataMode.value;
        var scope = $scope.selection.scopeMode.value;
        var period = $scope.selection.periodMode.value;
        var metric = $scope.selection.metric.value;
        var action = "data";
        var zeroes = !$scope.selection.omitZeroes;

        if($scope.selection.actorSpecific) {
            action = "actorData";
        }

        if (!!dataset && !!type && !!scope && !!period && !!metric) {//url contains some defined values
            return "/rest/console/metrics/"+action+"/?zeroes="+zeroes+"&metric=" + metric + "&period=" + period + "&scope=" + scope + "&type=" + type + "&dataset=" + encodeURIComponent(dataset);
        }
        return "";
    };

    $$data.listActors().then(function(value) {
        $scope.actorIds = angular.fromJson(value.data || {});
        $scope.selectedActorIds = new Array($scope.actorIds.length);
        $log.info("metricsController: actors list size getted is: " + $scope.actorIds.length);
    });

    $$data.getMetricsOptions().then(function(value) {
        $scope.metricsOptions = angular.fromJson(value.data || {});
        $log.info("metricsController: metricsOptions getted are: " + angular.toJson(value.data));


        //Select first available values by default
        if($scope.metricsOptions.scopes && $scope.metricsOptions.scopes.length>0) {
            $scope.selection.scopeMode = $scope.metricsOptions.scopes[0];
        }
        if($scope.metricsOptions.periods && $scope.metricsOptions.periods.length>0) {
            $scope.selection.periodMode = $scope.metricsOptions.periods[0];
        }
        if($scope.metricsOptions.dataTypes && $scope.metricsOptions.dataTypes.length>0) {
            $scope.selection.dataMode = $scope.metricsOptions.dataTypes[0];
        }
        if($scope.metricsOptions.generalMetrics && $scope.metricsOptions.generalMetrics.length>0) {
            $scope.selection.metric = $scope.metricsOptions.generalMetrics[0];
        }


    });


});

consoleControllers.controller("homeController", function ($scope) {
});

consoleControllers.controller("actorListController", function ($scope, $$data, $timeout) {
});
consoleControllers.controller("actorsController", function ($scope, $$data, $timeout) {
});
consoleControllers.controller("aboutController", function ($scope) {
});


