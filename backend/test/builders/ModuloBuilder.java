package builders;

import models.Modulo;

import java.util.Date;

public class ModuloBuilder extends BaseModelBuilder<Modulo> {

	public ModuloBuilder() {

		super(new Modulo());

		padrao();

	}

	public ModuloBuilder(Modulo model) {

		super(model);

	}

	@Override
	protected ModuloBuilder padrao() {

		model.nome = "Módulo Teste Padrão";
		model.sigla = "MTP";
		model.url = "http://www.teste.com";
		model.ips = "127.0.0.1";
		model.descricao = "aushausahushuahuauhshaus";
		model.dataCadastro = new Date();
		model.ativo = true;
		model.cadastrarPerfis = false;
		model.oAuthClient = new OAuthClientBuilder().build();

		return this;

	}

	public ModuloBuilder comNome(String nome) {

		model.nome = nome;

		return this;
	}

	public ModuloBuilder comDescricao(String descricao) {

		model.descricao = descricao;

		return this;
	}

	public ModuloBuilder comSigla(String sigla) {

		model.sigla = sigla;

		return this;

	}

	public ModuloBuilder comURL(String url) {

		model.url = url;

		return this;

	}

	public ModuloBuilder comDataCadastro(Date dataCadastro) {

		model.dataCadastro = dataCadastro;

		return this;
	}

	public ModuloBuilder comStatusAtivo() {

		model.ativo = true;

		return this;

	}

	public ModuloBuilder comStatusInativo() {

		model.ativo = false;

		return this;

	}

}
