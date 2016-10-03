var serviceApp = angular.module('serviceApp',[])

serviceApp.directive('fileModel', ['$parse', function ($parse) {
	return {
		restrict: 'A',
		link: function(scope, element, attrs) {
			var model = $parse(attrs.fileModel);
			var modelSetter = model.assign;

			element.bind('change', function(){
				scope.$apply(function(){
					modelSetter(scope, element[0].files[0]);
				});
			});
		}
	};
}]);

serviceApp.controller("serviceAppController", function($scope,$http){
	
	$scope.getObjects = function(){
		$http({
			method : 'GET',
			url : 'http://localhost:8880/ServiceApplication/rest/compute/objects'
		}).success(function(data, status, headers, config) {
			console.log(data);
			$scope.objects = data;
			$scope.status = status;
		}).error(function(data, status, headers, config) {
			alert("Something went wrong wile getting the created server");
			$scope.status = status;
		});
	}

	
	$http({
		method : 'GET',
		url : 'http://localhost:8880/ServiceApplication/rest/compute/user'
	}).success(function(data, status, headers, config) {
		console.log(data);
		$scope.userName = data.username;
		$scope.emailId=data.email;
		$scope.status = status;
	}).error(function(data, status, headers, config) {
		alert("Something went wrong wile getting the created server");
		$scope.status = status;
	});
	
	$http({
		method : 'GET',
		url : 'http://localhost:8880/ServiceApplication/rest/compute/containers'
	}).success(function(data, status, headers, config) {
		console.log(data);
		$scope.containers = data;
		$scope.status = status;
	}).error(function(data, status, headers, config) {
		alert("Something went wrong wile getting the created server");
		$scope.status = status;
	});
	
	$http({
		method : 'GET',
		url : 'http://localhost:8880/ServiceApplication/rest/compute/servers'
	}).success(function(data, status, headers, config) {
		console.log(data);
		$scope.servers = data;
		$scope.status = status;
	}).error(function(data, status, headers, config) {
		alert("Something went wrong wile getting the created server");
		$scope.status = status;
	});

	$scope.serviceFun = function(){

		$http({
			method : 'POST',
			url : 'http://localhost:8880/ServiceApplication/rest/compute/servers?vmName='+$scope.vmName
		}).success(function(data, status, headers, config) {
			console.log(data);
			//$scope.servers = data;
			$scope.status = status;
			location.reload();
		}).error(function(data, status, headers, config) {
			alert("Something went wrong wile getting the created server");
			$scope.status = status;
		});
	}

	$scope.getObjects = function(){
		$http({
			method : 'GET',
			url : 'http://localhost:8880/ServiceApplication/rest/compute/objects'
		}).success(function(data, status, headers, config) {
			console.log(data);
			$scope.objects = data;
			$scope.status = status;
		}).error(function(data, status, headers, config) {
			alert("Something went wrong wile getting the created server");
			$scope.status = status;
		});
	}

	$scope.getContainers = function(){
		$http({
			method : 'GET',
			url : 'http://localhost:8880/ServiceApplication/rest/compute/containers'
		}).success(function(data, status, headers, config) {
			console.log(data);
			$scope.containers = data;
			$scope.status = status;
		}).error(function(data, status, headers, config) {
			alert("Something went wrong wile getting the created server");
			$scope.status = status;
		});
	}

	$scope.getContainer = function(containerName){
		$http({
			method : 'GET',
			url : 'http://localhost:8880/ServiceApplication/rest/compute/containers/'+containerName+"/objects"
		}).success(function(data, status, headers, config) {
			console.log(data);
			$scope.objects = data;
			$scope.status = status;
		}).error(function(data, status, headers, config) {
			alert("Something went wrong wile getting the created server");
			$scope.status = status;
		});
	}
	
	$scope.profile = function(){
		window.location.href = "http://localhost:8880/ServiceApplication/html/Dashboard.html";
	}
	
	$scope.uploadFile = function(){
		var file = $scope.myFile;

		console.log('file is ' );
		console.dir(file.name);

		var uploadUrl = "/ServiceApplication/rest/compute/upload?fileName="+file.name;
		//fileUpload.uploadFileToUrl(file, uploadUrl);
		var fd = new FormData();
		fd.append('file', file);

		$http.post(uploadUrl, fd, {
			transformRequest: angular.identity,
			headers: {'Content-Type': undefined}
		})

		.success(function(){
			alert("File uploaded successfully!!!");
			$scope.myFile="";
		})

		.error(function(){
		});
	};

});
