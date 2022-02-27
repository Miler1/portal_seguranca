package services;

import play.Play;

public class UsuarioServiceFactory {

    private IUsuarioService service = null;
    private static final String SERVICE_CLASS_KEY = "usuario.service.class";

    public IUsuarioService getInstance() {

        if (service == null) {

            service = createService();

        }

        return service;

    }

    private IUsuarioService createService() {

        try {

            String className = Play.configuration.containsKey(SERVICE_CLASS_KEY) ? Play.configuration.getProperty(SERVICE_CLASS_KEY) : null;

            if (className == null) {

                throw new RuntimeException("CheckingService not found, check configuration.");

            }

            return (IUsuarioService) Class.forName(className).newInstance();

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }

}
