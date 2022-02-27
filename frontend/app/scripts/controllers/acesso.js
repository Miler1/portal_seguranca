(function() {

		var modulo = angular.module('appModule');

		modulo.controller('AcessoController', function($scope, $rootScope, $location, usuarioService) {

			$scope.init = function() {

				validarToken($location.search().token);

			};

			// $scope.criarSenha = function() {

			// 	if($scope.form.$invalid) {

			// 		return;

			// 	}

			// 	if($scope.usuario.temSenha) {

			// 		redefinirSenha();

			// 	} else {

			// 		var parameter = 'id=' + $scope.usuario.id + '&senha=' + $scope.senha;

			// 		usuarioService.definirSenhaPrimeiroAcesso(parameter).then(

			// 			function(response) {

			// 				$location.path('/');

			// 				$rootScope.$broadcast('showMessageEvent', response.data.text, 'success');

			// 			},
			// 			function(error) {

			// 				$location.path('/');

			// 				$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

			// 			}

			// 		);

			// 	}

			// };

			// function redefinirSenha() {

			// 	var parameter = 'id=' + $scope.usuario.id + '&senha=' + $scope.senha;

			// 	usuarioService.redefinirSenha(parameter).then(

			// 		function(message) {

			// 			$location.path('/');

			// 			$rootScope.$broadcast('showMessageEvent', message.data.text, 'success');

			// 		},
			// 		function(error) {

			// 			$location.path('/');

			// 			$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

			// 		}

			// 	);

			// }

			function validarToken(token) {

				usuarioService.validarToken(token).then(

					function(response) {

						$scope.usuario = response.data;

					},
					function(error) {

						$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

						$rootScope.timeoutAndRedirectToHome();

					}

				);

			}

		}

	);

})();