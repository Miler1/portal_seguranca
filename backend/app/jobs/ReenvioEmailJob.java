package jobs;

import models.ReenvioEmail;
import notifiers.Emails;
import play.jobs.Job;
import play.jobs.On;

import java.util.List;

// @On("cron.reenviarEmail")
public class ReenvioEmailJob extends Job {

	@Override
	public void doJob() {

		List<ReenvioEmail> reenviosEmail = ReenvioEmail.find("dataPrimeiraTentativa BETWEEN CURRENT_DATE() - " + ReenvioEmail.nroMaxTentativas + " AND CURRENT_DATE()").fetch();

		for(ReenvioEmail reenvioEmail : reenviosEmail) {

			sendMail(reenvioEmail);

		}

	}

	private void sendMail(ReenvioEmail reenvioEmail) {

		try {

			switch(reenvioEmail.tipoEmail) {

				case PRIMEIRO_ACESSO:

					Emails.primeiroAcesso(reenvioEmail.usuario, reenvioEmail.url);

					break;

				case CRIAR_SENHA_PRIMEIRO_ACESSO:

					Emails.criarSenhaPrimeiroAcesso(reenvioEmail.usuario);

					break;

				case REDEFINIR_SENHA:

					Emails.redefinirSenha(reenvioEmail.usuario, reenvioEmail.url);

					break;

				case SENHA_REDEFINIDA:

					Emails.senhaRedefinida(reenvioEmail.usuario);

					break;

			}

			reenvioEmail.delete();

		} catch(Exception e) {

			reenvioEmail.log = e.getMessage();

			reenvioEmail.save();

		}

	}

}
