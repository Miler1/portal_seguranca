package builders;

import models.Perfil;
import models.Permissao;

import java.util.Collections;
import java.util.Date;
import java.util.Random;

public class PerfilBuilder extends BaseModelBuilder<Perfil> {

	public PerfilBuilder() {

		super(new Perfil());
		padrao();

	}

	public PerfilBuilder(Perfil perfil) {

		super(perfil);

	}

	@Override
	protected PerfilBuilder padrao() {

		model.nome = "Perfil padrao " + gerarId();
		model.dataCadastro = new Date();
		model.permissoes = Collections.singletonList(new PermissaoBuilder().build());

		return this;

	}

	@Override
	public Perfil save() {

		for (Permissao permissao : model.permissoes) {

			if (permissao.id == null) {

				new PermissaoBuilder(permissao).save();

			}

		}

		return super.save();

	}

	public PerfilBuilder comId(Integer id) {

		model.id = id;

		return this;

	}

	public PerfilBuilder comNome(String nome) {

		model.nome = nome;

		return this;

	}

	public PerfilBuilder comNomeExecendoTamanhoMaximo() {

		model.nome = "";

		String letras = "ABCDEFGHIJKLMNOPQRSTUVYWXZ";

		Random random = new Random();

		int index = -1;

		StringBuilder nome = new StringBuilder();

		for(int i = 0; i <= 50; i++) {

			index = random.nextInt(letras.length());

			nome.append(letras.substring( index, index + 1 ));
		}

		model.nome = nome.toString();

		return this;

	}

	public PerfilBuilder semNome() {

		model.nome = null;

		return this;

	}

	public PerfilBuilder comPermissao(Permissao permissao) {

		model.permissoes = Collections.singletonList(permissao);

		return this;

	}

}
