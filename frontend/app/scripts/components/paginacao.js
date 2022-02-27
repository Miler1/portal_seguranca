(function() {

	// Model que gerencia o componente de paginação.
	// Recebe como parâmetro o tamanho da pagina e a função a ser chamada ao
	// clicar em uma pagina. É necessário chamar a função "atualizar" explicitamente 
	// sempre que mudar de página, para que o componente seja atualizado.
	var Paginacao = function(tamanhoPagina, onPaginaClick) {

		this.tamanhoPagina = tamanhoPagina;
		this.onPaginaClick = onPaginaClick;
		
		this.totalItens = 0;
		this.qtdPaginas = 1;
		this.paginaAtual = 1;
		this.intervaloItens = { inicio: 0, fim: 0 };

	};

	Paginacao.prototype.atualizar = function (totalItens) {

		this.totalItens = totalItens;
		this.qtdPaginas = Math.ceil(this.totalItens / this.tamanhoPagina);
		this.intervaloItens = [];
		this._configurarIntervaloItens();

	};

	Paginacao.prototype._configurarIntervaloItens = function () {

		var itensPaginasAnteriores = this.tamanhoPagina * (this.paginaAtual - 1);
		var isUltimaPagina = this.paginaAtual === this.qtdPaginas;

		this.intervaloItens.inicio = itensPaginasAnteriores + 1;

		if (this.paginaAtual == this.qtdPaginas) { // se ultima pagina

			this.intervaloItens.fim = this.totalItens;

		} else {

			this.intervaloItens.fim = itensPaginasAnteriores + this.tamanhoPagina;

		}

	};

	// Disponibilizando classe
	exports.Paginacao = Paginacao;

})();
