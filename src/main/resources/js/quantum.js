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
	$scope.assetsTotalPrincipal = 0.0;
	$scope.assetsTotalLastValue = 0.0;
	$scope.assetsTotalUnrealizedGain = 0.0;
	$scope.assetsTotalRealizedGain = 0.0;
	$scope.assetsTotalTotalPrincipal = 0.0;

	$http.get("api/v1/assets").then(function(response) {
		$scope.assets = response.data;
		for (i = 0; i < $scope.assets.length; i++) {
			$scope.assetsTotalPrincipal += $scope.assets[i].principal;
			$scope.assetsTotalLastValue += $scope.assets[i].lastValue;
			$scope.assetsTotalUnrealizedGain += $scope.assets[i].unrealizedGain;
			$scope.assetsTotalRealizedGain += $scope.assets[i].realizedGain;
			$scope.assetsTotalTotalPrincipal += $scope.assets[i].totalPrincipal;
		}
	});	
});

// Transaction Controller
app.controller('transactionsCtrl', function($scope, $http) {
	$scope.positionSelectedIndex = -1;
	$scope.positionSelected = [];
	
	$http.get("api/v1/securities").then(function(response) {
		$scope.securities = response.data;
	});
	
	$scope.loadPositionForSecurityIndex = function(index) {
		$scope.positionSelectedIndex = index;
		if (index >= 0 && index < $scope.securities.length) {
			var secId = index + 1;
			$http.get("api/v1/position/"+secId).then(function(response) {
				$scope.positionSelected = response.data;
			});
		}
		else {
			$scope.positionSelected = [];
		}
	};
	
});

