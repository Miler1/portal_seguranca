(function() {

	var modulo = angular.module('appModule');

	modulo.controller('CadastroSetorController', function($scope, $routeParams, moduloService, setorService, $location, $rootScope) {

		function init(){

			findSetoresPai();

			if ($rootScope.AcaoSistema.EDITAR_SETOR && $routeParams.id) {

				recuperarSetor($routeParams.id);
			}

			findModulosAll();

		}

		$scope.tiposSetor = [];
		$scope.listaSetoresPai = [];
		$scope.modulos = [];
		$scope.perfisUsuario = [];

		$scope.setor = {

			nome: null,
			sigla: null,
			tipo: null,
			setorPai: null,
			perfis: []

		};

		$scope.tiposSetor = [
			{"id": 0, "nome": "Secretaria", "codigo": "SECRETARIA"},
			{"id": 1, "nome": "Diretoria", "codigo": "DIRETORIA"},
			{"id": 2, "nome": "Coordenadoria", "codigo": "COORDENADORIA"},
			{"id": 3, "nome": "Gerência", "codigo": "GERENCIA"}
		];


		function findSetoresPai() {

			setorService.findSetoresPai().then(
				function(response) {

					$scope.listaSetoresPai = response.data;

					if ($routeParams.id) {

						var index = _.findIndex($scope.listaSetoresPai, {
							id: $routeParams.id
						});
						$scope.listaSetoresPai.splice(index, 1);
					}
				},
				function(error) {

					$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

				}

			);
		}

		function findModulosAll() {

			moduloService.buscarTodosComPerfis().then(
				function(response) {

					$scope.modulos = response.data;

				},
				function(error) {

					$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

				}

			);
		}

		function recuperarSetor(idSetor) {

			setorService.buscar(idSetor).then(
				function(response) {

					$scope.setor = response.data;

					if ($scope.setor.setorPai) {

						$scope.setor.setorPai = $scope.setor.setorPai.id;
					}
				},
				function(error) {

					$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

				}
			);
		}

		$scope.voltar = function() {

			if($rootScope.verificarPermissao($rootScope.AcaoSistema.PESQUISAR_SETOR)) {

				$location.path('/setor/gestao');

			} else {

				$rootScope.setForbiddenMessageAndRedirectToLogin();

			}

		};

		$scope.salvarSetor = function() {

			var servico = $scope.setor.id ? 'update' : 'save';

			if($scope.form.$invalid) {

				var message = 'Verifique os campos obrigatórios.';

				$rootScope.$broadcast('showMessageEvent', message , 'danger');

				return;

			}

			if ($scope.setor.setorPai) {

				$scope.setor.setorPai = { id: $scope.setor.setorPai };
			}

			setorService[servico]($scope.setor).then(
				function(response) {

					$scope.$emit('showMessageEvent', response.data.text, 'success', true);

					$location.path('/setor/gestao');

				},
				function(error) {

					$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

					$scope.setor.setorPai = $scope.setor.setorPai.id;

				}

			);

		};

		$scope.carregarPerfis = function (modulo) {

			$scope.perfisUsuario = modulo.perfis;
			trimPerfis($scope.setor.perfis);
		};

		function trimPerfis(perfis) {

			perfis.forEach(function(perfil) {

				$scope.perfisUsuario.forEach(function (perfilUsuario, index) {

					if(perfilUsuario.id === perfil.id) {
						$scope.perfisUsuario.splice(index, 1);
					}
				});

			});

		}

		$scope.adicionarPerfil = function(perfil) {

			if(!perfil)
				return;

			var index = _.findIndex($scope.perfisUsuario, {
				id: perfil.id
			});
			$scope.perfisUsuario.splice(index, 1);

			$scope.setor.perfis.push(perfil);

		};

		$scope.removerPerfil = function(perfil) {

			var index = _.findIndex($scope.setor.perfis, {
				id: perfil.id
			});
			$scope.setor.perfis.splice(index, 1);

			$scope.perfisUsuario.push(perfil);

		};

		init();

	});

})();