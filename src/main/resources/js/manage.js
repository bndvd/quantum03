// Manage Controller
app.controller("manageCtrl", function($scope, $http) {
	$scope.MANAGE_PAGE_SECURITIES = 1;
	$scope.MANAGE_PAGE_RATIOS = 2;
	$scope.MANAGE_PAGE_SETTINGS = 3;
	$scope.MANAGE_PAGE_BACKUP = 4;
	
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
					$scope.refreshManageAssetsAndTargetRatios();
				},
				// Error response
				function errorCallback(response) {
					window.alert("Error saving new asset: "+response.status+"; "+response.statusText);
					$scope.refreshManageAssetsAndTargetRatios();
				}
		);

		$scope.manageNewAsset = "";
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
					$scope.refreshManageAssetsAndTargetRatios();
				},
				// Error response
				function errorCallback(response) {
					window.alert("Error saving new security: "+response.status+"; "+response.statusText);
					$scope.refreshManageAssetsAndTargetRatios();
				}
		);
		
		$scope.manageNewSecurity = "";
		$scope.manageNewSecurityBasketId = null;
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
						$scope.refreshManageKeyvalMap();
						$scope.refreshManageAssetsAndTargetRatios();
					},
					// Error response
					function errorCallback(response) {
						window.alert("Error saving target ratios: "+response.status+"; "+response.statusText);
						$scope.refreshManageKeyvalMap();
						$scope.refreshManageAssetsAndTargetRatios();
					}
			);
		}
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
					$scope.refreshManageKeyvalMap();
				},
				// Error response
				function errorCallback(response) {
					window.alert("Error saving properties: "+response.status+"; "+response.statusText);
					$scope.refreshManageKeyvalMap();
				}
		);		
	};
	
	//
	// Save Settings
	//
	$scope.backupPortfolio = function() {
		$http({
			  method: "GET",
			  url: "api/v1/portfolioData"
			}).then(
				function successCallback(response) {
					var portfolioData = response.data;
					if (portfolioData != null) {
						portfolioData = $scope.preparePortfolioDataForExport(portfolioData);
						
						var portfolioDataText = JSON.stringify(portfolioData);

						var currentDate = new Date();
						var dateStr = "" + currentDate.getFullYear();
						var month = currentDate.getMonth() + 1;
						if (month < 10) {
							dateStr = dateStr + "0";
						}
						dateStr = dateStr + month;
						var day = currentDate.getDate();
						if (day < 10) {
							dateStr = dateStr + "0";
						}
						dateStr = dateStr + day;
						var filename = "qexport_" + dateStr + ".json";
						
						$scope.saveTextAsFile(portfolioDataText, filename);
					}
					else {
						window.alert("Error backing up data because returned data was null.");
					}
				},
				function errorCallback(response) {
					window.alert("Error backing up data: "+response.status);
				}
		);

	};
	

	$scope.saveTextAsFile = function(data, filename) {
		if (data == null || filename == null) {
			window.alert("Data backup: No data");
			return;
		}
	
	    var blob = new Blob([data], { type: "application/json" }),
	    e = document.createEvent("MouseEvents"),
	    a = document.createElement('a');
	    // FOR IE:

	    if (window.navigator && window.navigator.msSaveOrOpenBlob) {
	        window.navigator.msSaveOrOpenBlob(blob, filename);
	    }
	    else
	    {
	        var e = document.createEvent("MouseEvents"),
	        a = document.createElement("a");

	        a.download = filename;
	        a.href = window.URL.createObjectURL(blob);
	        a.dataset.downloadurl = ["application/json", a.download, a.href].join(":");
	        e.initEvent("click", true, false, window,
	        0, 0, 0, 0, 0, false, false, false, false, 0, null);
	        a.dispatchEvent(e);
	    }
	};
	
	
	$scope.preparePortfolioDataForExport = function(data) {
		var result = {
			    version: data.version,
			    lastDate: data.lastDate,
			    basketEntities: data.basketEntities,
			    securities: data.securities,
			    transactions: []
		};
		
		// copy over only data that's persisted in the db
		var i;
		for (i = 0; i < data.transactions.length; i++) {
			var t = data.transactions[i];
			result.transactions.push({
	            id: t.id,
	            secId: t.secId,
	            userId: t.userId,
	            tranDate: t.tranDate,
	            type: t.type,
	            shares: t.shares,
	            price: t.price
			});
		}
		
		return result;
	};
	
});

