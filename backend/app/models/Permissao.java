package models;

import models.permissoes.AcaoSistema;
import play.data.validation.*;
import play.db.jpa.GenericModel;
import secure.authorization.Action;
import secure.authorization.Permissible;
import utils.Identificavel;
import validators.CheckCodigoCompoundUnique;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(schema = "portal_seguranca", name = "permissao")
public class Permissao extends GenericModel implements Identificavel, Permissible {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_permissao")
	@SequenceGenerator(name = "sq_permissao", sequenceName = "portal_seguranca.sq_permissao", allocationSize = 1)
	public Integer id;

	@MaxSize(value = 200, message = "permissoes.validacao.nome.tamanho")
	@Required(message = "permissoes.validacao.nome.req")
	public String nome;

	@CheckWith(CheckCodigoCompoundUnique.class)
	@MaxSize(value = 200, message = "permissoes.validacao.codigo.tamanho")
	@Required(message="permissoes.validacao.codigo.req")
	public String codigo;

	@Column(name = "data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dataCadastro;

	@ManyToOne
	@JoinColumn(name = "id_modulo", referencedColumnName = "id")
	@Required(message = "permissoes.validacao.modulo.req")
	public Modulo modulo;

	@Valid
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(schema = "portal_seguranca", name = "permissao_perfil",
		joinColumns = @JoinColumn(name = "id_permissao", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name = "id_perfil", referencedColumnName = "id"))
	public Set<Perfil> perfis;

	@Transient
	private List<Integer> permittedActionsIds;

	@Transient
	public Boolean perfilPublico;

	@Transient
	public Boolean isFromPerfilVisualizar;

	public Permissao() {

		super();

		this.dataCadastro = new Date();

	}

	@Override
	public Integer getId() {

		return this.id;

	}

	@Override
	public List<Action> getAvailableActions() {

		List<Action> availableActions = new ArrayList<>();

		availableActions.add((Action) AcaoSistema.findById(AcaoSistema.CADASTRAR_PERMISSAO));
		availableActions.add((Action) AcaoSistema.findById(AcaoSistema.EDITAR_PERMISSAO));
		availableActions.add((Action) AcaoSistema.findById(AcaoSistema.REMOVER_PERMISSAO));
		availableActions.add((Action) AcaoSistema.findById(AcaoSistema.VISUALIZAR_PERMISSAO));

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

	static Boolean hasPerfil(List<Permissao> permissoes) {

		for (Permissao permissao : permissoes) {

			if((permissao.perfilPublico != null && permissao.perfilPublico) || (permissao.perfis != null) && (!permissao.perfis.isEmpty())) {

				return true;

			}

		}

		return false;

	}

}
