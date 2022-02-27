package secure;

import play.mvc.Http;
import play.mvc.Scope;
import portalSema.PortalSemaResponseTokenVO;

import java.io.Serializable;

public interface IAuthenticationService extends Serializable {

	IAuthenticatedUser authenticate(Http.Request request, Scope.Session session);

	IAuthenticatedUser getAuthenticatedUser(Http.Request request, Scope.Session session);

	PortalSemaResponseTokenVO authenticatePortalSema(Http.Request request, Scope.Session session);

	Boolean logoutPortalSema(String accesToken);
}