(function() {

	'use strict';

	angular
		.module('appModule')
		.service('usuarioService', function($http, $rootScope) {

		this.validarToken = function(token) {

			return $http.get("usuario/validarToken/" + token);

		};
		
		this.verificarPessoa = function(login) {

			return $http.get("usuario/verificarPessoa/" + login);

		};

		// this.definirSenhaPrimeiroAcesso = function(parametros) {

		// 	return $http({
		// 		url: "usuario/definirSenhaPrimeiroAcesso",
		// 		method: "PUT",
		// 		data: parametros,
		// 		headers: {
		// 			'Content-Type': 'application/x-www-form-urlencoded'
		// 		}
		// 	});

		// };

		// this.emailRedefinirSenha = function(login) {

		// 	return $http.get("usuario/emailRedefinirSenha/" + login);

		// };

		// this.redefinirSenha = function(parametros) {

		// 	return $http({
		// 		url: "usuario/redefinirSenha",
		// 		method: "PUT",
		// 		data: parametros,
		// 		headers: {
		// 			'Content-Type': 'application/x-www-form-urlencoded'
		// 		}
		// 	});

		// };

		this.temUsuarioComLogin = function(login) {

			return $http.get("public/temUsuarioComLogin/" + login);

		};

		this.pessoaEstaBloqueado = function(login) {

			return $http.get("public/pessoaEstaBloqueado/" + login);

		};

		this.buscarModulosPermitidos = function(login) {

			return $http.get("usuario/modulosPermitidos/" + login);

		};

		// this.atualizarSenha = function(parametros) {

		// 	return $http({
		// 		url: "usuario/alterarSenha",
		// 		method: "POST",
		// 		data: parametros,
		// 		headers: {
		// 			'Content-Type': 'application/x-www-form-urlencoded'
		// 		}
		// 	});

		// };

		this.getSetoresUsuario = function(login) {

			return $http.get("usuario/setores/" + login);

		};

	});

})();
