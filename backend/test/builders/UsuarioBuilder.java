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

public class UsuarioBuilder extends BaseModelBuilder<Usuario>{

	private Set<Perfil> perfis = new HashSet<>();
	public static final String SENHA_PADRAO = "123456";

	public UsuarioBuilder() {

		super(new Usuario());
		padrao();

	}

	public UsuarioBuilder(Usuario model) {

		super(model);

	}

	@Override
	protected UsuarioBuilder padrao() {

		model.dataCadastro = new Date();
		model.ativo = true;
		model.login = "19847388105";
		model.email = "testao@gmail.com";
		this.perfis = new HashSet<>();
		this.perfis.add(new PerfilBuilder().build());

		return this;

	}

	public UsuarioBuilder comLogin(String login){

		model.login = login;

		return this;

	}

	public UsuarioBuilder comEmail(String email) {

		model.email = email;

		return this;

	}

	public UsuarioBuilder comDataCadastro(Date dataCadastro){

		model.dataCadastro = dataCadastro;

		return this;

	}

	public UsuarioBuilder comStatusAtivo(Boolean status){

		model.ativo = status;

		return this;

	}

	public UsuarioBuilder comSenha(String senha){

		model.senha = SHA512Generator.generateValue(senha);

		return this;

	}

	public UsuarioBuilder comPerfil(Perfil perfil) {

		this.perfis = new HashSet<>();
		this.perfis.add(perfil);

		return this;

	}

	public UsuarioBuilder semPerfil() {

		this.perfis = new HashSet<>();

		return this;

	}

	public UsuarioBuilder comPerfis(Perfil ... perfis ) {

		this.perfis = new HashSet<>();
		this.perfis.addAll(Arrays.asList(perfis));

		return this;

	}

	public UsuarioBuilder comSenhaInvalida() {

		model.senha = "123";

		return this;

	}

	public UsuarioBuilder comPerfilSelecionado() {

		model.perfilSelecionado = this.perfis.iterator().next();

		return this;

	}

	public UsuarioBuilder semLogin() {

		model.login = null;

		return this;

	}

	public UsuarioBuilder semEmail() {

		model.email = null;

		return this;

	}

	public UsuarioBuilder comLoginRandomico() {

		Random randomico = new Random();
		Long login = randomico.nextLong();

		if(login < 0) {

			login = login * -1;

		}

		this.model.login = login.toString();

		return this;

	}

	@Override
	public Usuario save() {

		if(model.perfis == null) {

			model.perfis = new HashSet<>();

		}

		for (Perfil p : this.perfis) {

			if(p.id == null) {

				model.perfis.add(new PerfilBuilder(p).save());

			} else {

				model.perfis.add(p);

			}

		}

		return super.save();

	}

	@Override
	public Usuario build() {

		if(model.perfis == null) {

			model.perfis = new HashSet<>();

		}

		for (Perfil p : this.perfis) {

			if(p.id == null) {

				model.perfis.add(new PerfilBuilder(p).build());

			} else {

				model.perfis.add(p);

			}

		}

		return super.build();

	}

	public UsuarioBuilder semSenha() {

		this.model.senha = null;

		return this;

	}

	public UsuarioBuilder comLinkAcesso(LinkAcesso linkAcesso) {

		this.model.linkAcesso = linkAcesso;

		return this;

	}

}
