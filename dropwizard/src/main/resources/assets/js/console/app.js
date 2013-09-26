var consoleApp = angular.module("consoleApp", ['console.services', 'console.controllers', 'console.animation', 'console.directives', 'ui.bootstrap']);

consoleApp.config(function ($routeProvider, $locationProvider, $httpProvider) {

    //show loader on every ajax call
    $httpProvider.defaults.transformRequest.push(function (data, headersGetter) {
        $('#loading').show();
        return data;
    });

    //hide loader on every ajax response
    $httpProvider.defaults.transformResponse.push(function (data, headersGetter) {
        $('#loading').hide();
        return data;
    });


    $routeProvider.when('/home', {
        templateUrl: '/partials/view/home.html',
        controller: "homeController"
    });

    //QUEUES
    $routeProvider.when('/queues', {
        templateUrl: '/partials/view/list/queue_list.html',
        controller: "queueListController"
    });
    $routeProvider.when('/queues/queue/:queueName', {
        templateUrl: '/partials/view/card/queue_card.html',
        controller: "queueCardController"
    });

    //PROCESSES/TASKS
    //list
    $routeProvider.when('/processes/list', {
        templateUrl: '/partials/view/list/process_list.html',
        controller: "processListController"
    });
    $routeProvider.when('/processes/tasks', {
        templateUrl: '/partials/view/list/task_list.html',
        controller: "taskListController"
    });
    //card
    $routeProvider.when('/processes/process', {
        templateUrl: '/partials/view/card/process_card.html',
        controller: "processCardController"
    });
    $routeProvider.when('/processes/task', {
        templateUrl: '/partials/view/card/task_card.html',
        controller: "taskCardController"
    });
    //search
    $routeProvider.when('/processes/search/task', {
        templateUrl: '/partials/view/search/task_search.html',
        controller: "taskSearchController"
    });
    $routeProvider.when('/processes/search/process', {
        templateUrl: '/partials/view/search/process_search.html',
        controller: "processSearchController"
    });
    //create
    $routeProvider.when('/processes/create', {
        templateUrl: '/partials/view/create/process_create.html'
    });

    //MONITORING
    $routeProvider.when('/monitoring/metrics', {
        templateUrl: '/partials/view/card/metrics_card.html',
        controller: "metricsController"
    });

    $routeProvider.when('/monitoring/hoveringQueues', {
        templateUrl: '/partials/view/list/hovering_queues_list.html',
        controller: "hoveringQueuesController"
    });

    $routeProvider.when('/monitoring/repeatedTasks', {
        templateUrl: '/partials/view/list/repeated_tasks_list.html',
        controller: "repeatedTasksController"
    });

    //ACTORS
    $routeProvider.when('/actors/list', {
        templateUrl: '/partials/view/list/actors_list.html',
        controller: "actorListController"
    });
    $routeProvider.when('/actors/actor', {
        templateUrl: '/partials/view/list/actors_list.html',
        controller: "actorListController"
    });

    //DEFAULT
    $routeProvider.otherwise({
        redirectTo: '/home'
    });

    // configure html5 to get links working on jsfiddle
    //TODO: causes troubles on page refresh
//    $locationProvider.html5Mode(true);

});

consoleApp.run(function ($rootScope, $log) {
    $rootScope.getStartIndex = function (pageNum, pageSize) {
        return (pageNum - 1) * pageSize + 1;
    };
});