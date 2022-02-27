(function() {

	'use strict';

	angular
		.module('appModule')
		.service('setorService', function($http) {

			this.listarPorFiltro = function(filtro) {

				return $http.post("setores", filtro);

			};

			this.buscar = function(id) {

				return $http.get("setor/" + id);

			};

			this.findTiposSetores = function() {

				return $http.get("setor/tiposSetores");

			};

			this.findSetoresPai = function() {

				return $http.get("setores");

			};

			this.save = function(setor) {

				return $http.post("setor", setor);

			};

			this.excluir = function(id) {

				return $http.delete("setor/" + id);

			};

			this.update = function(setor) {

				return $http.put("setor", setor);

			};

			this.ativarSetor = function(idSetor) {

				var parameter = 'id=' + idSetor;

				return $http({
					url: 'setor/ativar',
					method: "PUT",
					data: parameter,
					headers: {
						'Content-Type': 'application/x-www-form-urlencoded'
					}
				});

			};

			this.desativarSetor = function(idSetor) {

				var parameter = 'id=' + idSetor;

				return $http({
					url: 'setor/desativar',
					method: "PUT",
					data: parameter,
					headers: {
						'Content-Type': 'application/x-www-form-urlencoded'
					}
				});

			};

			this.buscaTodoSetoresAtivos = function() {

				return $http.get("setoresAtivos");

			};

		});

})();
