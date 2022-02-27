(function() {

	var modulo = angular.module('appModule');

	modulo.value('config', {

		BASE_URL: '/portal-seguranca/',
		LOGIN_REDIRECT_URL: '/portal-seguranca/',
		CADASTRO_UNIFICADO: 'http://ap.puma.ti.lemaf.ufla.br/cadastro-unificado/',
		CRIAR_CONTA_PRODAP: 'https://homologacao.portal.ap.gov.br/login',
		RECUPERAR_SENHA_PRODAP: 'https://ssohomo.prodap.ap.gov.br/login/recuperar',
		RECUPERAR_SENHA_PRODAP_HEADER: 'https://ssohomo.prodap.ap.gov.br/login/',
		COOKIE_DOMAIN: '.ti.lemaf.ufla.br',
		COOKIE_PATH: '/portal-seguranca/'
	});

})();