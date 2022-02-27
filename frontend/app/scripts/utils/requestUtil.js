(function(){

	var modulo = angular.module('appModule');

	modulo.service('requestUtil', function($http, $rootScope) {

		var service = {};

		var count = 0;
		var elementCount = {};

		var showLoading = function(element) {
			app.LoadingUtil.show();
		};

		var hideLoading = function(element) {
			app.LoadingUtil.hide();
		};

		service.send = function(configObject, success, error, semLoading, element) {

			if(!semLoading)
				showLoading(element);

			return $http(configObject)
				.then(function(data){

					if(!semLoading)
						hideLoading(element);

					if(success) {

						success.call(null,data.data);

					}

				})
				.catch(function(data){

					if(!semLoading)
						hideLoading(element);

					if(!error) {

						$rootScope.$broadcast('showMessageEvent', data, 'danger');

					} else {

						error.call(null, data.data);

					}

				});

		};

		service.get = function(url, success, error, semLoading, element) {

			if(!semLoading)
				showLoading(element);

			return $http.get(url)
				.then(function(data){

					if(!semLoading)
						hideLoading(element);

					if(success) {
						success.call(null, data.data);
					}

				})
				.catch(function(data){

					if(!semLoading)
						hideLoading(element);

					if(!error) {

						$rootScope.$broadcast('showMessageEvent', data, 'danger');

					} else {

						error.call(null, data.data);

					}

				});

		};

		service.post = function(url, data, success, error, config, semLoading, element) {

			if(!semLoading)
				showLoading(element);

			return $http.post(url, data, config)
				.then(function(data){

					if(!semLoading)
						hideLoading(element);

					if(success) {

						success.call(null,data.data);
					}

				})
				.catch(function(data) {

					if(!semLoading)
						hideLoading(element);

					if(!error) {

						$rootScope.$broadcast('showMessageEvent', data, 'danger');

					} else {

						error.call(null, data.data);

					}

				});

		};

		service.delete = function(url, success, error, semLoading, element) {

			if(!semLoading)
				showLoading(element);

			return $http.delete(url)
				.then(function(data){

					if(!semLoading)
						hideLoading(element);

					if(success)
						success.call(null,data.data);

				})
				.catch(function(data) {

					if(!semLoading)
						hideLoading(element);

					if(!error) {

						$rootScope.$broadcast('showMessageEvent', data, 'danger');

					} else {

						error.call(null, data.data);

					}

				});

		};

		service.put = function(url, data, success, error, config, semLoading, element) {

			if(!semLoading)
				showLoading(element);

			return $http.put(url, data, config)
				.then(function(data){

					if(!semLoading)
						hideLoading(element);

					if(success) {

						success.call(null,data.data);
					}

				})
				.catch(function(data) {

					if(!semLoading)
						hideLoading(element);

					if(!error) {

						$rootScope.$broadcast('showMessageEvent', data, 'danger');

					} else {

						error.call(null, data.data);

					}

				});

		};

		return service;

	});

})();