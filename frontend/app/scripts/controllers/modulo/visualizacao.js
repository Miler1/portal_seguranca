(function() {

		var modulo = angular.module('appModule');

		modulo.controller('VisualizacaoModuloController', function($scope, config, moduloService, $rootScope, $routeParams, $location) {

			function init() {

				$scope.visualizarModulo($routeParams.id);

			}

			$scope.labels = {
				"itemsSelected": "perfis",
				"selectAll": "Todos",
				"unselectAll": "Remover selecionados",
				"select": "Todos"

			};

			$scope.voltar = function() {

				$location.path('/modulo/gestao');

			};

			$scope.visualizarModulo = function(idModulo) {

				if(!$rootScope.verificarPermissao($rootScope.AcaoSistema.VISUALIZAR_MODULO)) {

					$rootScope.setForbiddenMessageAndRedirectToLogin();

				}

				moduloService.buscar(idModulo).then(

					function(response) {

						$scope.modulo = response.data;

						$scope.modulo.perfis = _.sortBy($scope.modulo.perfis, 'nome');

						$scope.createPerfilList($scope.modulo.perfis);

						$scope.combobox = {perfis :$scope.modulo.perfis};

					},
					function(error) {

						$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

					}

				);

			};

			$scope.createPerfilList = function(perfis){

				_.each($scope.modulo.permissoes, function(permissao) { permissao.perfilChecked = []; });

				_.each(perfis, function(perfilList) {

					_.each($scope.modulo.permissoes, function(permissao) {

						if(_.find(permissao.perfis, function (perfil) { return perfilList.nome == perfil.nome; })){

							permissao.perfilChecked.push(true);

						} else {

							permissao.perfilChecked.push(false);

						}

					});

				});

			};

			$scope.$watch('combobox.perfis', function(newValue) {

				if(newValue) {

					if(newValue.length > 0) {

						$scope.createPerfilList(newValue);

						return;

					}

				}

				if($scope.modulo) {

					$scope.createPerfilList($scope.modulo.perfis);

				}

			}, true);

			init();

		});

	})();