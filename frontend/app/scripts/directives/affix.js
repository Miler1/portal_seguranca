'use strict';

angular.module('affix', [])

	.directive('affix', function($window, $location, $routeParams, debounce, dimensions) {

		var checkPosition = function(scope, el, options, affixed, unpin) {

			var scrollTop = window.pageYOffset;
			var scrollHeight = document.body.scrollHeight;
			var position = dimensions.offset.call(el[0]);
			var height = dimensions.height.call(el[0]);
			var offsetTop = options.offsetTop * 1;
			var offsetBottom = options.offsetBottom * 1;
			var reset = 'affix affix-top affix-bottom';
			var affix;

			if(unpin !== null && (scrollTop + unpin <= position.top)) {
				affix = false;
			} else if(offsetBottom && (position.top + height >= scrollHeight - offsetBottom)) {
				affix = 'bottom';
			} else if(offsetTop && scrollTop <= offsetTop) {
				affix = 'top';
			} else {
				affix = false;
			}

			if (affixed === affix) return;

			affixed = affix;
			unpin = affix === 'bottom' ? position.top - scrollTop : null;

			el.removeClass(reset).addClass('affix' + (affix ? '-' + affix : ''));

		};

		return {

			restrict: 'EAC',
			link: function postLink(scope, iElement, iAttrs) {

				var affixed;
				var unpin = null;

				angular.element($window).bind('scroll', function() {
					checkPosition(scope, iElement, iAttrs, affixed, unpin);
				});

				angular.element($window).bind('click', function() {

					setTimeout(function() {
						checkPosition(scope, iElement, iAttrs, affixed, unpin);
					}, 1);

				});

			}

		};

	})

	.provider('dimensions', function() {

		this.$get = function() {
			return this;
		};

		this.offset = function() {

			if(!this) return;

			var box = this.getBoundingClientRect();
			var docElem = this.ownerDocument.documentElement;

			return {
				top: box.top + window.pageYOffset - docElem.clientTop,
				left: box.left + window.pageXOffset - docElem.clientLeft
			};

		};

		this.height = function(outer) {

			var computedStyle = window.getComputedStyle(this);
			var value = this.offsetHeight;

			if(outer) {
				value += parseFloat(computedStyle.marginTop) + parseFloat(computedStyle.marginBottom);
			} else {
				value -= parseFloat(computedStyle.paddingTop) + parseFloat(computedStyle.paddingBottom) + parseFloat(computedStyle.borderTopWidth) + parseFloat(computedStyle.borderBottomWidth);
			}

			return value;

		};

		this.width = function(outer) {

			var computedStyle = window.getComputedStyle(this);
			var value = this.offsetWidth;

			if(outer) {
				value += parseFloat(computedStyle.marginLeft) + parseFloat(computedStyle.marginRight);
			} else {
				value -= parseFloat(computedStyle.paddingLeft) + parseFloat(computedStyle.paddingRight) + parseFloat(computedStyle.borderLeftWidth) + parseFloat(computedStyle.borderRightWidth);
			}

			return value;

		};

	})

	.constant('debounce', function(fn, wait) {

		var timeout, result;

		return function() {

			var context = this, args = arguments;

			var later = function() {
				timeout = null;
				result = fn.apply(context, args);
			};

			clearTimeout(timeout);
			timeout = setTimeout(later, wait);

			return result;

		};

	});

