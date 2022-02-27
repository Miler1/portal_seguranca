(function() {

	var modulo = angular.module('appModule');

	modulo.controller('HomeController', function($scope, $rootScope, usuarioService, moduloService, authService, $location, constantes, setorService) {

		function init() {

			var usuario = $rootScope.getUsuario();

			if(!usuario) {

				$location.path('/');

			}

			$rootScope.verifyMessages();

			usuarioService.buscarModulosPermitidos(usuario.login).then(
				function(response) {

					$scope.modulosPermitidos = response.data.modulosPermitidos;

				},
				function(error) {

					$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

				}

			);

			function verificaSelecaoSetor() {

				if ($scope.perfis && $scope.perfis.length > 0) {

					return $scope.perfis.some(function (perfil) {
						return perfil.setores.length > 0;
					});

				} else {
					return false;
				}

			}

			$scope.getPerfisSetores = function(modulo) {

				$scope.perfis = [];
				$scope.setores = [];

				moduloService.getPerfis(modulo.id).then(

					function(response) {

						$scope.perfis = response.data;

						$scope.habilitaSelecaoSetor = verificaSelecaoSetor();

						usuarioService.getSetoresUsuario(usuario.login).then(

							function(response) {

								$scope.setoresUsuario = response.data;

								if ($scope.perfis && $scope.perfis.length > 0) {

									$scope.setarPerfil($scope.perfis[0]);
								}

								if (($scope.perfis && $scope.perfis.length == 1) && (!$scope.setores || $scope.setores.length === 0) && (!$scope.habilitaSelecaoSetor)) {

									authService.selecionarPerfil($scope.perfis[0].id, modulo.id).then(function() {},
										function(error) {

											$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

										}

									);

								} else {

									$("#modalSelecaoPerfil").modal({keyboard: false});

									$scope.moduloSelecionado = modulo;

								}

							},
							function(error) {

								$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

							}

						);

					},
					function(error) {

						$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

					}

				);

			};

			function filtrarSetoresUsuarioByPerfil(perfilSelecionado) {

				var setores = $scope.setoresUsuario;

				$scope.setores = setores.filter(function (setorUsuario) {

					return perfilSelecionado.setores.some(function (setorPerfil) {
						return setorPerfil.id === setorUsuario.id;
					});
				});
			}

			$scope.setarPerfil = function(perfil) {

				$scope.perfil = perfil;

				filtrarSetoresUsuarioByPerfil($scope.perfil);

			};

			$scope.setarSetor = function(setor) {

				if ($scope.setor && $scope.setor.id === setor.id) {

					$scope.setor = null;

				} else {

					$scope.setor = setor;
				}

			};

			$scope.selecionarPerfil = function(perfil) {

				authService.selecionarPerfil(perfil.id, $scope.moduloSelecionado.id).then(
					function() {

					}

				);

			};

			$scope.acessarComPerfilSetor = function() {

				authService.selecionarPerfilSetor($scope.perfil.id, $scope.setor.id, $scope.moduloSelecionado.id).then(
					function() {

					}

				);

			};


		}

		if (!$scope.getUsuario().pessoaCadastro) {
			$rootScope.goToRouteCadastroUnificado('#/pessoaFisica/dadoscomplementares');
		} else {
			init();
		}

	});

})();
