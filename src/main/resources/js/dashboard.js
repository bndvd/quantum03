// Dashboard Controller
app.controller('dashboardCtrl', function($scope, $http) {
	$scope.assetsTotalPrincipal = 0.0;
	$scope.assetsTotalLastValue = 0.0;
	$scope.assetsTotalUnrealizedGain = 0.0;
	$scope.assetsTotalRealizedGain = 0.0;
	$scope.assetsTotalRealizedGainYtd = 0.0;
	$scope.assetsTotalRealizedGainYtdTax = 0.0;
	$scope.assetsTotalTotalPrincipal = 0.0;

	$http({
		  method: "GET",
		  url: "api/v1/assets"
		}).then(
			function successCallback(response) {
				$scope.assets = response.data;
				for (i = 0; i < $scope.assets.length; i++) {
					$scope.assetsTotalPrincipal += $scope.assets[i].principal;
					$scope.assetsTotalLastValue += $scope.assets[i].lastValue;
					$scope.assetsTotalUnrealizedGain += $scope.assets[i].unrealizedGain;
					$scope.assetsTotalRealizedGain += $scope.assets[i].realizedGain;
					$scope.assetsTotalRealizedGainYtd += $scope.assets[i].realizedGainYtd;
					$scope.assetsTotalRealizedGainYtdTax += $scope.assets[i].realizedGainYtdTax;
					$scope.assetsTotalTotalPrincipal += $scope.assets[i].totalPrincipal;
				}
			},
			function errorCallback(response) {
				window.alert("Error loading assets: "+response.status);
			}
	);

});

