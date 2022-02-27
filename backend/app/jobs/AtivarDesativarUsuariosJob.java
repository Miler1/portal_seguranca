package jobs;

import models.AgendaDesativacao;
import play.jobs.Job;
import play.jobs.On;

import java.util.List;

import static play.Logger.info;

// Dispara todos os dias 0:01:00
@On("cron.ativarDesativarUsuarios")
public class AtivarDesativarUsuariosJob extends Job {

	@Override
	public void doJob() {

		info("[JOB-START] Iniciando ativar/desativar usuário.");

		deactivateUsuarios();

		activateUsuarios();

	}

	private void deactivateUsuarios() {

		List<AgendaDesativacao> desativacoes = AgendaDesativacao.find("dataInicio = CURRENT_DATE()").fetch();

		for(AgendaDesativacao agendaDesativacao : desativacoes) {
			if(agendaDesativacao.usuario.ativo) {
				agendaDesativacao.usuario.deactivate();
			}
		}

	}

	private void activateUsuarios() {

		List<AgendaDesativacao> ativacoes = AgendaDesativacao.find("dataFim = CURRENT_DATE()").fetch();

		for(AgendaDesativacao agendaDesativacao : ativacoes) {
			if(!agendaDesativacao.usuario.ativo) {
				agendaDesativacao.usuario.activate();
			}
		}

	}

}
