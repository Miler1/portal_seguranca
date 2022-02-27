(function() {
	
	angular.module('appModule').factory('sessionStorage', function($window) {

		var arquivoConfiguracao = null;
		var modulo = null;

		return {

			getObject: function(key) {

				return JSON.parse($window.sessionStorage.getItem(key));

			},
			setObject: function(key, object) {

				$window.sessionStorage.setItem(key, JSON.stringify(object));

			},
			removeObject: function(key) {

				$window.sessionStorage.removeItem(key);

			}

		};

	});

})();
	