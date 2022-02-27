(function() {

	angular.module('appModule').factory('moduloInfo', function() {

		var modulo = null;
		var passoUm = false;

		return {

			getModulo: function() {

				return modulo;

			},
			setModulo: function(moduloInfo) {

				modulo = moduloInfo;

			},
			getPassoUm:function() {

				return passoUm;
			},
			setPassoUm: function() {

				passoUm = true;

			}

		};

	});

})();
