package models;

import com.google.gson.JsonObject;
import exceptions.ValidationException;
import models.permissoes.AcaoSistema;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.db.jpa.JPA;
import portalSema.PessoaPortalSemaVO;
import secure.IAuthenticatedUser;
import secure.authorization.Action;
import secure.authorization.Permissible;
import services.ExternalPessoaService;
import utils.Config;
import utils.ListUtil;
import utils.ValidationUtil;
import validators.UniqueIfNotRemoved;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(schema = "portal_seguranca", name = "usuario")
//@org.hibernate.annotations.Filter(name = "entityRemovida")
//@Filter(name = "entityRemovida")
public class Usuario extends GenericModel implements IAuthenticatedUser, Permissible {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_usuario")
	@SequenceGenerator(name = "sq_usuario", sequenceName = "portal_seguranca.sq_usuario", allocationSize = 1)
	public Integer id;

	@Required(message = "usuarios.validacao.login.req")
	@MaxSize(value = 20)
	@UniqueIfNotRemoved(message = "usuarios.validacao.login.unico")
	public String login;

//	@Required(message = "usuarios.validacao.email.req")
	@MaxSize(value = 50)
//	@UniqueIfNotRemoved(message = "usuarios.validacao.email.unico")
	public String email;

	@Column(name = "data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dataCadastro;

	@Column(name="ativo")
	public Boolean ativo;

	@MinSize(value = 6, message = "usuarios.validacao.usuario.tamanhoSenha")
	public String senha;

	@Column(name = "data_atualizacao")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dataAtualizacao;

	@Required(message="usuarios.validacao.perfil.req")
	@ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
	@JoinTable(schema = "portal_seguranca", name = "perfil_usuario",
			joinColumns = @JoinColumn(name = "id_usuario", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "id_perfil", referencedColumnName = "id"))
	public Set<Perfil> perfis;

	@OneToOne(mappedBy = "usuario")
	public AgendaDesativacao agendaDesativacao;

	@OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
	public LinkAcesso linkAcesso;

	@Column(name="removido")
	public Boolean removido;

	@ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
	@JoinTable(schema = "portal_seguranca", name = "usuario_setor",
			joinColumns = @JoinColumn(name = "id_usuario"),
			inverseJoinColumns = @JoinColumn(name = "id_setor"))
	public Set<Setor> setores;

	@Column(name="pessoa_cadastro")
	public Boolean pessoaCadastro;

	@Transient
	public Perfil perfilSelecionado;

	@Transient
	public Setor setorSelecionado;

	@Transient
	private List<Integer> permittedActionsIds;

	@Transient
	public Boolean temSenha;

	@Transient
	public Set<Modulo> modulosPermitidos;

	@Transient
	public Integer pessoaId;

	@Transient
	public String nomeUsuario;

	@Transient
	public String razaoSocialUsuario;

	@Transient
	public String sessionKeyEntradaUnica;

	@Transient
	public String nome;

	@Transient
	public Boolean portalSema;

	@Transient
	public PessoaPortalSemaVO pessoaPortalSemaVO;

	public Usuario() {

		super();

		this.dataCadastro = new Date();
		this.ativo = false;
		this.removido = false;

	}

	public Usuario(PessoaPortalSemaVO pessoaPortalSemaVO) {

		super();

		this.login = pessoaPortalSemaVO.pessoa.cpf;

		if(pessoaPortalSemaVO.pessoa.contato != null)
			this.email = pessoaPortalSemaVO.pessoa.contato.email;

		this.dataCadastro = new Date();
		this.ativo = true;
		this.removido = false;
		this.pessoaCadastro = false;

	}

	public Usuario(PessoaPortalSemaVO pessoaPortalSemaVO, Usuario usuario) {

		super();

		this.login = pessoaPortalSemaVO.pessoa.cpf;
		this.id = usuario.getId();

		if(pessoaPortalSemaVO.pessoa.contato != null) {
			this.email = pessoaPortalSemaVO.pessoa.contato.email;
		}

		this.nomeUsuario = pessoaPortalSemaVO.pessoa.nome;

		this.dataCadastro = pessoaPortalSemaVO.pessoa.created_at;
		this.dataAtualizacao = pessoaPortalSemaVO.pessoa.updated_at;

		this.pessoaPortalSemaVO = pessoaPortalSemaVO;

		this.ativo = usuario.ativo;
		this.removido = usuario.removido;
		this.pessoaCadastro = usuario.pessoaCadastro;
		this.perfilSelecionado = usuario.perfilSelecionado;

	}

	public String getNome() {

		return this.nome != null ? this.nome : this.nomeUsuario != null ? this.nomeUsuario : this.razaoSocialUsuario;

	}

	public Boolean getTemSenha() {

		return senha != null;

	}

	@Override
	public Usuario save() {

		ValidationUtil.validate(this);

//		if (this.pessoaCadastro) {
		Perfil.validateUsuarioPerfis(this.perfis);
//		}

		List<Perfil> perfis = Perfil.find("select p from " + Perfil.class.getSimpleName() + " p where p.id in (:ids)")
				.setParameter("ids", ListUtil.getIds(this.perfis))
				.fetch();

		if (this.setores != null && this.setores.size() > 0) {

			List<Setor> setores = Setor.find("select s from " + Setor.class.getSimpleName() + " s where s.id in (:ids)")
					.setParameter("ids", ListUtil.getIds(this.setores))
					.fetch();

			Set<Setor> hsetores = new HashSet<>();
			hsetores.addAll(setores);

			this.setores = hsetores;
		}

		this.perfis.clear();
		this.perfis.addAll(perfis);
//		this.ativo = false;
//
//		sendFirstAccessMail();

		super.save();

		return this;

	}

	// public void sendFirstAccessMail() {

	// 	this.linkAcesso = new LinkAcesso(this, false);

	// 	String url = Play.configuration.getProperty("application.baseUrl") + "#/acesso?token=" + this.linkAcesso.token;

	// 	super.save();

	// 	try {

	// 		if (!Emails.primeiroAcesso(this, url).get()) {

	// 			throw new PortalSegurancaException();

	// 		}

	// 	} catch(PortalSegurancaException | InterruptedException | ExecutionException e) {

	// 		Logger.error(e, e.getMessage());

	// 		new ReenvioEmail(e.getMessage(), this, url, ReenvioEmail.TipoEmail.PRIMEIRO_ACESSO).save();

	// 		Http.Response.current().status = Http.StatusCode.ACCEPTED;

	// 	}

	// }

	// public void sendResetPasswordMail() {

	// 	this.linkAcesso = new LinkAcesso(this, true);

	// 	String url = Play.configuration.getProperty("application.baseUrl") + "#/acesso?token=" + this.linkAcesso.token;

	// 	super.save();

	// 	try {

	// 		if(!Emails.redefinirSenha(this, url).get()) {

	// 			throw new PortalSegurancaException();

	// 		}

	// 	} catch(PortalSegurancaException | InterruptedException | ExecutionException e) {

	// 		Logger.error(e, e.getMessage());

	// 		new ReenvioEmail(e.getMessage(), this, url, ReenvioEmail.TipoEmail.REDEFINIR_SENHA).save();

	// 		Http.Response.current().status = Http.StatusCode.ACCEPTED;

	// 	}

	// }

	// public Usuario createPassword(String senha) throws Exception {

	// 	if(this.senha != null) {

	// 		throw new ValidationException().userMessage("usuarios.criarSenhaPrimeiroAcesso.senha");

	// 	}

	// 	this.senha = SHA512Generator.generateValue(senha);
	// 	this.ativo = true;
	// 	this.linkAcesso = null;

	// 	super.save();

	// 	try {

	// 		if(!Emails.criarSenhaPrimeiroAcesso(this).get()) {

	// 			throw new PortalSegurancaException();

	// 		}

	// 	} catch(PortalSegurancaException | InterruptedException | ExecutionException e) {

	// 		Logger.error(e, e.getMessage());

	// 		new ReenvioEmail(e.getMessage(), this, null, ReenvioEmail.TipoEmail.CRIAR_SENHA_PRIMEIRO_ACESSO).save();

	// 	}

	// 	return this;

	// }

	// public Usuario resetPassword(String senha) throws Exception {

	// 	if(this.senha == null || !this.ativo) {

	// 		throw new ValidationException().userMessage("usuarios.redefinirAtivoReq");

	// 	}

	// 	this.senha = SHA512Generator.generateValue(senha);
	// 	this.linkAcesso = null;

	// 	super.save();

	// 	try {

	// 		if(!Emails.senhaRedefinida(this).get()) {

	// 			throw new PortalSegurancaException();

	// 		}

	// 	} catch(PortalSegurancaException | InterruptedException | ExecutionException e) {

	// 		Logger.error(e, e.getMessage());

	// 		new ReenvioEmail(e.getMessage(), this, null, ReenvioEmail.TipoEmail.SENHA_REDEFINIDA).save();

	// 	}

	// 	return this;

	// }

	public static Usuario validateToken(String token) {

		return LinkAcesso.findByToken(token).usuario;

	}

	public Usuario update(Usuario usuarioAtualizacao) {

		ValidationUtil.validate(usuarioAtualizacao);

		Perfil.validateUsuarioPerfis(usuarioAtualizacao.perfis);

		this.dataCadastro = usuarioAtualizacao.dataCadastro;
		this.dataAtualizacao = new Date();
		this.email = usuarioAtualizacao.email;
		this.pessoaCadastro = usuarioAtualizacao.pessoaCadastro;
		this.perfis.clear();

		if(usuarioAtualizacao.perfis!= null && !usuarioAtualizacao.perfis.isEmpty()) {

			List<Perfil> perfis = Perfil.find("select p from " + Perfil.class.getSimpleName() + " p where p.id in (:ids)")
					.setParameter("ids", ListUtil.getIds(usuarioAtualizacao.perfis))
					.fetch();

			this.perfis.addAll(perfis);

		}

		this.setores.clear();

		if (usuarioAtualizacao.setores != null && usuarioAtualizacao.setores.size() > 0) {

			List<Setor> setores = Setor.find("select s from " + Setor.class.getSimpleName() + " s where s.id in (:ids)")
					.setParameter("ids", ListUtil.getIds(usuarioAtualizacao.setores))
					.fetch();

			this.setores.addAll(setores);
		}

		return super.save();

	}

	public static Usuario findByLogin(String login) {

		return Usuario.find("login", login).first();

	}

	public Usuario activate() {

		//primeiro acesso só é efetivado após o usuário cadastrar os dados complementares (pessoaCadastro == true)
		if(this.pessoaCadastro == false) {

			throw new ValidationException().userMessage("usuarios.ativo.primeiroAcesso.obrigatorio");

		}

		if(this.ativo) {

			throw new ValidationException().userMessage("usuarios.ativo.jaAtivo");

		}

		this.ativo = true;

		if(this.agendaDesativacao != null) {

			this.agendaDesativacao.delete();

		}

		return super.save();

	}

	public Usuario deactivate() {

		if(!this.ativo) {

			throw new ValidationException().userMessage("usuarios.ativo.jaDesativado");

		}

		this.ativo = false;

		return super.save();

	}

	@Override
	public Integer getId() {

		return this.id;

	}

	@Override
	public String getName() {

		return this.login;

	}

	@Override
	public String[] getRoles() {

		Set<String> roles = new HashSet<>();

		Perfil perfil = this.perfilSelecionado;

		for(Permissao permissao : perfil.permissoes) {

			roles.add(permissao.codigo);

		}

		return roles.toArray(new String[roles.size()]);

	}

	@Override
	public boolean hasRole(String role) {

		Perfil perfil = this.perfilSelecionado;

		for(Permissao permissao : perfil.permissoes) {

			if(permissao.codigo.equals(role)) {

				return true;

			}

		}

		return false;

	}

	@Override
	public List<Action> getAvailableActions() {

		List<Action> availableActions = new ArrayList<>();

		availableActions.add((Action) AcaoSistema.findById(AcaoSistema.CADASTRAR_USUARIO));
		availableActions.add((Action) AcaoSistema.findById(AcaoSistema.EDITAR_USUARIO));
		availableActions.add((Action) AcaoSistema.findById(AcaoSistema.REMOVER_USUARIO));
		availableActions.add((Action) AcaoSistema.findById(AcaoSistema.VISUALIZAR_USUARIO));
		availableActions.add((Action) AcaoSistema.findById(AcaoSistema.ATIVAR_DESATIVAR_USUARIO));

		return availableActions;

	}

	@Override
	public void setPermittedActionsIds(List<Integer> actionsIds) {

		this.permittedActionsIds = actionsIds;

	}

	@Override
	public List<Integer> getPermittedActionsIds() {

		return this.permittedActionsIds;

	}

	public Usuario remove() {

		if(this.removido) {

			throw new ValidationException().userMessage("usuarios.remocao.jaRemovido");

		}

		this.removido = true;

		return super.save();

	}

	public List<Permissao> findAllPermissoes() {

		List<Permissao> permissoes = new ArrayList<>();

		for(Perfil perfil : this.perfis) {

			permissoes.addAll(perfil.permissoes);

		}

		return permissoes;

	}

	public List<Perfil> getPerfisByModulo(Integer idModulo) {

		List<Perfil> perfis = new ArrayList<>();

		for (Perfil perfil : this.perfis) {

			if (perfil.isFromModulo(idModulo)) {

				perfis.add(perfil);

			} else if(perfil.isFromModulo(Config.ID_PORTAL) && perfil.nome.equals(Config.PERFIL_PUBLICO)) {

				perfis.add(perfil);

			}

		}

		return perfis;

	}

	public void getNomeByObject(JsonObject pessoa) {

		if(pessoa.get("nome") != null) {

			this.nomeUsuario = pessoa.get("nome").getAsString();

		}

	}

	public void getRazaoSocialByObject(JsonObject pessoa) {

		if(pessoa.get("razaoSocial") != null) {

			this.nomeUsuario = pessoa.get("razaoSocial").getAsString();

		}

	}

	// public Usuario updatePassword(String senhaAtual, String novaSenha) {

	// 	if(!SHA512Generator.generateValue(senhaAtual).equals(this.senha)) {

	// 		throw new ValidationException().userMessage("usuarios.senha.diferente");

	// 	}

	// 	ValidationUtil.validate(novaSenha);

	// 	this.senha = SHA512Generator.generateValue(novaSenha);

	// 	return super.save();

	// }

	public void readjustFieldsToResponse(String sessionKey) {

		Usuario usuarioPessoa = ExternalPessoaService.findPessoaByLoginForHeader(this.login);

		this.sessionKeyEntradaUnica = sessionKey;
		this.pessoaId = usuarioPessoa.pessoaId;
		this.nomeUsuario = usuarioPessoa.nomeUsuario;
		this.razaoSocialUsuario = usuarioPessoa.razaoSocialUsuario;

	}

	public static Boolean verificarCpf(String cpf){

		Criteria crit = ((Session) JPA.em().getDelegate()).createCriteria(Usuario.class);
		crit.add(Restrictions.eq("login", cpf));

		List<Usuario> usuarios = crit.list();

		return (usuarios.size() > 0);

	}

	public static List<Usuario> getUsuariosByModulo(String siglaModulo) {

		List<Usuario> usuarios = Usuario.findAll();

		for (Usuario usuario : usuarios) {

			// removendo perfis.moduloPertencente igual a nulo
			usuario.perfis = usuario.perfis.stream().filter(perfil -> Objects.nonNull(perfil.moduloPertencente)).collect(Collectors.toSet());

			usuario.perfis = usuario.perfis.stream().filter(perfil -> (perfil.moduloPertencente.sigla.equals(siglaModulo))).collect(Collectors.toSet());

		}

		return usuarios;

	}

	public static boolean verificarCadastro(String login) {
		
		Usuario usuario = Usuario.findByLogin(login);
		if(usuario == null){
			return false;
		}
		return usuario.pessoaCadastro;
	}

	public static Usuario verificarPessoa(String login) {

		Usuario usuarioPessoa = ExternalPessoaService.checkPessoaFisicaCadastrada(login);

		if(usuarioPessoa == null){
			return null;
		}
		return usuarioPessoa;
	}

}
