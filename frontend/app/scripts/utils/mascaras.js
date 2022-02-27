(function($){


	var somenteNumero = function(e,obj,isDecimal){
		if ([e.keyCode||e.which]==8) //this is to allow backspace
			return true;

		if(isDecimal){
			if ([e.keyCode||e.which]==110 || [e.keyCode||e.which]==188) {
				var val = obj.value;
				if(val.indexOf(",") > -1){
					if(e.preventDefault)  e.preventDefault(); else e.returnValue = false;
					return false;
				}
				return true;
			}
		}
		if ([e.keyCode||e.which] < 48 || [e.keyCode||e.which] > 57)
			if ([e.keyCode||e.which] < 96| [e.keyCode||e.which] > 105)
				if(e.preventDefault)  e.preventDefault(); else e.returnValue = false;
		return true;
	};

	var mascara = function(formato, arg, bloquearCopiarColar){

		return function(scope, element, attrs) {

			// Evitando Ctrl C e Ctrl V nos elementos com máscara
			if (bloquearCopiarColar) {
				$(element).bind("cut copy paste",function(e) {
			        e.preventDefault();
			    });
			}
			
			var options = {

				translation: {
					'a': {pattern:/[A-Za-z0-9]/},
					'h': {pattern: /[A-Za-z]/}
				},

				onComplete: function(value) {

					scope.$eval(attrs.ngModel + " = '"+ value +"'");
					scope.$digest(); //avisar o angular que ocorreu mudanças

					if (typeof arg == 'function') {
						arg(scope);
					}
				},

				onChange: function(value) {
					scope.$eval(attrs.ngModel + " = '"+ value +"'");
				},

				onInvalid: function(value) {
					$(element).removeClass("ng-valid").addClass("ng-invalid");
					$(element).attr("title", "Máscara Inválida para este campo.");
				}
			};

			if (typeof arg === 'string')
				options.placeholder = arg;

			$(element).mask(formato, options);

			// Evento auxiliar de máscara inválida por tamanho
			$(element).focusout(function () {

				// Verificando se o tamanho do texto digitado até o momento é menor que a 
				// máscara sem os caracteres opcionais (representados pelo digito 9)
				if (this.value.length > 0 && this.value.length < formato.replace(/9/g,'').length) {
					$(element).removeClass("ng-valid").addClass("ng-invalid");
					$(element).attr("title", "Máscara Inválida para este campo.");
					scope.$digest();
				} else {
					$(element).removeClass("ng-invalid").addClass("ng-valid");
					$(element).attr("title", "");
					scope.$digest();
				}
			});
		};
	};

	angular.module('mascaras', [])

		.directive('data', function() {
			return mascara("00/00/0000");
		})

		.directive('sigla', function() {
			return mascara("hhh");
		})

		.directive('cep', function() {
			return mascara("00000-000");
		})

		.directive('cpf', function() {
			return mascara("000.000.000-00");
		})

		.directive('cpfcnpj', function() {
			return mascara("00000000000000");
		})

		.directive('cnpj', function() {
			return mascara("00.000.000/0000-00");
		})

		.directive('telefone', function() {
			return mascara("(00) 00000000?0");
		})

		.directive('recibo', function() {
			//RJ-3300100-1ADC.7DF2.C2BA.463B.8867.AAB1.3048.478E
			//$('input[recibo]').css("text-transform", "uppercase");
			return mascara("hh-0000000-aaaa.aaaa.aaaa.aaaa.aaaa.aaaa.aaaa.aaaa");
		// }).directive('datepicker', function() {
		// 	return datepicker();
		})

		.directive('decimal', function(){
			return function(scope, element, attrs){
				$(element).keydown(function(event){
					var isDecimal =  attrs.decimal.toUpperCase() === "SIM" ? true : false;
					return somenteNumero(event, this,isDecimal);
				});
			};
		})

		.directive('codigoassentamento', function() {
			return mascara("hh0000000");
		})

		.directive('uppercased', function() {
			return {
				require: 'ngModel',
				link: function(scope, element, attrs, modelCtrl) {
					modelCtrl.$parsers.push(function(input) {
						return input ? input.toUpperCase() : "";
					});
					element.css("text-transform", "uppercase");
				}
			};
		})

		.filter('nomearquivo', function() {

			return function(nomeParametro) {

				var nomeArquivo = nomeParametro ? nomeParametro : "";

				if(nomeArquivo && nomeArquivo.length > 20) {
					return (nomeArquivo.substring(0,10) + "..." + nomeArquivo.substring(0 + nomeArquivo.length - 6, nomeArquivo.length));
				}

				return nomeArquivo;

			};
		});

})(jQuery);