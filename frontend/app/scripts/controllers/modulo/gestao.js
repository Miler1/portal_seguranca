(function() {

	var modulo = angular.module('appModule');

	modulo.controller('GestaoModuloController', function($scope, constantes, moduloService, $location, $rootScope, authService) {

		function init(){

			$scope.constantes = constantes;
			$scope.modulos = [];
			$scope.filtro = {};
			$scope.campo = 'Nome';
			$scope.ordenacaoReversa = false;
			$scope.paginacao = new app.Paginacao($scope.constantes.tamanhoPaginacao, $scope.listar);

			$scope.listar($scope.campo, $scope.ordenacaoReversa);

		}

		$rootScope.verifyMessages();

		function ordenacaoPadrao() {

			var ordenacao = 'NOME';

			if($scope.ordenacaoReversa === true)
				ordenacao += '_DESC';
			else
				ordenacao += '_ASC';

			return ordenacao;

		}

		$scope.listar = function(campo, ordenacaoReversa) {

			if($rootScope.verificarPermissao($rootScope.AcaoSistema.PESQUISAR_MODULOS)) {

				$scope.campo = campo;
				$scope.ordenacaoReversa = ordenacaoReversa;
				$scope.filtro.ordenacao = ordenacaoPadrao();
				$scope.filtro.tamanhoPagina = $scope.paginacao.tamanhoPagina;
				$scope.filtro.numeroPagina = $scope.paginacao.paginaAtual;

				moduloService.listarPorFiltro($scope.filtro).then(
					function(response) {

						$scope.modulos = response.data.pageItems;

						$scope.paginacao.atualizar(response.data.totalItems);

					},
					function(error) {

						$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

					}

				);

			} else {

				$rootScope.setForbiddenMessageAndRedirectToLogin();

			}

		};

		$scope.limparFiltro = function() {

			$scope.filtro.nome = null;
			$scope.filtro.sigla = null;
			$scope.filtro.ativo = null;

			$scope.listar();

		};

		$scope.irCadastro = function() {

			if($rootScope.verificarPermissao($rootScope.AcaoSistema.CADASTRAR_MODULO)) {

				$location.path('/modulo/cadastro/1');

			} else {

				$rootScope.setForbiddenMessageAndRedirectToLogin();

			}

		};

		$scope.visualizar = function(id) {

			if(!$rootScope.verificarPermissao($rootScope.AcaoSistema.VISUALIZAR_MODULO)) {

				$rootScope.setForbiddenMessageAndRedirectToLogin();

			}

			$location.path("/modulo/visualizacao/" + id);

		};

		$scope.criarNovasCredenciais = function(id) {

			if(!$rootScope.verificarPermissao($rootScope.AcaoSistema.CRIAR_NOVAS_CREDENCIAIS)) {

				return $rootScope.$broadcast('showMessageEvent',$rootScope.Mensagens.NAO_TEM_PERMISSAO, 'danger');

			}

			moduloService.criarNovasCredenciais(id).then(

				function(response) {

					$scope.oAuthClient = response.data.unHashedOAuthClient;
					$scope.novasCredenciais = true;

					$rootScope.abrirModal('modalCadastroModulo');

				},

				function(error) {

					$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

				}

			);

		};

		$scope.irGestaoModulos = function() {

			$rootScope.fecharModal('modalCadastroModulo');
			$('body').removeClass('modal-open');
			$('.modal-backdrop').remove();

		};

		$scope.abrirModalConfirmacao = function(idModulo) {

			$scope.idModulo = idModulo;
			$scope.confirmForm.$setPristine();
			$scope.confirmForm.$setUntouched();
			$scope.password = null;
			$scope.senhaEnviada = null;
			$scope.senhaInvalida = null;

			$rootScope.abrirModal('modalConfirmacao');

		};

		$scope.confirmarSolicitacao = function(idModulo, password) {
			
			$scope.senhaEnviada = password;
			var login = $rootScope.getUsuario().login;
			
			authService.autenticarSenha(login,password).then(
				function(response){

					$rootScope.fecharModal('modalConfirmacao');
					$scope.criarNovasCredenciais(idModulo);

				},
				function(error) {
					 
					$scope.senhaInvalida = error.data;

				}
				
			);

		};
		
		init();

	});

})();
