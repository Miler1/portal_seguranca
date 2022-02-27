package exceptions;

import play.libs.WS.HttpResponse;

public class WebServiceException extends RuntimeException {

    public WebServiceException(HttpResponse wsResponse) {

        super("WebService Error: " + wsResponse.getStatus() + " - " + wsResponse.getStatusText() +
                "\n    body: \n " + wsResponse.getString());
    }

    public WebServiceException(String msg) {

        super("WebService Error: " + msg);
    }
}
