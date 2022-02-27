(function() {

	'use strict';

	angular
		.module('appModule')
		.service('moduloService', function($http) {

			this.listarPorFiltro = function(filtro) {

				return $http.post("modulos", filtro);

			};

			this.validarArquivo = function(code) {

				var parameter = 'fileCode=' + code;

				return $http({
					url: 'modulos/validate',
					method: "POST",
					data: parameter,
					headers: {
						'Content-Type': 'application/x-www-form-urlencoded'
					}
				});

			};

			this.salvarModulo = function(modulo) {

				return $http.post("modulo", modulo);

			};

			this.getPerfis = function(idModulo) {

				return $http.get('modulos/' + idModulo + '/usuarioLogado/perfis');

			};

			this.getSetores = function(idModulo) {

				return $http.get('modulos/' + idModulo + '/setores');

			};

			this.buscar = function(idModulo) {

				return $http.get('modulo/' + idModulo);

			};

			this.criarNovasCredenciais = function(idModulo) {

				return $http.put('modulo/' + idModulo);

			};

			this.buscarTodos = function() {

				return $http.get('modulos');

			};

			this.buscarTodosComPerfis = function() {

				return $http.get('modulos/buscaModuloComPerfis');

			};

		});

})();
