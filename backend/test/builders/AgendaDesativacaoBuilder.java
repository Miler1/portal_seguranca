package builders;

import models.AgendaDesativacao;
import models.Motivo;
import models.Usuario;

import java.util.Date;

public class AgendaDesativacaoBuilder extends BaseModelBuilder<AgendaDesativacao> {

	public AgendaDesativacaoBuilder() {

		super(new AgendaDesativacao());

		padrao();

	}

	@Override
	protected BaseModelBuilder<AgendaDesativacao> padrao() {

		model.dataSolicitacao = new Date();

		model.usuario = new UsuarioBuilder()
				.comSenha(UsuarioBuilder.SENHA_PADRAO)
				.comLoginRandomico()
				.save();

		model.usuarioSolicitante = new UsuarioBuilder()
				.comSenha(UsuarioBuilder.SENHA_PADRAO)
				.comLoginRandomico()
				.save();

		model.tempoIndeterminado = true;
		model.motivo = Motivo.findById(Motivo.FERIAS);
		model.descricao = null;

		return null;

	}

	public AgendaDesativacaoBuilder semUsuarioAlvo() {

		this.model.usuario = null;

		return this;

	}

	public AgendaDesativacaoBuilder semDataInicio() {

		this.model.dataInicio = null;

		return this;

	}

	public AgendaDesativacaoBuilder semDataFim() {

		this.model.dataFim = null;

		return this;

	}

	public AgendaDesativacaoBuilder statusTempoIndeterminado(Boolean status) {

		this.model.tempoIndeterminado = status;

		return this;

	}

	public AgendaDesativacaoBuilder semMotivo() {

		this.model.motivo = null;

		return this;

	}

	public AgendaDesativacaoBuilder comMotivo(Motivo motivo) {

		this.model.motivo = motivo;

		return this;

	}

	public AgendaDesativacaoBuilder comDataInicio(Date date) {

		this.model.dataInicio = date;

		return this;

	}

	public AgendaDesativacaoBuilder comDataFim(Date date) {

		this.model.dataFim = date;

		return this;

	}

	public AgendaDesativacaoBuilder comUsuario(Usuario usuario) {

		this.model.usuario = usuario;

		return this;

	}

	public AgendaDesativacaoBuilder comUsuarioSolicitante(Usuario usuarioSolicitante) {

		this.model.usuarioSolicitante = usuarioSolicitante;

		return this;

	}

}
