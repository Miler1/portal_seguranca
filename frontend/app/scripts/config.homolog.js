(function() {

	var modulo = angular.module('appModule');

	modulo.value('config', {

		BASE_URL:'/portal-seguranca/',
		LOGIN_REDIRECT_URL:'/portal-seguranca/',
		CADASTRO_UNIFICADO:'http://homologacao.sistemas.sema.ap.gov.br/cadastro-unificado/',
		CRIAR_CONTA_PRODAP: 'https://homologacao.portal.ap.gov.br/login',
		RECUPERAR_SENHA_PRODAP: 'https://ssohomo.prodap.ap.gov.br/login/recuperar',
		RECUPERAR_SENHA_PRODAP_HEADER: 'https://ssohomo.prodap.ap.gov.br/login/',
		COOKIE_DOMAIN:'sema.ap.gov.br',
		COOKIE_PATH: '/portal-seguranca/'
	});

})();