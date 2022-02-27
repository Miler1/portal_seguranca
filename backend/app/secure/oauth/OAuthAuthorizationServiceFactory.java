package secure.oauth;

import exceptions.OAuthRequestException;
import play.Play;

public class OAuthAuthorizationServiceFactory {

	private static final String SERVICE_CLASS_BASE_KEY = "oAuth.service.class.";

	public OAuthAuthorizationService getInstance(String serviceClassKey) {

		return createService(serviceClassKey);

	}

	private OAuthAuthorizationService createService(String serviceClassKey) {

		try {

			String className = Play.configuration.containsKey(SERVICE_CLASS_BASE_KEY + serviceClassKey) ? Play.configuration.getProperty(SERVICE_CLASS_BASE_KEY + serviceClassKey) : null;

			if(className == null) {

				throw new RuntimeException("Serviço de autorização não encontrado, favor conferir a configuração.");

			}

			return (OAuthAuthorizationService) Class.forName(className).newInstance();

		} catch(Exception e) {

			throw new OAuthRequestException().userMessage("oAuth.authorizationFormat.invalido");

		}

	}

}
