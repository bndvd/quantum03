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
	$scope.transactionAddTypeOptions = ["BUY", "SEL", "DIV", "SPL", "CNV"];
	$scope.transactionAddDate = new Date();
	$scope.transactionAddType = "";
	$scope.transactionAddShares = "";
	$scope.transactionAddPrice = "";
	
	$http.get("api/v1/securities").then(function(response) {
		$scope.securities = response.data;
	});
	
	$scope.loadPositionForSecurityIndex = function(securityIndex) {
		$scope.positionSelectedIndex = securityIndex;
		if (securityIndex >= 0 && securityIndex < $scope.securities.length) {
			var secId = securityIndex + 1;
			$http.get("api/v1/position/"+secId).then(function(response) {
				$scope.positionSelected = response.data;
			});
		}
		else {
			$scope.positionSelected = [];
		}
	};
	
	$scope.showTransactionAddDialog = function(show) {
		if (show) {
			$scope.transactionAddDate = new Date();
			$scope.transactionAddType = "";
			$scope.transactionAddShares = "";
			$scope.transactionAddPrice = "";
			document.getElementById('modalTranAdd').style.display='block';
		}
		else {
			document.getElementById('modalTranAdd').style.display='none';
		}
	};
	
	$scope.addTransaction = function() {
		if ($scope.transactionAddDate == null || $scope.transactionAddType == "" || 
				$scope.transactionAddShares == "" || $scope.transactionAddPrice == "") {
			window.alert("Error adding transaction (invalid input): "+$scope.transactionAddDate+" "+$scope.transactionAddType+
				" "+$scope.transactionAddShares+" "+$scope.transactionAddPrice);
		}
		
		var secId = $scope.positionSelectedIndex + 1;
		var data = $.param({
	            json: JSON.stringify({
				    secId: secId,
				    userId: 1,
				    tranDate: "2018-09-27T14:30:00.000",
				    type: $scope.transactionAddType,
				    shares: $scope.transactionAddShares,
				    price: $scope.transactionAddPrice
	            })
		});
		/*var config = {
                headers : {
                    'Content-Type': 'application/json'
                }
        };*/
			
window.alert(data);

		$http.post("api/v1/transaction", data).then(
				// Success response
				function(response) {
					window.alert("Success adding transaction: "+response.status+"; "+response.statusText);
				},
				// Error response
				function(response) {
					window.alert("Error adding transaction: "+response.status+"; "+response.statusText);
				}
		);
		
		$scope.showTransactionAddDialog(false);
		// refresh transaction view
		//$scope.loadPositionForSecurityIndex($scope.positionSelectedIndex);
	};
	
});

