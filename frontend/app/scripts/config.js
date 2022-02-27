(function() {

	var modulo = angular.module('appModule');

	modulo.value('config', {

		BASE_URL: '/',
		LOGIN_REDIRECT_URL: '/',
		CADASTRO_UNIFICADO: 'http://localhost:9901/',
		CRIAR_CONTA_PRODAP: 'https://homologacao.portal.ap.gov.br/login',
		RECUPERAR_SENHA_PRODAP: 'https://ssohomo.prodap.ap.gov.br/login/recuperar',
		RECUPERAR_SENHA_PRODAP_HEADER: 'https://ssohomo.prodap.ap.gov.br/login/',
		COOKIE_DOMAIN: 'localhost',
		COOKIE_PATH: '/'
	});

})();