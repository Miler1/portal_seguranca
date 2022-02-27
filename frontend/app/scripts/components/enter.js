(function() {

	var modulo = angular.module('appModule');

	modulo.directive('onEnter', function() {

		return function(scope, element, attrs) {

			element.bind("keydown keypress", function(event) {

				//key 13 -> Enter
				if (event.which === 13) {

					scope.$apply(function() {
						scope.$eval(attrs.onEnter);
					});

					event.preventDefault();

				}

			});

		};
		
	});

})();
