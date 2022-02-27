(function() {

	angular.module('appModule').factory('httpInterceptor', function($q, $window, config, $rootScope) {

		var HTTPStatus = {
			REDIRECT: '312',
			UNAUTHORIZED: '401',
			FORBIDDEN: '403'
		};

		var count = 0;
		var elementCount = {};

		var showLoading = function() {

			if(count === 0)
				app.LoadingUtil.show();

			count++;

		};

		var hideLoading = function() {

			count--;

			if(count === 0)
				app.LoadingUtil.hide();

		};

		return {

			'request': function(config) {

				showLoading();

				return config;

			},

			'requestError': function(rejection) {

				// do something on error
				return $q.reject(rejection);

			},

			'response': function(response) {

				hideLoading();

				return response;

			},

			'responseError': function(rejection) {

				hideLoading();

				if(rejection.status == HTTPStatus.REDIRECT) {

					location.href = rejection.headers("location");

					return rejection;

				} else if (rejection.status == HTTPStatus.UNAUTHORIZED) {

					$rootScope.setErrorMessage($rootScope.Mensagens.SESSION_TIMEOUT);

					location.href = config.LOGIN_REDIRECT_URL;

					return rejection;

				}

				return $q.reject(rejection);

			}

		};

	});

})();
