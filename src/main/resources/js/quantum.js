// Main Quantum Angular app
var app = angular.module("qApp", ["ngRoute"]);

// Routing
app.config(function($routeProvider, $httpProvider) {
    $routeProvider
    .when("/", {
        templateUrl : "login.html",
        controller : "navCtrl"
    })
    .when("/login", {
        templateUrl : "login.html",
        controller : "navCtrl"
    })
    .when("/dashboard", {
        templateUrl : "dashboard.html",
        controller : "dashboardCtrl"
    })
    .when("/transactions", {
        templateUrl : "transactions.html",
        controller : "transactionsCtrl"
    })
    .when("/comp", {
        templateUrl : "comp.html",
        controller : "compCtrl"
    })
    .when("/manage", {
        templateUrl : "manage.html",
        controller : "manageCtrl"
    })
    .otherwise("/");
    
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
    
});

