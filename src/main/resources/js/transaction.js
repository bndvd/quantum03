// Transaction Controller
app.controller('transactionsCtrl', function($scope, $http) {
	$scope.positionSelectedIndex = -1;
	$scope.positionSelected = [];
	$scope.transactionAddTypeOptions = ["BUY", "SEL", "DIV", "SPL", "CNV"];
	$scope.transactionAddDate = new Date();
	$scope.transactionAddType = "";
	$scope.transactionAddShares = "";
	$scope.transactionAddPrice = "";
	
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
	$scope.loadPositionForSecurityIndex = function(securityIndex) {
		$scope.positionSelectedIndex = securityIndex;
		if (securityIndex >= 0 && securityIndex < $scope.securities.length) {
			var secId = securityIndex + 1;
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
	// Add Transaction
	//
	$scope.addTransaction = function() {
		if ($scope.transactionAddDate == null || $scope.transactionAddType == "" || 
				$scope.transactionAddShares == "" || $scope.transactionAddPrice == "") {
			window.alert("Error adding transaction (invalid input): "+$scope.transactionAddDate+" "+$scope.transactionAddType+
				" "+$scope.transactionAddShares+" "+$scope.transactionAddPrice);
		}
		
		var secId = $scope.positionSelectedIndex + 1;
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
		// refresh transaction view
		$scope.loadPositionForSecurityIndex($scope.positionSelectedIndex);
	};
	
});

