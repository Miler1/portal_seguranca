package controllers;

import models.AgendaDesativacao;
import models.Message;
import models.permissoes.AcaoSistema;
import play.i18n.Messages;
import play.mvc.With;

@With(Secure.class)
public class AgendasDesativacao extends BaseController {

	public static void create(AgendaDesativacao agendaDesativacao) {

		if(!isExternalResource()) {

			Secure.executeAuthorization(AcaoSistema.ATIVAR_DESATIVAR_USUARIO);

		}

		notFoundIfNull(agendaDesativacao);

		agendaDesativacao.save();

		renderJSON(new Message(Messages.get("agendas.cadastro.sucesso", agendaDesativacao)));

	}

}
