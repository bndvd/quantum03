// Transaction Controller
app.controller('transactionsCtrl', function($scope, $http) {
	$scope.positionSelectedPageIndex = -1;
	$scope.positionSelected = [];
	$scope.transactionAddTypeOptions = ["BUY", "SEL", "DIV", "SPL", "CNV"];
	$scope.transactionAddDate = new Date();
	$scope.transactionAddType = "";
	$scope.transactionAddShares = "";
	$scope.transactionAddPrice = "";
	$scope.transactionDeleteTran = null;
	$scope.transactionUpdateTran = null;
	$scope.transactionUpdateNewPrice = null;
	
	$http({
		  method: "GET",
		  url: "api/v1/securities"
		}).then(
			function successCallback(response) {
				$scope.securities = response.data;
			},
			function errorCallback(response) {
				window.alert("Error loading securities: "+response.status);
			}
	);
	
	//
	// Load the transactions for a given Position
	//
	$scope.loadPositionForPageIndex = function(pageIndex) {
		$scope.positionSelectedPageIndex = pageIndex;
		if (pageIndex >= 0 && pageIndex < $scope.securities.length) {
			var secId = $scope.securities[pageIndex].id;
			$http({
				  method: "GET",
				  url: "api/v1/position/"+secId
				}).then(
					function successCallback(response) {
						$scope.positionSelected = response.data;
					},
					function errorCallback(response) {
						window.alert("Error loading position transactions: "+response.status);
					}
			);

		}
		else {
			$scope.positionSelected = [];
		}
	};
	
	//
	// reload currently selected Position
	//
	$scope.reloadPosition = function() {
		$scope.loadPositionForPageIndex($scope.positionSelectedPageIndex);
	};
	
	//
	// Show Add Transaction dialog
	//
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
	
	//
	// Show Delete Transaction dialog
	//
	$scope.showTransactionDeleteDialog = function(show) {
		if (show) {
			$scope.transactionDeleteTran = null;
			document.getElementById('modalTranDelete').style.display='block';
		}
		else {
			document.getElementById('modalTranDelete').style.display='none';
			$scope.transactionDeleteTran = null;
		}
	};
	
	//
	// Show Update Transaction dialog
	//
	$scope.showTransactionUpdateDialog = function(show) {
		if (show) {
			$scope.transactionUpdateTran = null;
			$scope.transactionUpdateNewPrice = null;
			document.getElementById('modalTranUpdate').style.display='block';
		}
		else {
			document.getElementById('modalTranUpdate').style.display='none';
			$scope.transactionUpdateTran = null;
			$scope.transactionUpdateNewPrice = null;
		}
	};
	
	$scope.processTransactionUpdateTranSelection = function() {
		$scope.transactionUpdateNewPrice = $scope.transactionUpdateTran.price;
	};
	
	//
	// Add Transaction
	//
	$scope.addTransaction = function() {
		if ($scope.transactionAddDate == null || $scope.transactionAddType == "" || 
				$scope.transactionAddShares == "" || $scope.transactionAddPrice == "" ||
				$scope.positionSelectedPageIndex < 0 || $scope.positionSelectedPageIndex >= $scope.securities.length) {
			window.alert("Error adding transaction (invalid input): "+$scope.transactionAddDate+" "+$scope.transactionAddType+
				" "+$scope.transactionAddShares+" "+$scope.transactionAddPrice);
		}
		
		var secId = $scope.securities[$scope.positionSelectedPageIndex].id;
		var year = $scope.transactionAddDate.getFullYear();
		var month = $scope.transactionAddDate.getMonth() + 1;
		var monthStr = "" + month;
		if (month < 10) {
			monthStr = "0" + month;
		}
		var day = $scope.transactionAddDate.getDate();
		var dayStr = "" + day;
		if (day < 10) {
			dayStr = "0" + day;
		}
		var dateStr = year + "-" + monthStr + "-" + dayStr + "T14:30:00.000";
		
		var data = {
				    secId: secId,
				    userId: 1,
				    tranDate: dateStr,
				    type: $scope.transactionAddType,
				    shares: $scope.transactionAddShares,
				    price: $scope.transactionAddPrice
		};

		$http({
		    method: "POST",
		    url: "api/v1/transaction",
		    data: data,
		    headers: {"Content-Type": "application/json"}
		}).then(
				// Success response
				function successCallback(response) {
				},
				// Error response
				function errorCallback(response) {
					window.alert("Error adding transaction: "+response.status+"; "+response.statusText);
				}
		);

		
		$scope.showTransactionAddDialog(false);
		// refresh transaction view after sleeping for 1 sec to allow post to happen
		window.setTimeout($scope.reloadPosition, 1000);
	};
	
	
	//
	// Delete Transaction
	//
	$scope.deleteTransaction = function() {
		if ($scope.transactionDeleteTran == null) {
			return;
		}
		$http({
			  method: "DELETE",
			  url: "api/v1/transaction/" + $scope.transactionDeleteTran.id
			}).then(
				function successCallback(response) {
				},
				function errorCallback(response) {
					window.alert("Error deleting transaction: "+response.status);
				}
		);
		$scope.transactionDeleteTran = null;
		
		$scope.showTransactionDeleteDialog(false);
		// refresh transaction view after sleeping for 1 sec to allow post to happen
		window.setTimeout($scope.reloadPosition, 1000);
	};
	
	//
	// Update Transaction
	//
	$scope.updateTransaction = function() {
		if ($scope.transactionUpdateTran == null) {
			return;
		}
		
		// TODO
		
		$scope.showTransactionUpdateDialog(false);
		// refresh transaction view after sleeping for 1 sec to allow post to happen
		window.setTimeout($scope.reloadPosition, 1000);
	};
		
});

