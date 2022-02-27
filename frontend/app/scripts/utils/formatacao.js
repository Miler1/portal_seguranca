(function(){
	String.prototype.formatarCpfCnpj = function(){
		var v = this.toString().replace(/\D/g, "");
		if (v.length <= 11) { //CPF
			v = v.replace(/(\d{3})(\d)/, "$1.$2");
			v = v.replace(/(\d{3})(\d)/, "$1.$2");
			v = v.replace(/(\d{3})(\d{1,2})$/, "$1-$2");
		} else {
			v = v.replace(/^(\d{2})(\d)/, "$1.$2");
			v = v.replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3");
			v = v.replace(/\.(\d{3})(\d)/, ".$1/$2");
			v = v.replace(/(\d{4})(\d)/, "$1-$2");
		}
		return v;
	};
	var formatarNumero = function(number, casas){
		var numeros = number.toFixed(casas).toString().split(".");
		var esquerdo = numeros[0].replace(/\B(?=(\d{3})+(?!\d))/g, ".");
		var direito = numeros[1];
		return esquerdo + "," + direito;
	};

	String.prototype.formatarNumero = function(casas){
		casas = casas || 2;
		return formatarNumero(parseFloat(this), casas);
	};

	Number.prototype.formatarNumero = function(casas){
		casas = casas || 2;
		return formatarNumero(this, casas);
	};

	String.prototype.formatarTelefone = function(){
		return this.replace(/\D/g,"").replace(/^(\d{2})(\d)/g,"($1) $2");
	};


	String.prototype.capitalize = function() {
		return this.toLowerCase().replace(/(?:^|\s)\S/g, function(a) { return a.toUpperCase(); });
	};

	String.prototype.formatarCEP = function(){
		return this.replace(/\D/g,"").replace(/^(\d{5})(\d)/,"$1-$2");
	};


	String.prototype.deixarSomenteNumeros = function(){
		return this.toString().replace(/[-_./]/g,"");
	};

	String.prototype.isEmail = function() {
		var emailAddress = this.toString();
		var pattern = new RegExp(/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i);
		return pattern.test(emailAddress);
	};

	String.prototype.isCPF = function(){
		var cpf = this.toString().replace(/[-_./]/g,"");
		var soma;
		var resto;
		var i;
		if ((cpf === null) || (cpf === undefined) || (cpf.length !== 11) ||
			(cpf == "00000000000") || (cpf == "11111111111") ||
			(cpf == "22222222222") || (cpf == "33333333333") ||
			(cpf == "44444444444") || (cpf == "55555555555") ||
			(cpf == "66666666666") || (cpf == "77777777777") ||
			(cpf == "88888888888") || (cpf == "99999999999") ) {
			return false;
		}
		soma = 0;
		for (i = 1; i <= 9; i++) {
			soma += Math.floor(cpf.charAt(i-1)) * (11 - i);
		}
		resto = 11 - (soma - (Math.floor(soma / 11) * 11));
		if ((resto == 10) || (resto == 11)) {
			resto = 0;
		}
		if (resto != Math.floor(cpf.charAt(9))) {
			return false;
		}
		soma = 0;

		for (i = 1; i<=10; i++) {
			soma += cpf.charAt(i-1) * (12 - i);
		}
		resto = 11 - (soma - (Math.floor(soma / 11) * 11));
		if ((resto === 10) || (resto === 11)) {
			resto = 0;
		}
		if (resto != Math.floor(cpf.charAt(10))) {
			return false;
		}
		return true;
	};

	String.prototype.isCNPJ = function () {
		var cnpj = this.toString().replace(/[-_./]/g,"");
		if (cnpj === null || cnpj === undefined || cnpj.length != 14)
			return false;

		var i;
		var c = cnpj.substr(0,12);
		var dv = cnpj.substr(12,2);
		var d1 = 0;

		for (i = 0; i < 12; i++) {
			d1 += c.charAt(11-i)*(2+(i % 8));
		}

		if (d1 === 0) return false;

		d1 = 11 - (d1 % 11);

		if (d1 > 9) d1 = 0;
		if (dv.charAt(0) != d1) {
			return false;
		}

		d1 *= 2;

		for (i = 0; i < 12; i++) {
			d1 += c.charAt(11-i)*(2+((i+1) % 8));
		}

		d1 = 11 - (d1 % 11);

		if (d1 > 9) d1 = 0;
		if (dv.charAt(1) != d1){
			return false;
		}

		return true;
	};

	String.prototype.toSlug = function(){
		var str = this.replace(/^\s+|\s+$/g, ''); // trim
		str = str.toLowerCase();

		// remove accents, swap ñ for n, etc
		var from = "ãàáäâèéëêìíïîõòóöôùúüûñç·/_,:;";
		var to   = "aaaaaeeeeiiiiooooouuuunc------";
		for (var i=0, l=from.length ; i<l ; i++) {
			str = str.replace(new RegExp(from.charAt(i), 'g'), to.charAt(i));
		}

		str = str.replace(/[^a-z0-9 -]/g, '').replace(/\s+/g, '-').replace(/-+/g, '-');

		return str;
	};

	String.prototype.removeAcentos = function(palavra) {
		var comAcento = "áàãâäéèêëíìîïóòõôöúùûüçÁÀÃÂÄÉÈÊËÍÌÎÏÓÒÕÖÔÚÙÛÜÇ";
		var semAcento = "aaaaaeeeeiiiiooooouuuucAAAAAEEEEIIIIOOOOOUUUUC";
		var nova = "";

		for(var i=0; i< palavra.length; i++) {
			if (comAcento.search(palavra.substr(i,1)) >= 0) {

				nova += semAcento.substr(comAcento.search(palavra.substr(i,1)),1);
			} else {

				nova += palavra.substr(i,1);
			}
		}
		return nova;
	};

	String.prototype.truncar = function(n){

		if(this.indexOf(' ') === -1 || this.length <= n)
			return this.toString();

		var string = this.substr(0, n-1);

		if(string.indexOf(' ') !== -1)
			return string.substr(0, string.lastIndexOf(' ')) + ' ...';
		else
			return this.substr(0, this.indexOf(' ')) + ' ...';

	};

})();