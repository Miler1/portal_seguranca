(function() {

	var modulo = angular.module('appModule');

	modulo.controller('GestaoPerfilController', function($scope, constantes, moduloService, $location, $rootScope, perfilService) {

		function init(){

			$scope.constantes = constantes;
			$scope.perfis = [];
			$scope.filtro = {};
			$scope.campo = 'Nome';
			$scope.ordenacaoReversa = false;
			$scope.paginacao = new app.Paginacao($scope.constantes.tamanhoPaginacao, $scope.listar);

			$scope.listar($scope.campo, $scope.ordenacaoReversa);

		}

		function verifyMessages() {

			var successMessage = $rootScope.getMessage('successMessageAfterRedirect');
			var errorMessage = $rootScope.getMessage('errorMessageAfterRedirect');

			if(successMessage) {

				$scope.$emit('showMessageEvent', successMessage, 'success', true);

				$rootScope.removeMessage('successMessageAfterRedirect');

			} else if(errorMessage) {

				$scope.$emit('showMessageEvent', errorMessage, 'danger', true);

				$rootScope.removeMessage('errorMessageAfterRedirect');

			}

		}

		verifyMessages();

		function ordenacaoPadrao() {

			var ordenacao = 'NOME';

			if($scope.campo === 'Modulo') {

				ordenacao = 'MODULO';

			}

			if($scope.ordenacaoReversa === true) {

				ordenacao += '_DESC';

			} else {

				ordenacao += '_ASC';

			}

			return ordenacao;

		}

		$scope.listar = function(campo, ordenacaoReversa) {

			if(!$rootScope.verificarPermissao($rootScope.AcaoSistema.PESQUISAR_PERFIS)) {

				$rootScope.setForbiddenMessageAndRedirectToLogin();

			}

			$scope.campo = campo;
			$scope.ordenacaoReversa = ordenacaoReversa;
			$scope.filtro.ordenacao = ordenacaoPadrao();
			$scope.filtro.tamanhoPagina = $scope.paginacao.tamanhoPagina;
			$scope.filtro.numeroPagina = $scope.paginacao.paginaAtual;

			perfilService.listarPorFiltro($scope.filtro).then(
				function(response) {

					$scope.perfis = response.data.pageItems;

					$scope.paginacao.atualizar(response.data.totalItems);

				},
				function(error) {

					$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

				}

			);

		};

		$scope.limparFiltro = function() {

			$scope.filtro.nome = null;
			$scope.filtro.siglaModulo = null;
			$scope.filtro.ativo = null;

			$scope.listar();

		};

		// $scope.irCadastro = function() {

		// 	if(!$rootScope.verificarPermissao($rootScope.AcaoSistema.CADASTRAR_PERFIL)) {

		// 		return $rootScope.$broadcast('showMessageEvent', $rootScope.Mensagens.NAO_TEM_PERMISSAO, 'danger');


		// 	}

		// 	$location.path('/modulo/cadastro/1');

		// };

		init();

	});

})();
