var modulo = angular.module('appModule');

modulo.directive('pwCheck', function () {

	return {

		require: 'ngModel',
        scope: {
            pwCheck: "="
        },
		link: function (scope, element, attributes, ngModel) {

			ngModel.$validators.pwCheck = function(modelValue) {
				return modelValue == scope.pwCheck;
			};

			scope.$watch("pwCheck", function() {
				ngModel.$validate();
			});

		}

	};

});