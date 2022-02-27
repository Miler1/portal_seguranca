
(function() {

		var module = angular.module('appModule');

		module.directive('inputMaskCpfCnpj', function() {
			return {
				restrict: 'A',
				link: function(scope, element, attrs) {

					var model = attrs.ngModel;

					scope.$watch(model, function(newValue) {

						if(newValue) {

							if(newValue.length < 4) {

								scope.mask = "?9?9?9?9?9?9?9?9?9?9?9?9?9?9";

							} else if(newValue.length >= 4 && newValue.length < 7) {

								scope.mask = "?9?9?9.?9?9?9?9?9?9?9?9?9?9?9";

							} else if(newValue.length >= 7 && newValue.length < 10) {

								scope.mask = "?9?9?9.?9?9?9.?9?9?9?9?9?9?9?9";

							} else if(newValue.length >= 10 && newValue.length < 12) {

								scope.mask = "?9?9?9.?9?9?9.?9?9?9-?9?9?9?9?9";

							} else {

								if(newValue.length == 12) {

									scope.mask = "?9?9.?9?9?9.?9?9?9/?9?9?9?9?9?9";

								} else {

									scope.mask = "?9?9.?9?9?9.?9?9?9/?9?9?9?9-?9?9";

								}

							}

						} else {

							scope.mask = "?9?9?9?9?9?9?9?9?9?9?9?9?9?9?9?9";

						}

					});

				}

			};

		});

	})();