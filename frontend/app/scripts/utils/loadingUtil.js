(function($) {

	var LoadingUtil = {};
	var countServices = 0;

	LoadingUtil.show = function(data, headersGetter) {

		// jQuery BlockUI Plugin - http://malsup.com/jquery/block/
		$.blockUI({
			message: '<img src="images/loading.gif">',
			baseZ: 10000,
			css: {
				border: 'none',
				backgroundColor: "transparent",
				top:"50%",
				left: "50%",
				marginTop: "-51px",
				marginLeft: "-51px",
				width : "102px"
			}
		});

		return data;
	};

	LoadingUtil.hide = function() {
		$.unblockUI();
	};

	exports.LoadingUtil = LoadingUtil;

})(jQuery);