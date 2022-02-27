package secure.oauth;

import exceptions.OAuthRequestException;
import models.OAuthClient;
import models.Token;
import play.Play;
import play.mvc.Http;
import results.Unauthorized;
import secure.SHA512Generator;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Date;

public class ClientCredentials extends OAuthAuthorizationService {

	@Override
	public Token getAccessToken(Http.Request request) {

		String authorizationBasic = request.headers.get("authorization").value();

		if(authorizationBasic == null || authorizationBasic.isEmpty()) {

			throw new OAuthRequestException().userMessage("oAuth.authorizationFormat.invalido");

		}

		String[] values = getClientCredentials(authorizationBasic);
		String clientId = values[0];
		String clientSecret = values[1];

		OAuthClient oAuthClient = OAuthClient.find("clientId = :clientId AND clientSecret = :clientSecret")
				.setParameter("clientId", SHA512Generator.generateValue(clientId))
				.setParameter("clientSecret", SHA512Generator.generateValue(clientSecret))
				.first();

		if(Boolean.valueOf(Play.configuration.getProperty("authentication.validate.id"))) {

			if(!oAuthClient.modulo.isValidIP(request)) {

				throw new Unauthorized();

			}

		}

		if(oAuthClient == null || !oAuthClient.modulo.ativo) {

			throw new Unauthorized();

		}

		Token token = new Token(clientId, false);

		oAuthClient.accessToken = SHA512Generator.generateValue(token.access_token);
		oAuthClient.dataSolicitacao = new Date();
		oAuthClient.save();

		return token;

	}

	private static String[] getClientCredentials(String authorizationBasic) {

		if(!authorizationBasic.startsWith("Basic")) {

			throw new OAuthRequestException().userMessage("oAuth.authorizationFormat.invalido");

		}

		String base64Credentials = authorizationBasic.substring("Basic".length()).trim();
		String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));

		if(!credentials.contains(":")) {

			throw new OAuthRequestException().userMessage("oAuth.authorizationFormat.invalido");

		}

		return credentials.split(":", 2);

	}

}
