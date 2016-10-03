var loginApp = angular.module("loginApp", ['ngCookies','ngMessages'])

loginApp.controller("loginController", function($scope, $http,$cookies) {

	$scope.login = function(){


		$http ({
			method : 'POST',
			//url : 'https://localhost:8080/StudentCourseRegistrationSystem/rest/students/login',
			url : 'http://localhost:8880/ServiceApplication/rest/compute/login?userName='+$scope.userName+"&password="+$scope.password

		})
		.success(function(data,status,headers,config){
			//$cookies.put('userName', data.userName);
			$scope.student = data;
			$scope.userName = data.userName;
			var now = new Date(),
			// this will set the expiration to 6 months
			exp = new Date(now.getFullYear(), now.getMonth()+6, now.getDate());

			$cookies.put('userName',data.userName,{expires:exp});
			$cookies.put('emailId', data.emailId);
			$scope.loggedIn = true;
			$cookies.put('loggedIn',true);

			$scope.loginDetails = data;
			console.log("data : "+data)

			window.location.href = "http://localhost:8880/ServiceApplication/html/Service.html";
		}).error(function(data,status,headers,config){
			alert(data);
		})
	}

	$scope.register = function() {
		window.location.href = "http://localhost:8880/ServiceApplication/html/Register.html";
	}
})