//Navigation Controller (user session, page redirect)
app.controller("navCtrl", function($scope, $http) {
	$scope.LOGIN_MODE_SIGNIN = 1;
	$scope.LOGIN_MODE_CREATE = 2;
	
	$scope.authSuccess = false;
	$scope.authError = null;
	$scope.createError = null;
	
	$scope.loginMode = $scope.LOGIN_MODE_SIGNIN;
	$scope.loginUsername = null;
	$scope.loginPassword = null;
	$scope.loginNewUsername = null;
	$scope.loginNewPassword = null;
	$scope.loginNewPasswordRetype = null;
	
	
	$scope.selectLoginMode = function(newMode) {
		$scope.authError = null;
		$scope.createError = null;
		$scope.loginUsername = null;
		$scope.loginPassword = null;
		$scope.loginNewUsername = null;
		$scope.loginNewPassword = null;
		$scope.loginNewPasswordRetype = null;
		
		$scope.loginMode = newMode;
	};
	
	$scope.login = function() {
		if ($scope.loginUsername == null || $scope.loginUsername.trim() == "" ||
				$scope.loginPassword == null || $scope.loginPassword.trim() == "") {
			$scope.authError = "Username and password must be non-blank";
			return;
		}
		// TODO
	};
	
	$scope.createUsername = function() {
		if ($scope.loginNewUsername == null || $scope.loginNewUsername.trim() == "" ||
				$scope.loginNewPassword == null || $scope.loginNewPassword.trim() == "" ||
				$scope.loginNewPasswordRetype == null || $scope.loginNewPasswordRetype.trim() == "" ||
				$scope.loginNewPassword != $scope.loginNewPasswordRetype) {
			$scope.createError = "Username and password must be non-blank";
			return;
		}
		// TODO
	};
	
	$scope.logout = function() {
		// TODO
	};
	
});

