(function() {

	var modulo = angular.module('appModule');

	modulo.controller('VisualizacaoPerfilController', function($scope, config, perfilService, $rootScope, $routeParams, $location) {

		function init() {

			visualizarPerfil($routeParams.id);

		}

		$scope.voltar = function() {

			$location.path('/perfil/gestao');

		};

		function visualizarPerfil(idPerfil) {

			if(!$rootScope.verificarPermissao($rootScope.AcaoSistema.VISUALIZAR_PERFIL)) {

				return $rootScope.$broadcast('showMessageEvent',$rootScope.Mensagens.NAO_TEM_PERMISSAO, 'danger');

			}

			perfilService.buscar(idPerfil).then(

				function(response) {

					$scope.perfil = response.data;

				},
				function(error) {

					$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

				}

			);

		}

		function getNumeroPermissoesPerfil(permissoes) {

			var cont = 0;

			_.each(permissoes, function(permissao) { cont += permissao.isFromPerfilVisualizar ? 1 : 0;});

			return cont;

		}

		$scope.getMensagem = function(permissoes) {

			var numeroPermissoesModulo = permissoes.length;
			var numeroPermissoesPerfil = getNumeroPermissoesPerfil(permissoes);

			return "(" + numeroPermissoesPerfil.toString() + " " + (numeroPermissoesPerfil == 1 ? "permissão selecionada" : "permissões selecionadas") + " de " + numeroPermissoesModulo.toString() + (numeroPermissoesModulo == 1 ? " possível" : " possíveis") + ")";

		};

		init();

	});

})();