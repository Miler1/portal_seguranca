package functional;

import builders.AgendaDesativacaoBuilder;
import builders.UsuarioBuilder;
import models.AgendaDesativacao;
import models.Message;
import models.Usuario;
import models.permissoes.AcaoSistema;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.i18n.Messages;
import play.mvc.Http;
import play.mvc.Router;
import utils.DatabaseCleaner;

import java.util.concurrent.ExecutionException;

public class AgendasDesativacaoTest {

	public static class Cadastro extends BaseFunctionalTest {

		private static String url;
		private static Usuario usuarioComPermissao, usuarioSemPermissao;
		private static AgendaDesativacao agendaDesativacaoValida;

		@BeforeClass
		public static void beforeClass() {

			DatabaseCleaner.cleanAll();

			usuarioComPermissao = buildUsuarioLogin(AcaoSistema.ATIVAR_DESATIVAR_USUARIO);
			usuarioSemPermissao = buildUsuarioLogin(AcaoSistema.VISUALIZAR_USUARIO);

			commitTransaction();

			url = Router.reverse("AgendasDesativacao.create").url;

			assertEquals("/agendaDesativacao", url);

		}

		@AfterClass
		public static void afterClass() {

			DatabaseCleaner.cleanAll();

		}

		@Before
		public void before() {

			login(usuarioComPermissao.login, UsuarioBuilder.SENHA_PADRAO);

		}

		@Test
		public void deveCadastrar() throws ExecutionException, InterruptedException {

			agendaDesativacaoValida = new AgendaDesativacaoBuilder().build();

			commitTransaction();

			Http.Response response = POSTJson(url, agendaDesativacaoValida);

			assertIsOk(response);

			Message message = responseToObject(response, Message.class);
			AgendaDesativacao agendaDesativacao = AgendaDesativacao.find("usuario.id", agendaDesativacaoValida.usuario.id).first();

			assertNotNull(agendaDesativacao);

			agendaDesativacao._delete();
			agendaDesativacao.usuario._delete();

			assertEquals(Messages.get("agendas.cadastro.sucesso"), message.getText());

		}

		@Test
		public void naoDeveCadastrarSemPermissao() {

			logout();

			login(usuarioSemPermissao.login, UsuarioBuilder.SENHA_PADRAO);

			agendaDesativacaoValida = new AgendaDesativacaoBuilder().build();

			commitTransaction();

			Http.Response response = POSTJson(url, agendaDesativacaoValida);

			assertStatus(Http.StatusCode.FORBIDDEN, response);

			assertNull(agendaDesativacaoValida.id);

		}

		@Test
		public void naoDeveCadastrarSemUsuarioLogado() {

			logout();

			agendaDesativacaoValida = new AgendaDesativacaoBuilder().build();

			commitTransaction();

			Http.Response response = POSTJson(url, agendaDesativacaoValida);

			assertStatus(Http.StatusCode.UNAUTHORIZED, response);

			assertNull(agendaDesativacaoValida.id);

		}

	}

}
