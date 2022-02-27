(function() {

	'use strict';

	angular
		.module('appModule')
		.service('perfilService', function($http) {

			this.listarPorFiltro = function(filtro) {

				return $http.post("perfis", filtro);

			};

			this.buscar = function(id) {

				return $http.get("perfil/" + id);

			};

		});

})();