package builders;

import models.LinkAcesso;
import models.Perfil;
import models.Usuario;
import secure.AuthenticationService;
import secure.SHA512Generator;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class LinkAcessoBuilder extends BaseModelBuilder<LinkAcesso> {

	public LinkAcessoBuilder() {

		super(new LinkAcesso());

		padrao();

	}

	public LinkAcessoBuilder(LinkAcesso model) {

		super(model);

	}

	@Override
	protected LinkAcessoBuilder padrao() {

		this.model.redefinirSenha = false;

		return this;

	}

	public LinkAcessoBuilder comRedefinirSenha() {

		this.model.redefinirSenha = true;

		return this;

	}

	public LinkAcessoBuilder comUsuario(Usuario usuario){

		this.model.usuario = usuario;

		return this;

	}

	public LinkAcessoBuilder comToken(String token){

		this.model.token = token;

		return this;

	}

	public LinkAcessoBuilder comDataSolicitacao(Date data) {

		this.model.dataSolicitacao = data;

		return this;

	}

}
