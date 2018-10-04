// Manage Controller
app.controller('manageCtrl', function($scope, $http) {
	$scope.MANAGE_PAGE_SECURITIES = 1;
	$scope.MANAGE_PAGE_RATIOS = 2;
	$scope.MANAGE_PAGE_SETTINGS = 3;
	
	$scope.manageKeyvalMap;
	$scope.managePageIndex = 0;
	$scope.manageAssets;
	$scope.manageBasketIdToNameMap;
	$scope.manageTargetRatiosByAsset = null;
	$scope.manageSecurities;
	
	$scope.manageNewAsset = "";
	$scope.manageNewSecurity = "";
	$scope.manageNewSecurityBasketId = null;
	
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

					// read in specific values from keyval map
					$scope.propTaxRate = $scope.manageKeyvalMap["pr.tax"];
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
					$scope.manageBasketIdToNameMap = [];
					// refresh target ratios
					if ($scope.manageTargetRatiosByAsset == null) {
						$scope.manageTargetRatiosByAsset = [];
					}
					var i;
					for (i = 0; i < $scope.manageAssets.length; i++) {
						var basketId = $scope.manageAssets[i].basketId;
						var basketName = $scope.manageAssets[i].basketName;
						$scope.manageBasketIdToNameMap[basketId] = basketName;
						var basketRatio = $scope.manageKeyvalMap["pr.tr."+basketId];
						if (basketRatio == null) {
							basketRatio = 0;
						}
						if (i >= $scope.manageTargetRatiosByAsset.length) {
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
					
					// read in all the securities
					$http({
						  method: "GET",
						  url: "api/v1/securities"
						}).then(
							function successCallback(response) {
								$scope.manageSecurities = [];
								var i;
								for (i = 0; i < response.data.length; i++) {
									var secId = response.data[i].id;
									var symbol = response.data[i].symbol;
									var basketId = response.data[i].basketId;
									var basketName = $scope.manageBasketIdToNameMap[basketId];
									
									var secItem = {
										secId : secId,
										symbol : symbol,
										basketId : basketId,
										basketName : basketName
									};
									$scope.manageSecurities.push(secItem);
								}
							},
							function errorCallback(response) {
								window.alert("Error loading manage securities: "+response.status);
							}
					);
					
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
		if (pageIndex == $scope.MANAGE_PAGE_RATIOS) {
			$scope.refreshManageAssetsAndTargetRatios();
		}
		$scope.managePageIndex = pageIndex;
	};
	
	//
	// Save Asset
	//
	$scope.saveAsset = function() {
		// return if new asset is not defined or if it already exists
		if ($scope.manageNewAsset == null || $scope.manageNewAsset.trim() == "") {
			return;
		}
		$scope.manageNewAsset = $scope.manageNewAsset.trim();
		
		var i;
		for (i = 0; i < $scope.manageAssets.length; i++) {
			if ($scope.manageNewAsset == $scope.manageAssets[i].basketName) {
				window.alert("Asset Name already exists. Please enter a new Asset Name.");
				return;
			}
		}
		
		var data = {
				basketName : $scope.manageNewAsset
		};
	
		$http({
		    method: "POST",
		    url: "api/v1/asset",
		    data: data,
		    headers: {"Content-Type": "application/json"}
		}).then(
				// Success response
				function successCallback(response) {
				},
				// Error response
				function errorCallback(response) {
					window.alert("Error saving new asset: "+response.status+"; "+response.statusText);
				}
		);

		$scope.manageNewAsset = "";
		window.setTimeout($scope.refreshManageAssetsAndTargetRatios, 1000);
	};
	
	//
	// Save Security
	//
	$scope.saveSecurity = function() {
		if ($scope.manageNewSecurity == null || $scope.manageNewSecurity.trim() == "" ||
				$scope.manageNewSecurityBasketId == null) {
			return;
		}
		$scope.manageNewSecurity = $scope.manageNewSecurity.trim();
		
		var i;
		for (i = 0; i < $scope.manageSecurities.length; i++) {
			if ($scope.manageNewSecurity == $scope.manageSecurities[i].symbol) {
				window.alert("Security symbol already exists. Please enter a new Security symbol.");
				return;
			}
		}
		
		var data = {
			    symbol: $scope.manageNewSecurity,
			    basketId: $scope.manageNewSecurityBasketId
		};
		$http({
		    method: "POST",
		    url: "api/v1/security",
		    data: data,
		    headers: {"Content-Type": "application/json"}
		}).then(
				// Success response
				function successCallback(response) {
				},
				// Error response
				function errorCallback(response) {
					window.alert("Error saving new security: "+response.status+"; "+response.statusText);
				}
		);
		
		$scope.manageNewSecurity = "";
		$scope.manageNewSecurityBasketId = null;
		window.setTimeout($scope.refreshManageAssetsAndTargetRatios, 1000);
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
		window.setTimeout($scope.refreshManageAssetsAndTargetRatios, 1000);
	};
	
	//
	// Save Settings
	//
	$scope.saveSettings = function() {
		if ($scope.propTaxRate == null || $scope.propTaxRate == "" ||
				! Number.isFinite($scope.propTaxRate)) {
			return;
		}
		
		var data = {
			    key: "pr.tax",
			    value: $scope.propTaxRate
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
					window.alert("Error saving property tax rate: "+response.status+"; "+response.statusText);
				}
		);
		
		window.setTimeout($scope.refreshManageKeyvalMap, 1000);
	};
	
});
