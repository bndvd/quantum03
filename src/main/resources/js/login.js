//Navigation Controller (user session, page redirect)
app.controller("navCtrl", function($scope, $http) {
	$scope.LOGIN_MODE_SIGNIN = 1;
	$scope.LOGIN_MODE_CREATE = 2;
	
	$scope.authSuccess = false;
	$scope.authError = null;
	
	$scope.loginMode = $scope.LOGIN_MODE_SIGNIN;
	$scope.loginUsername = null;
	$scope.loginPassword = null;
	$scope.loginNewUsername = null;
	$scope.loginNewPassword = null;
	$scope.loginNewPasswordRetype = null;
	
	
	$scope.login = function() {
		if ($scope.loginUsername == null || $scope.loginUsername.trim() == "" ||
				$scope.loginUsername == null || $scope.loginUsername.trim() == "") {
			// TODO
			return;
		}
		// TODO
	};
	
	$scope.logout = function() {
		// TODO
	};
	
});

