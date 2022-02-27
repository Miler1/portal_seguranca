package secure;

import play.Play;

public class CheckingServiceFactory {

    private static ICheckingService service = null;
    private static final String SERVICE_CLASS_KEY = "checking.service.class";

    public ICheckingService getInstance() {

        if (service == null) {

            service = createService();

        }

        return service;

    }

    private ICheckingService createService() {

        try {

            String className = Play.configuration.containsKey(SERVICE_CLASS_KEY) ? Play.configuration.getProperty(SERVICE_CLASS_KEY) : null;

            if (className == null) {

                throw new RuntimeException("CheckingService not found, check configuration.");

            }

            return (ICheckingService) Class.forName(className).newInstance();

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }

}
