package functional;

import builders.UsuarioBuilder;
import models.Token;
import models.Usuario;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Play;
import play.cache.Cache;
import play.mvc.Http;
import utils.DatabaseCleaner;

public class SecureTest {

	public static class Login extends BaseFunctionalTest {

		@BeforeClass
		public static void beforeClass() {

			DatabaseCleaner.cleanAll();

		}

		@Test
		public void ExternalDeveLogar() {

			Cache.clear();

			Http.Request request = getDefaultRequestWithToken();

			request.params.put("username", "06926933600");
			request.params.put("password", "123456");

			Http.Response response = POST(request, "/external/login", "application/json", "");
			Usuario usuario = responseToObject(response, Usuario.class);

			assertNotNull(response.cookies.get(Play.configuration.get("portalSeguranca.session.cookie")));
			assertNotNull(usuario);

		}

		@Test
		public void naoDeveLogarUsuarioInativo() {

			Cache.clear();

			Usuario usuarioInativo = new UsuarioBuilder()
					.comLogin("39534348961")
					.comSenha("123456")
					.comStatusAtivo(false)
					.save();

			commitTransaction();

			Http.Request request = getDefaultRequestWithToken();
			request.params.put("username", "39534348961");
			request.params.put("password", "123456");

			Http.Response response = POST(request, "/external/login", "application/json", "");

			assertEquals(response.status.longValue(), Http.StatusCode.INTERNAL_ERROR);

			usuarioInativo.delete();

		}

	}

	public static class OAuthToken extends BaseFunctionalTest {

		@BeforeClass
		public static void beforeClass() {

			DatabaseCleaner.cleanAll();

		}

		@Test
		public void deveGerarToken() {

			Http.Request request = getAccessTokenRequest();

			Http.Response response = POST(request, "/external/token", "application/x-www-form-urlencoded", request.body);

			Token token = responseToObject(response, Token.class);

			assertEquals(response.status.intValue(), Http.StatusCode.OK);

			assertNotNull(token);
			assertNotNull(token.access_token);
			assertNotNull((String) token.expires_in);
			assertNotNull(token.token_type);

			if(Boolean.getBoolean(Play.configuration.getProperty("oAuth.token.refreshToken"))) {

				assertNotNull(token.refresh_token);

			} else {

				assertNull(token.refresh_token);

			}

			modulo.refresh();

			assertNotNull(modulo.oAuthClient.accessToken);

			modulo.delete();

		}

	}

}
