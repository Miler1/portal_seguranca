package builders;

import models.permissoes.AcaoSistema;

public class AcaoSistemaBuilder extends BaseModelBuilder<AcaoSistema> {

	public static final Integer FIRST_ID_FAKE = 1000;
	public static Integer id = FIRST_ID_FAKE;

	public AcaoSistemaBuilder() {

		super(new AcaoSistema());
		padrao();

	}

	public AcaoSistemaBuilder(AcaoSistema acao) {

		super(acao);
	}

	@Override
	protected AcaoSistemaBuilder padrao() {

		model.id = gerarId();
		model.descricao = "ACAO DE TESTE";

		return this;

	}

	protected Integer gerarId() {

		return id++;

	}

	public AcaoSistemaBuilder comPermissao(){

		model.permissao = "teste" + model.id;

		return this;

	}

}
