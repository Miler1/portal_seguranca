(function() {

	var modulo = angular.module('appModule');

	modulo.controller('LoginController', function($window, $scope, $rootScope, $location, $timeout, authService, config, usuarioService, constantes, $cookies) {

		function init() {

			$rootScope.verifyMessages();

		}

		$scope.verifyUsuario = function() {

			if($rootScope.getUsuario()) {

				authService.isUserAuthenticated($rootScope.getUsuario()).then(
					function(response) {

						if(response.isAuthenticated)
							$location.path('/inicial');
						else
							$rootScope.removeUsuario();

					},
					function(error) {
						$scope.$emit('showMessageEvent', error.data, 'danger');
					}
				);

			}

		};

		$scope.login = function() {

			if((!$scope.username) || (!$scope.password)) {

				$scope.$emit("showMessageEvent", "Usuário e/ou senha inválidos.", "danger");

			} else {
				
				// checa se a pessoa fisica foi cadastrada via sincronização (Rede Simples) se caso houver somente edita os dados complementares
				let pessoaId;
				usuarioService.verificarPessoa($scope.username).then(
					function(response) {
						pessoaId = response.data.pessoaId;
					}
				);
				
				authService.login($scope.username, $scope.password).then(
					function(response){

						$scope.usuario = response.data;

						//Caso não seja um usuario no portal sema, deve ser redirecionado
						if(!$scope.usuario.portalSema) {
							$scope.irCadastro();

							return;
						}

						//Se o usuário for nulo, é pq a autenticação funcionou, porém o usuário precisa fornecer os dados complementares						
						if(!$scope.usuario.pessoaCadastro) {

							$rootScope.setUsuario($scope.usuario);

							if (pessoaId) {
								$rootScope.goToRouteCadastroUnificado("#/pessoaFisica/dadoscomplementares/" + pessoaId);
							} else {
								$rootScope.goToRouteCadastroUnificado("#/pessoaFisica/dadoscomplementares");
							}
							
							return;
							
						}

						$rootScope.setUsuario($scope.usuario);
						

						$location.path('/home');

					},
					function(error) {

						$scope.$emit("showMessageEvent", error.data, "danger", null, true);

					}

				);

			}

		};

		// $scope.emailRedefinirSenha = function() {

		// 	usuarioService.emailRedefinirSenha($scope.loginUsuario).then(

		// 		function(response) {

		// 			if(response.status == constantes.HTTPStatus.OK) {

		// 				$rootScope.$broadcast('showMessageEvent', response.data.text, 'success');

		// 			} else if(response.status == constantes.HTTPStatus.ACCEPTED) {

		// 				$rootScope.$broadcast('showMessageEvent', response.data.text, 'warning', 15000);

		// 			}

		// 			$("#modalRedefinirSenha").modal('hide');

		// 		},
		// 		function(error) {

		// 			$scope.$emit('showMessageEvent', error.data, 'danger', null, true);

		// 			$("#modalRedefinirSenha").modal('hide');

		// 		}

		// 	);

		// };

		$scope.redefinirSenha = function() {

			$window.open(config.RECUPERAR_SENHA_PRODAP);

		};

		$scope.irCadastro = function() {

			$window.open(config.CRIAR_CONTA_PRODAP);

		};

		// $scope.verificarLogin = function() {

		// 	usuarioService.temUsuarioComLogin($scope.loginUsuario).then(
		// 		function(response) {

		// 			if(response.data) {

		// 			} else {

		// 				usuarioService.pessoaEstaBloqueado($scope.loginUsuario).then(
		// 					function(response) {

		// 						if(response.data) {

		// 							$("#modalCriarConta").modal("hide");

		// 							$rootScope.$broadcast('showMessageEvent', 'O limite de três tentativas foi excedido. Nova solicitação poderá ser realizada após 24 horas.', 'danger');

		// 						} else {

		// 							$rootScope.goToRouteCadastroUnificado('#/public/validacao/' + $scope.loginUsuario);

		// 						}

		// 					},
		// 					function(error) {

		// 						$("#modalCriarConta").modal("hide");

		// 						$rootScope.$broadcast('showMessageEvent', error.data , 'danger');

		// 					}
		// 				);

		// 			}

		// 		},
		// 		function(error) {

		// 			$("#modalCriarConta").modal("hide");

		// 			$rootScope.$broadcast('showMessageEvent', error.data , 'danger');

		// 		}

		// 	);

		// };

		$scope.loginPerfil = function(perfil) {

			authService.loginPerfil(perfil).then(
				function(response) {

					$scope.usuario = response.data;

					$("#modalSelecaoPerfil").on('hidden.bs.modal', function () {

						$rootScope.setUsuario($scope.usuario);

						$location.path('/inicial');

						$scope.$apply();

					});

					$("#modalSelecaoPerfil").modal("hide");

				},
				function(error) {

					$scope.$emit("showMessageEvent", error.data, "danger");
					$("#modalSelecaoPerfil").modal("hide");

				}
			);

		};

		function initForm() {

			// $scope.redefinirSenhaForm.$setPristine();
			// $scope.redefinirSenhaForm.$setUntouched();

		}

		function initFormCadastro() {

			// $scope.cadastrarUsuarioForm.$setPristine();
			// $scope.cadastrarUsuarioForm.$setUntouched();

		}

		// Bloquear Ctrl+V nos inputs de senha
		// window.onload = function() {
		// 	var novaSenha = document.getElementById('novaSenha');

		// 	novaSenha.onpaste = function(e) {
		// 		e.preventDefault();
		// 	};

		// 	var confirmacao = document.getElementById('confirmacao');

		// 	confirmacao.onpaste = function(e) {
		// 		e.preventDefault();
		// 	};
		// };

		init();

	});

})();
