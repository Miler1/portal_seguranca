package secure;

import play.Play;

public class AuthenticationServiceFactory {

	private static IAuthenticationService service = null;
	private static final String SERVICE_CLASS_KEY = "authentication.service.class";

	public IAuthenticationService getInstance() {

		if (service == null) {

			service = createService();

		}

		return service;

	}

	private IAuthenticationService createService() {

		try {

			String className = Play.configuration.containsKey(SERVICE_CLASS_KEY) ? Play.configuration.getProperty(SERVICE_CLASS_KEY) : null;

			if (className == null) {

				throw new RuntimeException("AuthenticationService not found, check configuration.");

			}

			return (IAuthenticationService) Class.forName(className).newInstance();

		} catch (Exception e) {

			throw new RuntimeException(e);

		}

	}

}
