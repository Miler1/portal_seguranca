package secure.oauth;

import exceptions.OAuthRequestException;
import models.OAuthClient;
import models.Token;
import org.apache.commons.lang.time.DateUtils;
import play.Play;
import play.mvc.Http;
import results.Unauthorized;
import secure.SHA512Generator;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Date;

public abstract class OAuthAuthorizationService {

	public abstract Token getAccessToken(Http.Request request);

	public static void validateTokenAndIP(Http.Request request) {

		String accessToken = getToken(request);
		OAuthClient oAuthClient = OAuthClient.find("accessToken", SHA512Generator.generateValue(accessToken)).first();

		if(Boolean.valueOf(Play.configuration.getProperty("authentication.validate.id"))) {

			if(!oAuthClient.modulo.isValidIP(request)) {

				throw new Unauthorized();

			}

		}

		if(oAuthClient == null || !oAuthClient.modulo.ativo) {

			throw new Unauthorized();

		}

		verifyExpirationTime(oAuthClient);

	}

	private static void verifyExpirationTime(OAuthClient oAuthClient) {

		Integer expirationTime = Integer.parseInt(Play.configuration.getProperty("oAuth.token.expirationTime").replace("h",""));
		Date horaMaxima = DateUtils.addHours(oAuthClient.dataSolicitacao, expirationTime);

		if(horaMaxima.before(new Date())) {

			oAuthClient.accessToken = null;

			oAuthClient.save();

			throw new Unauthorized();

		}

	}

	private static String getBearerToken(String authorizationBearer) {

		if(!authorizationBearer.startsWith("Bearer")) {

			throw new OAuthRequestException().userMessage("oAuth.authorizationFormat.invalido");

		}

		return authorizationBearer.substring("Bearer".length()).trim();

	}

	public static void validateToken(String token) {

		if(token == null) {

			throw new Unauthorized();

		}

		String accessToken = getBearerToken(token);
		OAuthClient oAuthClient = OAuthClient.find("accessToken", SHA512Generator.generateValue(accessToken)).first();

		if(oAuthClient == null || !oAuthClient.modulo.ativo) {

			throw new Unauthorized();

		}

		verifyExpirationTime(oAuthClient);

	}

	public static String getToken(Http.Request request) {

		String authorizationBearer = request.headers.get("authorization").value();

		if(authorizationBearer == null || authorizationBearer.isEmpty()) {

			throw new OAuthRequestException().userMessage("oAuth.authorizationFormat.invalido");

		}

		return getBearerToken(authorizationBearer);

	}

	public static String getToken(String authorizationBearer) {

		if(authorizationBearer == null || authorizationBearer.isEmpty()) {

			throw new OAuthRequestException().userMessage("oAuth.authorizationFormat.invalido");

		}

		return getBearerToken(authorizationBearer);

	}

}
