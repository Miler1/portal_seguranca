(function() {

	var modulo = angular.module('appModule');

	modulo.controller('CadastroModuloUmController', function($scope, config, moduloService, $rootScope, $location, moduloInfo, sessionStorage, constantes) {

		function init() {

			if($rootScope.verificarPermissao($rootScope.AcaoSistema.CADASTRAR_MODULO)) {

				initUploadArea();

			} else {

				$rootScope.setForbiddenMessageAndRedirectToLogin();

			}

		}

		$rootScope.verifyMessages();

		function initUploadArea() {

			var dropzoneContainer = null;
			var dropzone = null;

			var componentId = "div#dropzone-upload";
			dropzoneContainer = $(componentId);

			if(dropzone) {

				dropzone.destroy();

			}

			dropzone = new Dropzone(componentId, {
				paramName : 'file',
				url: config.BASE_URL + 'arquivo/upload',
				dictResponseError : "Houve um problema ao realizar o upload do arquivo.",
				clickable : ['#dropzone-upload','.dropzone-icon', '.clickable'],
				acceptedFiles : '.eu',
				uploadMultiple : false,
				maxFilesize : 2, //2MB
				dictInvalidFileType : 'Este tipo de arquivo não é permitido.',
				dictFileTooBig : 'O arquivo é muito grande. Tamanho máximo permitido: {{maxFilesize}}MB.',
				previewTemplate : "<div class=\"dz-preview dz-file-preview clickable\">\n   <div class=\"dz-mensagem-erro\"><span class=\"no-clickable\" data-dz-errormessage></span></div>\n</div>"
			});

			dropzone.on("addedfile", function(file) {

				$scope.arquivoAdicionado = false;

				$('.dz-error').remove();

			});

			dropzone.on("success", function(file, response){

				$scope.file = file;
				$scope.arquivoAdicionado = true;
				$scope.key = response;

				var arquivo = {
					name: file.name,
					key: $scope.key
				};

				sessionStorage.removeObject('modulo');

				$scope.$digest();

			});

			$scope.clearInput = function() {

				$scope.arquivoAdicionado = false;

			};

		}

		$scope.validarArquivo = function() {

			if($rootScope.verificarPermissao($rootScope.AcaoSistema.CADASTRAR_MODULO)) {

				if(!$scope.arquivoAdicionado) {

					$rootScope.$broadcast('showMessageEvent', 'É necessário enviar um arquivo de configuração.', 'danger');

					return;

				}

				moduloService.validarArquivo($scope.key).then(
					function(response) {

						var modulo = response.data;

						modulo.chaveArquivo = $scope.key;

						moduloInfo.setModulo(modulo);

						moduloInfo.setPassoUm();

						$location.path('modulo/cadastro/2');

					},
					function(error) {

						$rootScope.$broadcast('showMessageEvent', error.data, 'danger');

					}

				);

			} else {

				$rootScope.setForbiddenMessageAndRedirectToLogin();

			}
	};

		$scope.voltar = function() {

			$location.path('/modulo/gestao');

		};

		init();

	});

})();
