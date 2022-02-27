package secure;

import exceptions.PortalSegurancaException;
import models.Usuario;
import play.cache.Cache;
import play.mvc.Http.Request;
import play.mvc.Scope.Session;
import portalSema.PortalSemaResponseTokenVO;
import portalSema.PortalSemaWS;

public class AuthenticationService implements IAuthenticationService {

	@Override
	public IAuthenticatedUser authenticate(Request request, Session session) {

		String username = request.params.get("username");

		if(username == null) {

			return null;

		}

		String password = request.params.get("password");

		if(password == null) {

			return null;

		}

		String login = username.replaceAll("[./-]", "");

		Usuario usuario = Usuario.find("login = ? AND senha = ? AND removido = false", login, SHA512Generator.generateValue(password)).first();

		if (usuario == null || usuario.id == null) {

			return null;

		}

		if(!usuario.ativo) {

			throw new PortalSegurancaException().userMessage("authenticate.inativo");

		}

		return usuario;

	}

	@Override
	public IAuthenticatedUser getAuthenticatedUser(Request request, Session session) {

		return (IAuthenticatedUser) Cache.get(session.getId());

	}

	@Override
	public PortalSemaResponseTokenVO authenticatePortalSema(Request request, Session session) {

		String username = request.params.get("username");

		if(username == null) {

			return null;

		}

		String login = username.replaceAll("[./-]", "");

		String password = request.params.get("password");

		if(password == null) {

			return null;

		}

		PortalSemaWS portalSemaWS = new PortalSemaWS();

		return portalSemaWS.loginAPI(login, password);

	}

	@Override
	public Boolean logoutPortalSema(String accesToken) {

		PortalSemaWS portalSemaWS = new PortalSemaWS();

		return portalSemaWS.logoutAPI(accesToken);

	}

}

