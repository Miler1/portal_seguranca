(function() {

	var modulo = angular.module('appModule');

	modulo.controller('InicialController', function($window, $scope, $route, config, $rootScope, usuarioService, authService, previousPath, $location) {

		$scope.atualizarCadastro = function() {

			var usuario = $rootScope.getUsuario();

			if(previousPath.getLastPath().split('/')[1] == 'modulo') {

				$rootScope.setRedirect($location.path().split('#')[0]);

				if($rootScope.getUsuario().login.length == 11) {

					$rootScope.goToRouteCadastroUnificado('#/pessoaFisica/cadastro/' + usuario.pessoaId + '/edicao');

				} else {

					$rootScope.goToRouteCadastroUnificado('#/pessoaJuridica/cadastro/' + usuario.pessoaId + '/edicao');

				}

			} else {

				$rootScope.setRedirect($location.path().split('#')[0]);

				if($rootScope.getUsuario().login.length == 11) {

					$rootScope.goToRouteCadastroUnificado('#/public/pessoaFisica/cadastro/' + usuario.pessoaId);

				} else {

					$rootScope.goToRouteCadastroUnificado('#/public/pessoaJuridica/cadastro/' + usuario.pessoaId);

				}

			}

		};

		// $scope.senhaAtual = null;
		// $scope.senhaConfirmacao = null;
		// $scope.senhaNova = null;

		function initForm() {

			// $scope.formModalAtualizarSenha.$setPristine();
			// $scope.formModalAtualizarSenha.$setUntouched();
			// $scope.senhaAtual = null;
			// $scope.senhaConfirmacao = null;
			// $scope.senhaNova = null;

		}

		$scope.abrirModalAtualizarSenha = function() {

			$window.open(config.RECUPERAR_SENHA_PRODAP_HEADER);

		};

		// $scope.salvarNovaSenha = function() {

		// 	if($scope.formModalAtualizarSenha.$error.pwCheck) {

		// 		return;

		// 	}

		// 	var parametros = 'id=' + $rootScope.getUsuario().id + '&senhaAtual=' + $scope.senhaAtual + '&senhaNova=' + $scope.senhaNova;

		// 	usuarioService.atualizarSenha(parametros).then(
		// 		function(response) {

		// 			$rootScope.fecharModal('modalAtualizarSenha');

		// 			$rootScope.$broadcast('showMessageEvent', response.data.text, 'success');

		// 		},

		// 		function(error) {

		// 			$scope.senhaAtualDiferente = error;

		// 		}
		// 	);

		// };

		$scope.refresh = function(){

			$route.reload();

		};

		$scope.irHome = function() {

			$location.path('/home');

		};

	});

})();
