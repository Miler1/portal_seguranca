(function() {

	var modulo = angular.module('appModule');

	modulo.controller('GestaoSetorController', function($scope, constantes, moduloService, $location, $rootScope, setorService) {

		function init(){

			$scope.constantes = constantes;
			$scope.setores = [];
			$scope.filtro = {};
			$scope.campo = 'Nome';
			$scope.ordenacaoReversa = false;
			$scope.paginacao = new app.Paginacao($scope.constantes.tamanhoPaginacao, $scope.listar);

			$scope.listar($scope.campo, $scope.ordenacaoReversa);

			$scope.tipoSetorFiltro = [
				{"id": 0, "nome": "Secretaria", "codigo": "SECRETARIA"},
				{"id": 1, "nome": "Diretoria", "codigo": "DIRETORIA"},
				{"id": 2, "nome": "Coordenadoria", "codigo": "COORDENADORIA"},
				{"id": 3, "nome": "Gerência", "codigo": "GERENCIA"}
			];

			$scope.tipoSetor = {
				"SECRETARIA": {"id": 0, "nome": "Secretaria"},
				"DIRETORIA": {"id": 1, "nome": "Diretoria"},
				"COORDENADORIA": {"id": 2, "nome": "Coordenadoria"},
				"GERENCIA": {"id": 3, "nome": "Gerência"}
			};
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

			if($scope.campo === 'Sigla') {

				ordenacao = 'SIGLA';

			}

			if($scope.ordenacaoReversa === true) {

				ordenacao += '_DESC';

			} else {

				ordenacao += '_ASC';

			}

			return ordenacao;

		}

		$scope.listar = function(campo, ordenacaoReversa) {

			if(!$rootScope.verificarPermissao($rootScope.AcaoSistema.PESQUISAR_SETOR)) {

				$rootScope.setForbiddenMessageAndRedirectToLogin();

			}

			$scope.campo = campo;
			$scope.ordenacaoReversa = ordenacaoReversa;
			$scope.filtro.ordenacao = ordenacaoPadrao();
			$scope.filtro.tamanhoPagina = $scope.paginacao.tamanhoPagina;
			$scope.filtro.numeroPagina = $scope.paginacao.paginaAtual;

			setorService.listarPorFiltro($scope.filtro).then(
				function(response) {

					$scope.setores = response.data.pageItems;

					$scope.paginacao.atualizar(response.data.totalItems);

				},
				function(error) {

					$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

				}

			);

		};

		$scope.limparFiltro = function() {

			$scope.filtro.nome = null;
			$scope.filtro.sigla = null;
			$scope.filtro.tipo = null;

			$scope.listar();

		};

		$scope.irCadastro = function() {

			if($rootScope.verificarPermissao($rootScope.AcaoSistema.CADASTRAR_SETOR)) {

				$location.path('/setor/cadastro');

			} else {

				$rootScope.setForbiddenMessageAndRedirectToLogin();

			}

		};

		$scope.abrirModalExcluir = function (setor) {
			console.log(setor);

			if($rootScope.verificarPermissao($rootScope.AcaoSistema.REMOVER_SETOR)) {

				$scope.setorExcluir = setor;
				$rootScope.abrirModal('modalExcluir');

			} else {

				$rootScope.setUnauthorizedMessageAndRedirectToLogin();

			}
		};

		$scope.abrirModalAtivarDesativar = function (setor) {
			console.log(setor);

			if($rootScope.verificarPermissao($rootScope.AcaoSistema.ATIVAR_DESATIVAR_SETOR)) {

				$scope.setorAtivarDesativar = setor;
				$rootScope.abrirModal('modalAtivarDesativarSetor');

			} else {

				$rootScope.setUnauthorizedMessageAndRedirectToLogin();

			}
		};

		$scope.ativarSetor = function (idSetor) {

			if($rootScope.verificarPermissao($rootScope.AcaoSistema.ATIVAR_DESATIVAR_SETOR)) {

				$rootScope.fecharModal('modalAtivarDesativarSetor');

				setorService.ativarSetor(idSetor).then(

					function(response) {

						$rootScope.$broadcast('showMessageEvent', response.data.text, 'success');

						$scope.listar();

					},
					function(error) {

						$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

					}

				);


			} else {

				$rootScope.setUnauthorizedMessageAndRedirectToLogin();

			}
		};

		$scope.desativarSetor = function (idSetor) {

			if($rootScope.verificarPermissao($rootScope.AcaoSistema.ATIVAR_DESATIVAR_SETOR)) {

				$rootScope.fecharModal('modalAtivarDesativarSetor');

				setorService.desativarSetor(idSetor).then(

					function(response) {

						$rootScope.$broadcast('showMessageEvent', response.data.text, 'success');

						$scope.listar();

					},
					function(error) {

						$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

					}

				);


			} else {

				$rootScope.setUnauthorizedMessageAndRedirectToLogin();

			}
		};
		
		$scope.excluir = function(idSetor) {

			if($rootScope.verificarPermissao($rootScope.AcaoSistema.REMOVER_SETOR)) {

				console.log($scope.setorExcluir);
				console.log(idSetor);

				$rootScope.fecharModal('modalExcluir');

				setorService.excluir(idSetor).then(

					function(response) {

						$scope.limparFiltro();

						$rootScope.$broadcast('showMessageEvent', response.data.text, 'success');

					},
					function(error) {

						$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

					}

				);

			} else {

				$rootScope.setUnauthorizedMessageAndRedirectToLogin();

			}
		};

		init();

	});

})();
