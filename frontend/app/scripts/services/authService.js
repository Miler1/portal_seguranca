(function() {

	'use strict';

	angular
		.module('appModule')
		.service('authService', function($http, $rootScope, config, requestUtil) {

		this.login = function(username, password) {

			var parameter = 'username=' + username.replace(/[.-]/g, "") + '&password=' + password;

			return $http({
				url: 'login',
				method: "POST",
				data: parameter,
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded'
				}
			});

		};

		this.loginCertificado = function(successCallback, errorCallback) {

			requestUtil.get(config.BASE_URL + "login/certificado", successCallback, errorCallback);
		};

		this.loginPerfil = function(perfil) {

			return $http.post("/login/perfil/" + perfil.id, null);
		};

		this.autenticarSenha = function(login,password) {

			var parameter = 'login=' + login.replace(/[.-]/g, "") + '&password=' + password;

			return $http({
				url: 'autenticarSenha',
				method: "POST",
				data: parameter,
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded'
				}
			});

		};

		this.isUserAuthenticated = function(user) {

			return $http.post("/usuario/" + user.id + "/authenticated");

		};

		this.getAcoesPermitidas = function() {

			return $http.get("/usuario/acoesPermitidas");

		};

		this.selecionarPerfil = function(idPerfil, idModulo) {

			var parameter = 'idPerfil=' + idPerfil + '&idModulo=' + idModulo;

			return $http({
				url: 'redirecionarModulo',
				method: "POST",
				data: parameter,
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded'
				}
			});

		};

		this.selecionarPerfilSetor = function(idPerfil, idSetor, idModulo) {

			var parameter = 'idPerfil=' + idPerfil + '&idSetor=' + idSetor +'&idModulo=' + idModulo;

			return $http({
				url: 'redirecionarModulo',
				method: "POST",
				data: parameter,
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded'
				}
			});

		};

	});

})();
