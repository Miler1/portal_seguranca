(function() {

	var modulo = angular.module('appModule');

	modulo.value('config', {

		BASE_URL: '/portal-seguranca/',
		LOGIN_REDIRECT_URL: '/portal-seguranca/',
		CADASTRO_UNIFICADO: 'https://sistemas.sema.ap.gov.br/cadastro-unificado/',
		CRIAR_CONTA_PRODAP: 'https://servicos.portal.ap.gov.br/login',
		RECUPERAR_SENHA_PRODAP: 'https://sso.prodap.ap.gov.br/login/recuperar',
		RECUPERAR_SENHA_PRODAP_HEADER: 'https://sso.prodap.ap.gov.br/login/',
		COOKIE_DOMAIN: 'sema.ap.gov.br',
		COOKIE_PATH: '/portal-seguranca/',
		URL_PORTAL_SEMA: 'https://servicos.portal.ap.gov.br/login'
	});

})();