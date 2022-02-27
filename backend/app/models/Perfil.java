package models;

import exceptions.ValidationException;
import models.permissoes.AcaoSistema;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import play.Play;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.db.jpa.JPA;
import play.mvc.Http;
import secure.SHA512Generator;
import secure.authorization.Action;
import secure.authorization.Permissible;
import secure.oauth.OAuthAuthorizationService;
import utils.Config;
import utils.Identificavel;
import utils.ListUtil;

import javax.persistence.*;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(schema = "portal_seguranca", name = "perfil")
public class Perfil extends GenericModel implements Identificavel, Permissible {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_perfil")
	@SequenceGenerator(name = "sq_perfil", sequenceName = "portal_seguranca.sq_perfil", allocationSize = 1)
	public Integer id;

	@MaxSize(value = 200, message = "perfis.validacao.nome.max")
	@Required(message = "perfis.validacao.nome.req")
	public String nome;

	public String avatar;

	@Column(name = "data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dataCadastro;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(schema = "portal_seguranca", name = "permissao_perfil",
		joinColumns = @JoinColumn(name = "id_perfil", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name = "id_permissao", referencedColumnName = "id"))
	public List<Permissao> permissoes;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "id_modulo_pertencente", referencedColumnName = "id")
	public Modulo moduloPertencente;

	@Column(name = "ativo")
	public Boolean ativo;

	@Column(name = "cadastro_entrada_unica")
	public Boolean cadastroEntradaUnica;

	@Transient
	private List<Integer> permittedActionsIds;

	@MaxSize(value = 200, message = "perfis.validacao.codigo.max")
	@Required(message="perfis.validacao.codigo.req")
	public String codigo;

	@ManyToMany(fetch= FetchType.LAZY)
	@JoinTable(schema = "portal_seguranca", name = "perfil_setor",
			joinColumns = @JoinColumn(name = "id_perfil"),
			inverseJoinColumns = @JoinColumn(name = "id_setor"))
	public List<Setor> setores;

	@Transient
	public Set<Modulo> modulosTemPermissoes;

	public Perfil() {

		super();

		this.dataCadastro = new Date();

	}

	public Perfil(String nome, String codigo) {

		super();

		this.nome = nome;
		this.codigo = codigo;

	}

	public static Perfil findByIdWithPermissoes(Integer id) {

		String jpql = "SELECT p FROM " + Perfil.class.getSimpleName() + " p INNER JOIN FETCH p.permissoes"
				+ " WHERE p.id = ?";

		return Perfil.find(jpql, id).first();

	}

	public static Perfil findByCodigoModuloWithPermissao(String codigo, Integer idModule) {

		String jpql = "SELECT p FROM " + Perfil.class.getSimpleName() + " p " +
			" INNER JOIN FETCH p.permissoes pm" +
			" JOIN p.moduloPertencente m " +
			" WHERE p.codigo = :codigo " +
			" AND m.id = :idModule";

		JPAQuery query = Perfil.find(jpql);

		return query.setParameter("codigo", codigo)
			.setParameter("idModule", idModule)
			.first();

	}

	@Override
	public Integer getId() {

		return this.id;

	}

	@Override
	public List<Action> getAvailableActions() {

		List<Action> availableActions = new ArrayList<>();

		availableActions.add((Action) AcaoSistema.findById(AcaoSistema.CADASTRAR_PERFIL));
		availableActions.add((Action) AcaoSistema.findById(AcaoSistema.EDITAR_PERFIL));
		availableActions.add((Action) AcaoSistema.findById(AcaoSistema.REMOVER_PERFIL));
		availableActions.add((Action) AcaoSistema.findById(AcaoSistema.VISUALIZAR_PERFIL));

		return availableActions;

	}

	public String getAvatar() {

		if(this.avatar != null) {

			return this.avatar;

		}

		return "images" + Play.configuration.getProperty("arquivos.path.padrao.avatarPerfis");

	}

	@Override
	public void setPermittedActionsIds(List<Integer> actionsIds) {

		this.permittedActionsIds = actionsIds;

	}

	@Override
	public List<Integer> getPermittedActionsIds() {

		return this.permittedActionsIds;

	}

    public static void validateUsuarioPerfis(Set<Perfil> usuarioPerfis) {

        if (usuarioPerfis == null) {
            throw new ValidationException().userMessage("usuarios.validacao.perfil.req");
        }

        atribuirModulos(usuarioPerfis);

        List<Integer> idsPerfis = ListUtil.getIds(usuarioPerfis);

        List<Integer> idsPerfisValidos = Perfil
                .find("select p.id from " + Perfil.class.getSimpleName() + " p where p.id in (:ids)")
                .setParameter("ids", idsPerfis).fetch();

        if (idsPerfis.size() != idsPerfisValidos.size()) {

            throw new ValidationException().userMessage("usuarios.validacao.perfil.invalido");

        }

    }

    private static void atribuirModulos(Set<Perfil> usuarioPerfis) {

        Map<String, String> modulos = new HashMap<>();

        modulos.put(Play.configuration.getProperty("modulo.licenciamento.sigla"),
                Play.configuration.getProperty("modulo.licenciamento.perfil.publico")
        );
        modulos.put(Play.configuration.getProperty("modulo.gestaoResponsabilidadeTecnica.sigla"),
                Play.configuration.getProperty("modulo.gestaoResponsabilidadeTecnica.perfil.usuario")
        );

		modulos.forEach((key, value) -> {

			Perfil perfilPublico = Perfil.findByCodigoModuloWithPermissao(
					value, Modulo.findBySigla(key).id);

			if (perfilPublico != null && !usuarioPerfis.isEmpty()) {

				Set<Perfil> filtered = usuarioPerfis.stream()
						.filter(perfil -> perfil.id.equals(perfilPublico.id)).collect(Collectors.toSet());

				if (filtered.isEmpty()) {
					usuarioPerfis.add(perfilPublico);
				}

			}

		});

	}

	public Boolean isFromModulo(Integer idModulo) {

		boolean hasModulo = false;

		for(Permissao permissao : this.permissoes) {

			if(permissao.modulo != null && permissao.modulo.id.equals(idModulo)) {

				hasModulo = true;

			}

		}

		return hasModulo;

	}

	@Override
	public boolean equals(Object o) {

		if(o == this) {

			return true;

		}

		if(!(o instanceof Perfil)) {

			return false;

		}

		Perfil perfil = (Perfil) o;

		return perfil.codigo != null && (perfil.codigo.equals(codigo) && (perfil.moduloPertencente == moduloPertencente) && (perfil.nome.equals(nome)));

	}

	@Override
	public int hashCode() {

		return Objects.hash(nome, moduloPertencente);

	}

	public static Perfil getPefilByNomeAndModulo(String nome, Modulo modulo) {

		return Perfil.find("byNomeAndModuloPertencente", nome, modulo).first();

	}

	public static Perfil getPerfilByCodigo(String codigoPerfilProdap) {

		return Perfil.find("byCodigo", codigoPerfilProdap).first();

	}


	public static Perfil getPerfilByCodigoAndModulo(String codigo, Modulo modulo) {

		return Perfil.find("byCodigoAndModuloPertencente", codigo, modulo).first();

	}

	public static Perfil getPefilByCodigoWithPermissao(String codigo, Modulo modulo) {

		return Perfil.findByCodigoModuloWithPermissao(codigo, modulo.id);

	}

	public static Set<Perfil> findPerfisByModuloRequest(Usuario usuario) {

		String accessToken = OAuthAuthorizationService.getToken(Http.Request.current());
		OAuthClient oAuthClient = OAuthClient.find("accessToken", SHA512Generator.generateValue(accessToken)).first();

		return new HashSet<>(usuario.getPerfisByModulo(oAuthClient.modulo.id));

	}

	public static List<Perfil> findByFilter(FiltroPerfil filtro) {

		Criteria crit = ((Session) JPA.em().getDelegate()).createCriteria(Perfil.class);
		crit.setFirstResult((filtro.numeroPagina - 1) * filtro.tamanhoPagina);
		crit.setMaxResults(filtro.tamanhoPagina);

		if((filtro.siglaModulo != null && filtro.siglaModulo.equals("%"))
				|| (filtro.nome != null && filtro.nome.equals("%"))) {

			return new ArrayList<>();

		}

		advancedFilterRestrictions(crit, filtro);

		filtro.addOrder(crit);

		return crit.list();

	}

	public static Long countByFilter(FiltroPerfil filtro) {

		Criteria crit = ((Session) JPA.em().getDelegate()).createCriteria(Perfil.class);

		if((filtro.siglaModulo != null && filtro.siglaModulo.equals("%"))
				|| (filtro.nome != null && filtro.nome.equals("%"))) {

			return 0L;

		}

		advancedFilterRestrictions(crit, filtro);

		return Long.valueOf(crit.list().size());

	}

	private static void advancedFilterRestrictions(Criteria crit, FiltroPerfil filtro) {

		crit.createAlias("moduloPertencente", "m");

		if(filtro.nome != null) {

			String parametro = Normalizer.normalize(filtro.nome, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
			Criterion nome = Restrictions.sqlRestriction("unaccent({alias}.nome) ILIKE ?", "%" + parametro + "%", new StringType());
			crit.add(nome);

		}

		if(filtro.ativo != null) {

			crit.add(Restrictions.eq("ativo", filtro.ativo));

		}

		if(filtro.siglaModulo != null) {

			String parametro = Normalizer.normalize(filtro.siglaModulo, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
			crit.add(Restrictions.ilike("m.sigla", parametro, MatchMode.ANYWHERE));

		}

	}

	public void adjustFind() {

		this.modulosTemPermissoes = Modulo.findModulosByPermissoes(this.permissoes);

		for(Modulo modulo : this.modulosTemPermissoes) {

			if(modulo.id.equals(Config.ID_PORTAL)) {

				modulo.permissoes.addAll(((Modulo) Modulo.findById(Config.ID_CADASTRO)).permissoes);

			} else if(modulo.id.equals(Config.ID_CADASTRO)) {

				modulo.permissoes.addAll(((Modulo)Modulo.findById(Config.ID_PORTAL)).permissoes);

			}

			for(Permissao permissao : this.permissoes) {

				if(modulo.permissoes.contains(permissao)) {

					permissao.isFromPerfilVisualizar = true;

				}

			}

		}

	}

	public static List<Usuario> findUsuariosByPerfil(String codigoPerfil){

		String jpql = "SELECT DISTINCT(u) FROM " + Usuario.class.getSimpleName() + " u " +
				" JOIN u.perfis p " +
				" WHERE p.codigo = :codigo";

		JPAQuery query = Usuario.find(jpql);

		return query.setParameter("codigo", codigoPerfil).fetch();
	}

	public static List<Usuario> findUsuariosByPerfilAndSetores(String codigoPerfil, String siglaSetores){

		String[] arraySiglas = siglaSetores.split("\\s*,\\s*");
		List<String> listaSiglas = Arrays.stream(arraySiglas).collect(Collectors.toList());

		String jpql = "SELECT DISTINCT(u) FROM " + Usuario.class.getSimpleName() + " u " +
				" JOIN u.perfis p " +
				" JOIN u.setores s " +
				" WHERE p.codigo = :codigo" +
				" AND s.sigla IN (:listaSiglas)";

		JPAQuery query = Usuario.find(jpql);

		return query.setParameter("codigo", codigoPerfil).
				setParameter("listaSiglas", listaSiglas)
				.fetch();
	}

}
