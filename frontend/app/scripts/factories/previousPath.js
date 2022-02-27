(function() {
	
	angular.module('appModule').factory('previousPath', function($rootScope) {
	
		var lastPath = '';
		
		$rootScope.$on('$locationChangeSuccess', function (event, newPath, oldPath) {
			
			lastPath = oldPath.split('#')[1];

		});
		
		return {
			getLastPath: function() { return lastPath; }
		};

	});
		
})();
