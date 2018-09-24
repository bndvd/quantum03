// Main Quantum Angular app
var app = angular.module("qApp", ["ngRoute"]);

// Routing
app.config(function($routeProvider) {
    $routeProvider
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
    });
});


// Dashboard Controller
app.controller('dashboardCtrl', function($scope, $http) {
	$http.get("api/v1/assets").then(function(response) {
		$scope.assetsTotalPrincipal = 0.0;
		$scope.assetsTotalLastValue = 0.0;
		$scope.assets = response.data;
		for (i = 0; i < $scope.assets.length; i++) {
			$scope.assetsTotalPrincipal += $scope.assets[i].principal;
			$scope.assetsTotalLastValue += $scope.assets[i].lastValue;
		}
		$scope.assetsTotalCapGain = $scope.assetsTotalLastValue - $scope.assetsTotalPrincipal;
	});
	

});


