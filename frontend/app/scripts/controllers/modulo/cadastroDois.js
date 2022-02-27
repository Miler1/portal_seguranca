(function() {

	var modulo = angular.module('appModule');

	modulo.controller('CadastroModuloDoisController', function($scope, config, moduloService, $rootScope, $location, moduloInfo, sessionStorage, constantes) {

		function init() {

			$rootScope.verifyMessages();

			if(moduloInfo.getPassoUm()) {

				$scope.modulo = sessionStorage.getObject('modulo');

			}

			if($rootScope.previousPath.getLastPath() == $location.path()) {

				$scope.modulo = sessionStorage.getObject('modulo');

			}

			if(!$scope.modulo) {

				$scope.modulo = moduloInfo.getModulo();

				if($scope.modulo) {

					sessionStorage.setObject('modulo', $scope.modulo);

				} else {

					$location.path('modulo/gestao');

				}

			}

			$scope.combobox = {
				perfis: $scope.modulo.perfis
			};

			$scope.labels = {
				"itemsSelected": "perfis",
				"selectAll": "Todos",
				"unselectAll": "Remover selecionados",
				"select": "Todos"
			};

			$scope.modulo.perfis = _.sortBy($scope.modulo.perfis, 'name');

			$scope.createPerfilList($scope.modulo.perfis);

			if($rootScope.verificarPermissao($rootScope.AcaoSistema.CADASTRAR_MODULO)) {

				initUploadArea();

			} else {

				$rootScope.setForbiddenMessageAndRedirectToLogin();

			}

		}

		function initUploadArea() {

			$scope.croppedImage = null;
			$scope.blobCroppedImage = null;
			var dropzoneContainer = null;
			var dropzone = null;

			var componentId = "div#dropzone-upload-image";
			dropzoneContainer = $(componentId);

			if(dropzone) {

				dropzone.destroy();

			}

			Dropzone.autoDiscover = false;

			dropzone = new Dropzone(componentId, {
				paramName : 'file',
				url: config.BASE_URL + 'arquivo/upload',
				clickable: ['#dropzone-upload-image', '.message', '#upload-img', '.update-image'],
				acceptedFiles : 'image/*',
				uploadMultiple : false,
				maxFilesize : 4, //4MB
				autoProcessQueue: false,
				previewTemplate : "<div class=\"dz-preview dz-file-preview\">\n </div>\n</div>"
			});

			dropzone.on("addedfile", function(file) {

				if(file.type.split('/')[0] != 'image' || file.size > 4000000) {

					$rootScope.$broadcast('showMessageEvent', 'Arquivo inválido.', 'danger');

					return;

				}

				$scope.clearInput();

				var reader = new FileReader();

				reader.onload = function(evt) {

					$scope.$apply(function($scope) {

						$scope.imageToCrop = evt.target.result;

					});

				};

				$('#modalCorteImagem').modal({backdrop:'static', keyboard: false});

				reader.readAsDataURL(file);

				dropzone.removeFile(file);

			});

			dropzone.on("success", function(file, response) {

				$scope.chaveLogotipo = response;

				$scope.$digest();

			});

			$scope.clearInput = function() {

				$scope.imageToCrop = null;
				$scope.croppedImage = null;
				$scope.blobCroppedImage = null;

				dropzone.enable();

			};

			$('#modalCorteImagem').on('shown.bs.modal', function() {

				$scope.cortar = false;

			});

			$('#modalCorteImagem').on('hidden.bs.modal', function() {

				if($scope.cortar) {

					dropzone.processFile($scope.blobCroppedImage);

					$scope.image = $scope.croppedImage;

					$scope.arquivoAdicionado = true;

					$scope.clearInput();

					$scope.$apply();

				}

			});

			$scope.cortarImagem = function() {

				$scope.cortar = true;

				$('#modalCorteImagem').modal('hide');

			};

		}

		$scope.createPerfilList = function(perfis){

			_.each($scope.modulo.permissoes, function(permissao) { permissao.perfilChecked = []; });

			_.each(perfis, function(perfilList) {

				_.each($scope.modulo.permissoes, function(permissao) {

					if(_.find(permissao.perfis, function (perfil) { return perfilList.nome == perfil.nome; })){

						permissao.perfilChecked.push(true);

					} else {

						permissao.perfilChecked.push(false);

					}

				});

			});

		};

		$scope.$watch('combobox.perfis', function(newValue) {

			if(newValue) {

				if(newValue.length > 0) {

					$scope.createPerfilList(newValue);

					return;

				}

			}

			$scope.createPerfilList($scope.modulo.perfis);

		}, true);

		$scope.salvarModulo = function() {

			if($rootScope.verificarPermissao($rootScope.AcaoSistema.CADASTRAR_MODULO)) {

				if(!$scope.chaveLogotipo) {

					$rootScope.$broadcast('showMessageEvent', 'É necessário enviar uma imagem.', 'danger');

					return;

				}

				$scope.modulo.chaveLogotipo = $scope.chaveLogotipo;

				moduloService.salvarModulo($scope.modulo).then(

					function(response) {

						$scope.oAuthClient = response.data.unHashedOAuthClient;

						sessionStorage.removeObject('modulo');

						$rootScope.abrirModal('modalCadastroModulo');

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

			$location.path('/modulo/cadastro/1');

			sessionStorage.removeObject('modulo');

		};

		$scope.irGestaoModulos = function() {

			$rootScope.fecharModal('modalCadastroModulo');
			$('body').removeClass('modal-open');
			$('.modal-backdrop').remove();

			$location.path('/modulo/gestao');

		};

		init();

	});

})();
