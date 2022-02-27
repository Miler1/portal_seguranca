package models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import deserializers.BooleanDeserializer;
import exceptions.ValidationException;
import models.permissoes.AcaoSistema;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import play.Logger;
import play.Play;
import play.data.validation.*;
import play.db.jpa.GenericModel;
import play.db.jpa.JPA;
import play.mvc.Http;
import results.Unauthorized;
import secure.SHA512Generator;
import secure.authorization.Action;
import secure.authorization.Permissible;
import utils.Config;
import utils.FileManager;
import utils.Identificavel;
import utils.InetAddressComparator;
import utils.ValidationUtil;

import javax.persistence.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.Normalizer;
import java.util.*;

@Entity
@Table(schema = "portal_seguranca", name = "modulo")
public class Modulo extends GenericModel implements Permissible, Identificavel {


	public static final String pathArquivos = Play.configuration.getProperty("arquivos.path");

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_modulo")
	@SequenceGenerator(name = "sq_modulo", sequenceName = "portal_seguranca.sq_modulo", allocationSize = 1)
	public Integer id;

	@MaxSize(value = 200, message = "modulos.nome.max")
	@Required(message = "modulos.nome.obrigatorio")
	public String nome;

	@Unique(message = "modulos.sigla.unico")
	@Required(message = "modulos.sigla.obrigatorio")
	@MinSize(value = 3, message = "modulos.sigla.min")
	@MaxSize(value = 3, message = "modulos.sigla.max")
	public String sigla;

	@MaxSize(value = 200, message = "modulos.url.max")
	@Required(message = "modulos.url.obrigatorio")
	public String url;

	@MaxSize(value = 500, message = "modulos.descricao.max")
	@Required(message = "modulos.descricao.obrigatorio")
	public String descricao;

	@Column(name = "data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dataCadastro;

	public Boolean ativo;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "id_documento_logotipo", referencedColumnName = "id")
	public Documento logotipo;

	@Valid
	@Required(message = "arquivo.permissoes.obrigatorio")
	@OneToMany(mappedBy = "modulo", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<Permissao> permissoes;

	// Lista de IPs válidos separados por vírgula
	@MaxSize(value = 50, message = "modulos.ips.max")
	@Required(message = "modulos.ips.obrigatorio")
	public String ips;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "id_cliente_oauth", referencedColumnName = "id")
	public OAuthClient oAuthClient;

	@Column(name = "cadastrar_perfis")
	@Required(message = "modulos.cadastrarPerfis.obrigatorio")
	public Boolean cadastrarPerfis;

	@Transient
	private List<Integer> permittedActionsIds;

	@Transient
	public Set<Perfil> perfis;

	@Transient
	public String chaveArquivo;

	@Transient
	public String chaveLogotipo;

	@Transient
	public OAuthClient unHashedOAuthClient;

	public Modulo() {

		super();

		this.dataCadastro = new Date();
		this.ativo = true;

	}

	@Override
	public void setPermittedActionsIds(List<Integer> actionsIds) {

		this.permittedActionsIds = actionsIds;

	}

	@Override
	public List<Integer> getPermittedActionsIds() {

		return this.permittedActionsIds;

	}

	@Override
	public List<Action> getAvailableActions() {

		List<Action> availableActions = new ArrayList<>();

		availableActions.add((Action) AcaoSistema.findById(AcaoSistema.CADASTRAR_MODULO));
		availableActions.add((Action) AcaoSistema.findById(AcaoSistema.EDITAR_MODULO));
		availableActions.add((Action) AcaoSistema.findById(AcaoSistema.REMOVER_MODULO));
		availableActions.add((Action) AcaoSistema.findById(AcaoSistema.VISUALIZAR_MODULO));
		availableActions.add((Action) AcaoSistema.findById(AcaoSistema.ATIVAR_DESATIVAR_MODULO));

		return availableActions;

	}

	public boolean isValidIP(Http.Request request) {

		String remoteAddress;

		if(request.headers.get("x-real-ip") != null) {

			remoteAddress = request.headers.get("x-real-ip").value();

		} else {

			remoteAddress = request.remoteAddress;

		}

		String[] ipsValidos = this.ips.trim().split(",");
		InetAddress addr;
		InetAddress remoteAddr;

		try {

			remoteAddr = InetAddress.getByName(remoteAddress.trim());

			for(String ipValido : ipsValidos) {

				InetAddressComparator addressComparator = new InetAddressComparator();
				addr = InetAddress.getByName(ipValido.trim());

				if(addressComparator.compare(remoteAddr, addr) == 0) {

					return true;

				}

			}

		} catch(UnknownHostException | NullPointerException e) {

			Logger.error(e.getMessage());

			throw new Unauthorized();

		}

		Logger.warn("Unauthorized IP: " + remoteAddress);

		return false;

	}

	public static List<Modulo> findByFilter(FiltroModulo filtro) {

		Criteria crit = ((Session) JPA.em().getDelegate()).createCriteria(Modulo.class);
		crit.setFirstResult((filtro.numeroPagina - 1) * filtro.tamanhoPagina);
		crit.setMaxResults(filtro.tamanhoPagina);

		if((filtro.sigla != null && filtro.sigla.equals("%"))
				|| (filtro.nome != null && filtro.nome.equals("%"))) {

			return new ArrayList<>();

		}

		advancedFilterRestrictions(crit, filtro);

		filtro.addOrder(crit);

		crit.add(Restrictions.ne("sigla", Config.SIGLA_CADASTRO));
		crit.add(Restrictions.ne("sigla", Config.SIGLA_PORTAL));

		return crit.list();

	}

	public static Long countByFilter(FiltroModulo filtro) {

		Criteria crit = ((Session) JPA.em().getDelegate()).createCriteria(Modulo.class);

		if((filtro.sigla != null && filtro.sigla.equals("%"))
				|| (filtro.nome != null && filtro.nome.equals("%"))) {

			return 0L;

		}

		advancedFilterRestrictions(crit, filtro);

		crit.add(Restrictions.ne("sigla", Config.SIGLA_CADASTRO));
		crit.add(Restrictions.ne("sigla", Config.SIGLA_PORTAL));

		return Long.valueOf(crit.list().size());

	}

	private static void advancedFilterRestrictions(Criteria crit, FiltroModulo filtro) {

		if(filtro.sigla != null) {

			String parametro = Normalizer.normalize(filtro.sigla, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
			Criterion sigla = Restrictions.sqlRestriction("unaccent({alias}.sigla) ILIKE ?", "%" + parametro + "%", new StringType());
			crit.add(sigla);

		}

		if(filtro.nome != null) {

			String parametro = Normalizer.normalize(filtro.nome, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
			Criterion nome = Restrictions.sqlRestriction("unaccent({alias}.nome) ILIKE ?", "%" + parametro + "%", new StringType());
			crit.add(nome);

		}

		if(filtro.ativo != null) {

			crit.add(Restrictions.eq("ativo", filtro.ativo));

		}

	}

	public static Modulo readFileToJson(String fileCode) {

		try {

			String[] filePath = fileCode.split("_");

			JsonReader jsonReader = new JsonReader(new FileReader(FileManager.getInstance().getTempFilesFolder().getPath() + File.separator + filePath[0] + File.separator + filePath[1]));

			try {

				Gson gson;

				GsonBuilder builder = new GsonBuilder()
						.serializeSpecialFloatingPointValues()
						.registerTypeAdapter(Boolean.class, new BooleanDeserializer());

				gson = builder.create();

				Modulo modulo =  gson.fromJson(jsonReader, Modulo.class);
				modulo.permissoes.removeAll(Collections.singleton(null));

				for (Permissao permissao : modulo.permissoes) {

					if(permissao.perfis != null) {

						permissao.perfis.removeAll(Collections.singleton(null));

					}

				}

				return modulo;

			} catch(JsonParseException e) {

				throw new ValidationException().userMessage("modulos.cadastro.arquivo.erro");

			}

		} catch(FileNotFoundException e) {

			throw new ValidationException().userMessage("arquivo.naoEncontrado");

		}

	}

	public void validateModulo() {

		if(this.permissoes == null || !Permissao.hasPerfil(this.permissoes)) {

			throw new ValidationException().userMessage("modulos.cadastro.arquivo.erro");

		}

		List<String> codigoPermissao = new ArrayList<>();
		List<Perfil> perfis = new ArrayList<>();

		for(Permissao permissao : this.permissoes) {

			codigoPermissao.add(permissao.codigo);

			if(permissao.perfilPublico != null && permissao.perfilPublico) {

				perfis.add(new Perfil(Config.CODIGO_PERFIL_PUBLICO, Config.PERFIL_PUBLICO));

			}

			if(permissao.perfis != null) {

				for(Perfil perfil : permissao.perfis) {

					perfis.add(perfil);

				}

				Set<Perfil> perfisSemDuplicata = new HashSet<>(perfis);

				if(perfis.size() < perfis.size()) {

					Validation.addError("permissoes.validacao.perfis.duplicados", "permissoes.validacao.perfis.duplicados");

				}

			}

			perfis = new ArrayList<>();
			permissao.modulo = this;

		}

		Set<String> codigosSemDuplicata = new HashSet<>(codigoPermissao);

		if(codigosSemDuplicata.size() < codigoPermissao.size()) {

			Validation.addError("permissoes.validacao.codigo.unico", "permissoes.validacao.codigo.unico");

		}

		ValidationUtil.validate(this);

	}

	public void createModuloPerfis() {

		this.perfis = createPerfisList(this);

	}

	private Set<Perfil> createPerfisList(Modulo modulo) {

		Set<Perfil> perfis = new HashSet<>();

		for(Permissao permissao : modulo.permissoes) {

			permissao.modulo = this;

			if(permissao.perfis != null) {

				for(Perfil perfil : permissao.perfis) {

					perfil.moduloPertencente = this;
					perfis.add(perfil);

				}

			}

			if(permissao.perfilPublico != null && permissao.perfilPublico) {

				Perfil perfilPublico = Perfil.getPefilByNomeAndModulo(Config.PERFIL_PUBLICO, Modulo.findBySigla(Config.SIGLA_PORTAL));

				perfis.add(perfilPublico);

				if(permissao.perfis == null) {

					permissao.perfis = new HashSet<>();
				}

				permissao.perfis.add(perfilPublico);

			}

		}

		return perfis;

	}

	@Override
	public Modulo save() {

		createPerfisAndPermissoesInstances();

		ValidationUtil.validate(this);

		validateArquivoIntegracao();
		validateLogotipo();
		saveArquivoIntegracao();
		saveLogotipo();

		this.unHashedOAuthClient = OAuthClient.createOAuthClient();

		this.oAuthClient = new OAuthClient();
		this.oAuthClient.scope = this.unHashedOAuthClient.scope;
		this.oAuthClient.clientType = this.unHashedOAuthClient.clientType;
		this.oAuthClient.dataSolicitacao = this.unHashedOAuthClient.dataSolicitacao;
		this.oAuthClient.clientId = SHA512Generator.generateValue(this.unHashedOAuthClient.clientId);
		this.oAuthClient.clientSecret = SHA512Generator.generateValue(this.unHashedOAuthClient.clientSecret);
		this.sigla = this.sigla.toUpperCase();

		return super.save();

	}

	public Modulo newCredentials() {

		this.unHashedOAuthClient = OAuthClient.createOAuthClient();

		this.oAuthClient.scope = this.unHashedOAuthClient.scope;
		this.oAuthClient.clientType = this.unHashedOAuthClient.clientType;
		this.oAuthClient.dataSolicitacao = this.unHashedOAuthClient.dataSolicitacao;
		this.oAuthClient.clientId = SHA512Generator.generateValue(this.unHashedOAuthClient.clientId);
		this.oAuthClient.clientSecret = SHA512Generator.generateValue(this.unHashedOAuthClient.clientSecret);
		this.oAuthClient.accessToken = null;
		this.oAuthClient.refreshToken = null;

		return super.save();

	}

	private void createPerfisAndPermissoesInstances() {

		Set<Perfil> perfis = new HashSet<>(this.perfis);
		Set<Perfil> perfisAux = new HashSet<>();

		this.perfis.clear();

		for(Permissao permissao : this.permissoes) {

			if(permissao.perfis != null) {

				for(Perfil perfil : permissao.perfis) {

					for(Perfil perfilAux : perfis) {

						if(perfilAux.nome.equals(Config.PERFIL_PUBLICO)
								&& perfilAux.nome.equals(perfil.nome)) {

							perfisAux.add(Perfil.getPefilByNomeAndModulo(
									Config.PERFIL_PUBLICO,
									Modulo.findBySigla(Config.SIGLA_PORTAL)));

						} else if(perfilAux.codigo != null && perfilAux.codigo.equals(perfil.codigo)) {

							perfilAux.moduloPertencente = this;
							perfilAux.ativo = true;

							perfisAux.add(perfilAux);

						}

					}

				}

				permissao.perfis = perfisAux;
				this.perfis.addAll(perfisAux);

			}

			perfisAux = new HashSet<>();
			permissao.modulo = this;

		}

	}

	public static Modulo findBySigla(String sigla) {

		return Modulo.find("bySigla", sigla).first();

	}

	private void validateLogotipo() {

		if(this.id == null || this.chaveLogotipo != null && !this.chaveLogotipo.isEmpty()) {

			if(!FileManager.getInstance().fileExists(this.chaveLogotipo, Config.APPLICATION_TEMP_FOLDER)) {

				throw new ValidationException().userMessage("modulos.logo.invalido");

			}

		}

	}

	private void validateArquivoIntegracao() {

		if(this.id == null || this.chaveArquivo != null && !this.chaveArquivo.isEmpty()) {

			if(!FileManager.getInstance().fileExists(this.chaveArquivo, Config.APPLICATION_TEMP_FOLDER)) {

				throw new ValidationException().userMessage("modulos.arquivoIntegracao.invalido");

			}

		}

	}

	private void saveArquivoIntegracao() {

		String nomeArquivoMovido = this.sigla + "_" + TipoDocumento.Tipos.ARQUIVO_INTEGRACAO.prefixo;

		new Documento(nomeArquivoMovido, TipoDocumento.findById(TipoDocumento.Tipos.ARQUIVO_INTEGRACAO.id), this.chaveArquivo).save();

	}

	private void saveLogotipo() {

		String nomeArquivoMovido = this.sigla + "_" + TipoDocumento.Tipos.LOGOTIPO.prefixo;

		this.logotipo = new Documento(nomeArquivoMovido, TipoDocumento.findById(TipoDocumento.Tipos.LOGOTIPO.id), this.chaveLogotipo);
		this.logotipo.save();

	}

	public static Set<Modulo> findModulosByPermissoes(List<Permissao> permissoes) {

		Set<Modulo> modulos = new HashSet();
		Boolean moduloEntradaUnica = false;

		for(Permissao permissao : permissoes) {

			if (permissao.modulo != null) {

				if(permissao.modulo.sigla.equals(Config.SIGLA_CADASTRO) || permissao.modulo.sigla.equals(Config.SIGLA_PORTAL)) {

					if(!moduloEntradaUnica) {

						permissao.modulo.sigla = Config.SIGLA_ENTRADA_UNICA;
						permissao.modulo.nome = Config.NOME_ENTRADA_UNICA;
						modulos.add(permissao.modulo);
						moduloEntradaUnica = true;

					}

				} else {

					modulos.add(permissao.modulo);

				}

			}

		}

		return modulos;

	}

	public List<Perfil> getPerfisUsuarioLogado(Usuario usuario) {

		return usuario.getPerfisByModulo(this.id);

	}

	public File getArquivoLogotipo() {

		return new File(pathArquivos  + this.logotipo.caminho);

	}

	public static List<Modulo> findAllWithPerfis(){

		List<Modulo> modulos = Modulo.findAll();

		for(Modulo modulo : modulos) {

			modulo.createModuloPerfis();

		}

		return modulos;

	}

	@Override
	public Integer getId() {

		return this.id;
	}
}
