// Manage Controller
app.controller('manageCtrl', function($scope, $http) {
	$scope.manageKeyvalMap;
	$scope.managePageIndex = 0;
	$scope.manageAssets;
	$scope.manageTargetRatiosByAsset = null;
	
	
	$scope.refreshManageKeyvalMap = function() {
		$http({
			  method: "GET",
			  url: "api/v1/keyval"
			}).then(
				function successCallback(response) {
					$scope.manageKeyvalMap = {};
					var i;
					for (i = 0; i < response.data.length; i++) {
						$scope.manageKeyvalMap[response.data[i].key] = response.data[i].value;
					}
				},
				function errorCallback(response) {
					window.alert("Error loading manage keyvals: "+response.status);
				}
		);
	};
	$scope.refreshManageKeyvalMap();
	
	$scope.refreshManageAssetsAndTargetRatios = function() {
		$http({
			  method: "GET",
			  url: "api/v1/assets"
			}).then(
				function successCallback(response) {
					$scope.manageAssets = response.data;
					// refresh target ratios
					if ($scope.manageTargetRatiosByAsset == null) {
						$scope.manageTargetRatiosByAsset = [];
					}
					var i;
					for (i = 0; i < $scope.manageAssets.length; i++) {
						var basketId = $scope.manageAssets[i].basketId;
						var basketName = $scope.manageAssets[i].basketName;
						var basketRatio = $scope.manageKeyvalMap["pr.tr."+basketId];
						if (basketRatio == null) {
							basketRatio = 0;
						}
						if (i >= $scope.manageTargetRatiosByAsset) {
							$scope.manageTargetRatiosByAsset.push({
								basketId : basketId,
								ratio : basketRatio,
								basketName : basketName
							});
						}
						else {
							$scope.manageTargetRatiosByAsset[i].basketId = basketId;
							$scope.manageTargetRatiosByAsset[i].ratio = basketRatio;
							$scope.manageTargetRatiosByAsset[i].basketName = basketName;
						}
					}
				},
				function errorCallback(response) {
					window.alert("Error loading manage assets: "+response.status);
				}
		);
	};
	$scope.refreshManageAssetsAndTargetRatios();
	
	
	//
	// Show selected Manage page
	//
	$scope.showManagePageIndex = function(pageIndex) {
		if (pageIndex == 1) {
			$scope.refreshManageAssetsAndTargetRatios();
		}
		$scope.managePageIndex = pageIndex;
	};
	
	//
	// Save Target Ratios
	//
	$scope.saveTargetRatios = function() {
		var i;
		for (i = 0; i < $scope.manageTargetRatiosByAsset.length; i++) {
			var basketId = $scope.manageTargetRatiosByAsset[i].basketId;
			var basketRatio = $scope.manageTargetRatiosByAsset[i].ratio;
			var data = {
				    key: "pr.tr."+basketId,
				    value: basketRatio
			};
			$http({
			    method: "POST",
			    url: "api/v1/keyval",
			    data: data,
			    headers: {"Content-Type": "application/json"}
			}).then(
					// Success response
					function successCallback(response) {
					},
					// Error response
					function errorCallback(response) {
						window.alert("Error saving target ratios: "+response.status+"; "+response.statusText);
					}
			);
		}
		window.setTimeout($scope.refreshManageKeyvalMap, 1000);
		$scope.refreshManageAssetsAndTargetRatios();
	};
	
	//
	// Save Settings
	//
	$scope.saveSettings = function() {
		
	};
	
});