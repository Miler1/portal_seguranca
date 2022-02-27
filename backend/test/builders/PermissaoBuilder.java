package builders;

import java.util.Date;

import models.Modulo;
import models.Permissao;

public class PermissaoBuilder extends BaseModelBuilder<Permissao> {

	public PermissaoBuilder() {

		super(new Permissao());
		padrao();

	}

	public PermissaoBuilder(Permissao p) {

		super(p);

	}

	@Override
	protected PermissaoBuilder padrao() {

		model.nome = "Permissao Teste";
		model.codigo = "permissaoTeste";
		model.dataCadastro = new Date();
		model.modulo = new ModuloBuilder().build();

		return this;

	}

	public PermissaoBuilder comModulo(Modulo modulo) {

		model.modulo = modulo;
		return this;

	}

	public PermissaoBuilder comNome(String nome) {

		model.nome = nome;
		return this;

	}

	public PermissaoBuilder comId(Integer id) {

		model.id = id;
		return this;

	}

	public PermissaoBuilder comCodigo(String codigo) {

		model.codigo = codigo;
		return this;

	}

	@Override
	public Permissao save() {

		if(model.modulo.id == null) {

			new ModuloBuilder(model.modulo).save();

		}

		return super.save();

	}

}
