package models;

import exceptions.ValidationException;
import org.apache.commons.lang.time.DateUtils;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
import utils.ValidationUtil;
import validators.If;
import validators.RequiredIf;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(schema = "portal_seguranca", name = "agenda_desativacao")
public class AgendaDesativacao extends GenericModel {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_agenda_desativacao")
	@SequenceGenerator(name = "sq_agenda_desativacao", sequenceName = "portal_seguranca.sq_agenda_desativacao", allocationSize = 1)
	public Integer id;

	@OneToOne
	@Required(message = "agendas.usuario.obrigatorio")
	@JoinColumn(name="id_usuario", referencedColumnName = "id")
	public Usuario usuario;

	@Temporal(TemporalType.DATE)
	@Column(name="data_inicio")
	@RequiredIf(ifs = @If(option = If.IfOption.FALSE, attribute = "tempoIndeterminado"), message = "agendas.dataInicio.obrigatorio")
	public Date dataInicio;

	@Temporal(TemporalType.DATE)
	@Column(name="data_fim")
	@RequiredIf(ifs = @If(option = If.IfOption.FALSE, attribute = "tempoIndeterminado"), message = "agendas.dataFim.obrigatorio")
	public Date dataFim;

	@OneToOne
	@Required(message = "agendas.usuarioSolicitante.obrigatorio")
	@JoinColumn(name = "id_usuario_solicitante", referencedColumnName = "id")
	public Usuario usuarioSolicitante;

	@ManyToOne
	@Required(message = "agendas.motivo.obrigatorio")
	@JoinColumn(name = "id_motivo", referencedColumnName = "id")
	public Motivo motivo;

	@Column(name="descricao")
	@RequiredIf(ifs = @If(option = If.IfOption.EQUALS, attribute = "motivo.id", value = "4"), message = "agendas.descricao.obrigatorio")
	public String descricao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_solicitacao")
	@Required(message = "agendas.dataSolicitacao.obrigatorio")
	public Date dataSolicitacao;

	@Transient
	public Boolean tempoIndeterminado;

	public AgendaDesativacao() {

		super();

		this.dataSolicitacao = new Date();

	}

	@Override
	public AgendaDesativacao save() {

		ValidationUtil.validate(this);

		verifyUsuario(this.usuario);

		return super.save();

	}

	private void verifyDates() {

		if(this.dataInicio.after(this.dataFim) || DateUtils.isSameDay(this.dataInicio, this.dataFim)) {

			throw new ValidationException().userMessage("agendas.datas.invalidas");

		}

	}

	private void verifyUsuario(Usuario usuarioVerificar) {

		Usuario usuario = Usuario.findById(usuarioVerificar.id);

		if(usuario == null) {

			throw new ValidationException().userMessage("agendas.usuario.naoEncontrado");

		} else if(!usuario.ativo) {

			throw new ValidationException().userMessage("agendas.usuario.desativado");

		} else if(usuario.agendaDesativacao != null) {

			usuario.agendaDesativacao.delete();

		} else if(usuario.id.equals(this.usuarioSolicitante.id)) {

			throw new ValidationException().userMessage("agendas.usuario.logado");

		}

		if(this.tempoIndeterminado) {

			this.dataFim = null;
			this.dataInicio = null;

			usuario.deactivate();

		} else {

			verifyDates();

			if(DateUtils.isSameDay(this.dataInicio, new Date())) {

				usuario.deactivate();

			}

		}

	}

}
