//Navigation Controller (user session, page redirect)
app.controller("navCtrl", function($rootScope, $scope, $http) {
	$scope.LOGIN_MODE_SIGNIN = 1;
	$scope.LOGIN_MODE_CHGPASSWORD = 2;
	$scope.LOGIN_MODE_CREATE = 3;
	
	$rootScope.authSuccess = false;
	$scope.authError = null;
	$scope.createError = null;
	
	$scope.loginMode = $scope.LOGIN_MODE_SIGNIN;
	$scope.loginUsername = null;
	$scope.loginPassword = null;
	$scope.loginChgPassword = null;
	$scope.loginChgPasswordRetype = null;
	$scope.loginNewUsername = null;
	$scope.loginNewPassword = null;
	$scope.loginNewPasswordRetype = null;
	
	
	$scope.selectLoginMode = function(newMode) {
		$scope.authError = null;
		$scope.createError = null;
		$scope.loginUsername = null;
		$scope.loginPassword = null;
		$scope.loginChgPassword = null;
		$scope.loginChgPasswordRetype = null;
		$scope.loginNewUsername = null;
		$scope.loginNewPassword = null;
		$scope.loginNewPasswordRetype = null;
		
		$scope.loginMode = newMode;
	};
	
	$scope.loginSubmit = function() {
		if ($scope.loginMode == $scope.LOGIN_MODE_SIGNIN) {
			$scope.login();
		}
		else if ($scope.loginMode == $scope.LOGIN_MODE_CHGPASSWORD) {
			$scope.changePassword();
		}
	};
	
	$scope.login = function() {
		if ($scope.loginUsername == null || $scope.loginUsername.trim() == "" ||
				$scope.loginPassword == null || $scope.loginPassword.trim() == "") {
			$scope.authError = "Username and password must be non-blank. Passwords must match.";
			return;
		}

		authenticate($scope.loginUsername, $scope.loginPassword, null, function() {
			if ($rootScope.authSuccess) {
				$location.path("/");
				$scope.authError = null;
			}
			else {
				$location.path("/login");
				$scope.authError = "Error occurred while trying to authenticate";
			}
		});
	      
		$scope.authError = null;
		$scope.loginUsername = null;
		$scope.loginPassword = null;
	};
	
	$scope.changePassword = function() {
		if ($scope.loginUsername == null || $scope.loginUsername.trim() == "" ||
				$scope.loginPassword == null || $scope.loginPassword.trim() == "" ||
				$scope.loginChgPassword == null || $scope.loginChgPassword.trim() == "" ||
				$scope.loginChgPasswordRetype == null || $scope.loginChgPasswordRetype.trim() == "" ||
				($scope.loginChgPassword != $scope.loginChgPasswordRetype)) {
			$scope.authError = "Username and password must be non-blank. New passwords must match.";
			return;
		}
		// TODO
		
		$scope.authError = null;
		$scope.loginUsername = null;
		$scope.loginPassword = null;
		$scope.loginChgPassword = null;
		$scope.loginChgPasswordRetype = null;
	};
	
	var authenticate = function(username, password, newPassword, callback) {
		$rootScope.authSuccess = true;
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
		
		$scope.createError = null;
		$scope.loginNewUsername = null;
		$scope.loginNewPassword = null;
		$scope.loginNewPasswordRetype = null;
	};
	
	$scope.logout = function() {
		// TODO
		
		$rootScope.authSuccess = false;
	};
	
});

