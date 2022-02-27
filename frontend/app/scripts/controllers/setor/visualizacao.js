(function() {

    var modulo = angular.module('appModule');

    modulo.controller('VisualizacaoSetorController', function($scope, config, setorService, $rootScope, $routeParams, $location) {

        function init() {

            $scope.visualizarSetor($routeParams.id);

        }

        $scope.voltar = function() {

            $location.path('/setor/gestao');

        };

        $scope.visualizarSetor = function(idSetor) {

            if(!$rootScope.verificarPermissao($rootScope.AcaoSistema.VISUALIZAR_SETOR)) {

                $rootScope.setForbiddenMessageAndRedirectToLogin();

            }

            setorService.buscar(idSetor).then(

                function(response) {

                    $scope.setor = response.data;

                },
                function(error) {

                    $rootScope.$broadcast('showMessageEvent', error.data, 'danger');

                }

            );

        };
        
        init();

    });

})();