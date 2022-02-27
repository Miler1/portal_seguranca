package notifiers;

import java.util.concurrent.Future;

import models.Usuario;
import play.Play;
import play.mvc.Mailer;

public class Emails extends Mailer {

	public static Future<Boolean> primeiroAcesso(Usuario usuario, String url) {

		setFrom("Entrada Única <"+ Play.configuration.getProperty("mail.smtp.sender") +">");
		setSubject("Ativação de usuário");
		addRecipient(usuario.email);

		return send(usuario, url);

	}

	public static Future<Boolean> criarSenhaPrimeiroAcesso(Usuario usuario) {

		setFrom("Entrada Única <"+ Play.configuration.getProperty("mail.smtp.sender") +">");
		setSubject("Usuário ativado");
		addRecipient(usuario.email);

		return send(usuario);

	}

	public static Future<Boolean> redefinirSenha(Usuario usuario, String url) {

		setFrom("Entrada Única <"+ Play.configuration.getProperty("mail.smtp.sender") +">");
		setSubject("Redefinição de senha");
		addRecipient(usuario.email);

		return send(usuario, url);

	}

	public static Future<Boolean> senhaRedefinida(Usuario usuario) {

		setFrom("Entrada Única <"+ Play.configuration.getProperty("mail.smtp.sender") +">");
		setSubject("Redefinição de senha");
		addRecipient(usuario.email);

		return send(usuario);

	}

}