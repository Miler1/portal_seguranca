var exports = {};
var app = exports;

(function($) {

	var modulo = angular.module('appModule', ['ngRoute', 'ngCookies', 'ui.bootstrap', 'affix', 'ui.utils.masks', 'uiCropper', 'btorfs.multiselect']);

	modulo.config(['$routeProvider', '$httpProvider', '$locationProvider', '$uibTooltipProvider',

		function($routeProvider, $httpProvider, $locationProvider, $uibTooltipProvider){

			// register the interceptor as a service, intercepts ALL angular ajax http calls
			$httpProvider.interceptors.push('httpInterceptor');

			$locationProvider.hashPrefix('');

			$uibTooltipProvider.options({
				popupDelay: 400
			});

			$routeProvider

				.when('/manual', {
					templateUrl: 'manual/portal.html'
				})
				.when('/inicial', {
					templateUrl: 'views/sections/inicial.html',
					controller: 'InicialController'
				})
				.when('/home', {
					templateUrl: 'views/sections/home.html',
					controller: 'HomeController'
				})
				.when('/acesso', {
					templateUrl: 'views/sections/usuarios/acesso.html',
					controller: 'AcessoController'
				})
				.when('/modulo/gestao', {
					templateUrl: 'views/sections/modulos/gestao.html',
					controller: 'GestaoModuloController'
				})
				.when('/modulo/visualizacao/:id', {
					templateUrl: 'views/sections/modulos/visualizacao.html',
					controller: 'VisualizacaoModuloController'
				})
				.when('/modulo/cadastro/1', {
					templateUrl: 'views/sections/modulos/cadastroUm.html',
					controller: 'CadastroModuloUmController'
				})
				.when('/modulo/cadastro/2', {
					templateUrl: 'views/sections/modulos/cadastroDois.html',
					controller: 'CadastroModuloDoisController'
				})
				.when('/perfil/gestao', {
					templateUrl: 'views/sections/perfis/gestao.html',
					controller: 'GestaoPerfilController',
					permission: app.AcaoSistema.PESQUISAR_PERFIS
				})
				.when('/perfil/visualizacao/:id', {
					templateUrl: 'views/sections/perfis/visualizacao.html',
					controller: 'VisualizacaoPerfilController',
					permission: app.AcaoSistema.VISUALIZAR_PERFIL
				})
				.when('/setor/gestao', {
					templateUrl: 'views/sections/setores/gestao.html',
					controller: 'GestaoSetorController',
					permission: app.AcaoSistema.PESQUISAR_SETOR
				})
				.when('/setor/cadastro', {
					templateUrl: 'views/sections/setores/cadastro.html',
					controller: 'CadastroSetorController',
					permission: app.AcaoSistema.CADASTRAR_SETOR
				})
				.when('/setor/cadastro/:id' , {
					templateUrl: 'views/sections/setores/cadastro.html',
					controller: 'CadastroSetorController',
					permission: app.AcaoSistema.EDITAR_SETOR
				})
				.when('/setor/visualizacao/:id', {
					templateUrl: 'views/sections/setores/visualizacao.html',
					controller: 'VisualizacaoSetorController',
					permission: app.AcaoSistema.VISUALIZAR_SETOR
				})
				.otherwise({
					redirectTo: '/'
				});

		}

	]);

	modulo.constant('constantes', {
		"status": [
			{
				"nome": "Ativo",
				"valor": true
			},
			{
				"nome": "Inativo",
				"valor": false
			}
		],
		"tamanhoPaginacao": 10,
		"USUARIO_EXISTE": 0,
		"PESSOA_EXISTE": 1,
		"PESSOA_NAO_EXISTE": 2,
		"HTTPStatus" : {
			"OK": '200',
			"REDIRECT": '312',
			"UNAUTHORIZED": '401',
			"FORBIDDEN": '403',
			"ACCEPTED": '202'
		},
		"ALERT_WARNING_DELAY_TIME": 15000,
		"ALERT_DEFAULT_DELAY_TIME": 5000
	});

	modulo.controller('AppCtrl', ["$scope", "config", "$rootScope", "$cookies", "$location", "$timeout", "$window", "previousPath", "constantes",
		function($scope, config, $rootScope, $cookies, $location, $timeout, $window, previousPath, constantes) {

			$.material.ripples();
			$.material.checkbox();
			$.material.radio();

			$scope.pathAtual = $location.path().split('/')[1] || '';

			$rootScope.previousPath = previousPath;

			$scope.goToRoute = function(path) {

				$scope.pathAtual = path.split('/')[0];

				$location.path(path);

			};

			$rootScope.goToRouteCadastroUnificado = function(path) {

				$window.location.href = config.CADASTRO_UNIFICADO + path;

			};

			$rootScope.AcaoSistema = app.AcaoSistema;
			$rootScope.Mensagens = app.Mensagens;

			$rootScope.setUsuario = function(usuario) {

				$cookies.putObject("usuarioEntradaUnica", usuario, { path: '/', domain: config.COOKIE_DOMAIN });

			};

			$rootScope.getUsuario = function() {

				return $cookies.getObject("usuarioEntradaUnica");

			};

			$rootScope.getUsuarioCadastroUnificado = function() {

				return $cookies.getObject("usuario");

			};

			$rootScope.removeUsuario = function() {

				$cookies.remove("usuarioEntradaUnica", { path: '/', domain: config.COOKIE_DOMAIN });

			};

			// $rootScope.removeUsuarioCadastroUnificado = function() {

			// 	return $cookies.remove("usuario", { path: '/', domain: config.COOKIE_DOMAIN });

			// };

			$rootScope.setRedirect = function(path) {

				$cookies.put("redirect", path, { path: '/', domain: config.COOKIE_DOMAIN });

			};

			$rootScope.setErrorMessage = function(message) {

				return $cookies.putObject("errorMessageAfterRedirect", message, { path: '/', domain: config.COOKIE_DOMAIN });

			};

			$rootScope.getMessage = function(message) {

				return $cookies.getObject(message, { path: '/', domain: config.COOKIE_DOMAIN });

			};

			$rootScope.removeMessage = function(message) {

				$cookies.remove(message, { path: '/', domain: config.COOKIE_DOMAIN });

			};

			$rootScope.logout = function() {

				$rootScope.removeUsuario();
				// $rootScope.removeUsuarioCadastroUnificado();

				$scope.pathAtual = "";

				// location.href = "logout";
				location.href = config.CADASTRO_UNIFICADO + "logout";

			};

			$rootScope.abrirModal = function(nomeModal) {

				$('.modalVisualizar').collapse('show');

				$('.modalEsconder').collapse('hide');

				$("#" + nomeModal).modal('show').collapse('show');

			};

			$rootScope.fecharModal = function(nomeModal) {
				$("#" + nomeModal).modal('hide');
			};

			$rootScope.isRoute = function(path) {

				return $location.path() === path;

			};

			// Verifica se possui a ação sistema. Se não for informado o Permissivel será utilizado o usuário logado
			$rootScope.verificarPermissao = function(idAcao, permissivel) {

				if(!permissivel)
					permissivel = $rootScope.getUsuarioCadastroUnificado();

				return permissivel && permissivel.permittedActionsIds && permissivel.permittedActionsIds.indexOf(idAcao) >= 0;

			};

			// Verifica se possui alguma das ações sistema. Se não for informado o Permissivel será utilizado o usuário logado.
			$rootScope.verificarPermissoes = function(idsAcoes, permissivel) {

				if(!permissivel) {

					permissivel = $rootScope.getUsuarioCadastroUnificado();

				}

				if(idsAcoes !== null && idsAcoes.length > 0) {

					var possuiAcao = false;

					for(var i = 0; i < idsAcoes.length; i++){

						if (!possuiAcao)
							possuiAcao = this.verificarPermissao(idsAcoes[i], permissivel);

					}

					return possuiAcao;

				}

				return false;

			};

			$rootScope.setUnauthorizedMessageAndRedirectToLogin = function() {

				if($rootScope.rota.public) {

					return;

				}

				$rootScope.setErrorMessage($rootScope.Mensagens.SESSION_TIMEOUT);
				$rootScope.goToRoutePortalSeguranca("#/");

			};

			$rootScope.tituloPagina = null;

			function showMessage(texto, tipo, showTime) {

				var delay;

				if(Number.isInteger(showTime)) {

					delay = showTime;

				} else {

					delay = constantes.ALERT_DEFAULT_DELAY_TIME;

				}

				$.notify({ message: texto },{ delay: delay, type: tipo });

			}

			function hideMessage() {

				// $scope.msg.show = false;

			}

			// Verifica as permissoes configuradas nas rotas
			$scope.$on('$routeChangeStart', function(event, next) {

				var permission = next.permission;
				var permissions = next.permissions;

				if((permission && !$rootScope.verificarPermissao(permission)) ||
					(permissions && !$rootScope.verificarPermissoes(permissions)) ) {

					$rootScope.setForbiddenMessageAndRedirectToLogin();

				}

				if(!$rootScope.getUsuario() && $scope.pathAtual !== "") {

					if($scope.pathAtual === 'acesso') {

						return;

					}

					$rootScope.setForbiddenMessageAndRedirectToLogin();

				}

			});

			$rootScope.setForbiddenMessageAndRedirectToLogin = function () {

				showMessage($rootScope.Mensagens.SESSION_TIMEOUT, "danger");
				$scope.goToRoute("/");

			};

			// Evento de mudanca de rota
			$scope.$on('$routeChangeSuccess', function(scope, next, current) {

				if (!$scope.msg)
					return;

				if (!$scope.msg.hideOnRouteChange)
					$scope.msg.hideOnRouteChange = true;
				else
					hideMessage();

			});

			// Evento de exibição de mensagem
			$scope.$on('showMessageEvent', function(event, texto, tipo, showTime) {
				showMessage(texto, tipo, showTime);
			});

			// Evento para remover mensagem
			$scope.$on('hideMessageEvent', function(event) {
				hideMessage();
			});

			$rootScope.timeoutAndRedirectToHome = function() {

				$timeout(function(){

					$scope.$apply(function() {

						$location.path('/').replace();

					});

				}, 3000);

			};

			$rootScope.setErrorMessage = function(message) {

				return $cookies.putObject("errorMessageAfterRedirect", message, { path: '/', domain: config.COOKIE_DOMAIN });

			};

			$rootScope.verifyMessages = function() {

				var successMessage = $rootScope.getMessage('successMessageAfterRedirect');
				var errorMessage = $rootScope.getMessage('errorMessageAfterRedirect');
				var warningMessage = $rootScope.getMessage('warningMessageAfterRedirect');

				if(successMessage) {

					$scope.$emit('showMessageEvent', successMessage, 'success');

					$rootScope.removeMessage('successMessageAfterRedirect');

				} else if(errorMessage) {

					$scope.$emit('showMessageEvent', errorMessage, 'danger');

					$rootScope.removeMessage('errorMessageAfterRedirect');

				} else if(warningMessage) {

					$scope.$emit('showMessageEvent', warningMessage, 'warning', constantes.ALERT_WARNING_DELAY_TIME);

					$rootScope.removeMessage('warningMessageAfterRedirect');

				}

			};

		}

	]);

	//for DatePicker options
	modulo.config(['uibDatepickerConfig', function (uibDatepickerConfig) {
		uibDatepickerConfig.showWeeks = false;
	}]);

	//for DatePickerPopup options
	modulo.config(['uibDatepickerPopupConfig', function (uibDatepickerPopupConfig) {
		uibDatepickerPopupConfig.showButtonBar = false;
	}]);

})(jQuery);
