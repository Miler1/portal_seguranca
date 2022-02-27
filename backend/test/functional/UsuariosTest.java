package functional;

import builders.LinkAcessoBuilder;
import builders.PerfilBuilder;
import builders.UsuarioBuilder;
import models.Message;
import models.Perfil;
import models.ReenvioEmail;
import models.Usuario;
import models.permissoes.AcaoSistema;
import org.apache.commons.mail.Email;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import play.i18n.Messages;
import play.libs.Crypto;
import play.libs.Mail;
import play.libs.mail.MailSystem;
import play.mvc.Http;
import play.mvc.Http.Response;
import play.mvc.Router;
import secure.SHA512Generator;
import utils.DatabaseCleaner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class UsuariosTest extends BaseFunctionalTest {

	private static Usuario usuarioComPermissao, usuarioParaRecuperacaoSenha, usuarioParaAlteracaoSenha;

	@BeforeClass
	public static void beforeClass() {

		DatabaseCleaner.cleanAll();

		usuarioComPermissao = buildUsuarioLogin(AcaoSistema.PESQUISAR_USUARIOS);
		usuarioParaRecuperacaoSenha = new UsuarioBuilder().save();
		usuarioParaAlteracaoSenha = new UsuarioBuilder().save();

		commitTransaction();

	}

	@AfterClass
	public static void afterClass() {

		usuarioParaRecuperacaoSenha._delete();
		usuarioParaAlteracaoSenha._delete();

	}

	@Test
	public void deveRetornarAcoesPermitidas() {

		login(usuarioComPermissao.login, UsuarioBuilder.SENHA_PADRAO);

		String url = Router.reverse("Usuarios.getAcoesPermitidas").url;

		assertEquals("/usuario/acoesPermitidas", url);

		Response response = GET(url);

		List<Integer> idsAcoesPermitidas = getResponseAsListOf(response, Integer.class);

		assertNotNull(idsAcoesPermitidas);
		assertEquals(1, idsAcoesPermitidas.size());
		assertEquals(AcaoSistema.PESQUISAR_USUARIOS, idsAcoesPermitidas.get(0));

	}

	public static class Cadastro extends BaseFunctionalTest {

		private static String url;
		private static Perfil perfilValido, perfilInvalido;
		private static Usuario usuarioInvalido, usuarioValido, usuarioPermissao, usuarioParaTesteDeEmailComErro, usuarioParaTesteDeEmailComExcecao;
		private static Usuario usuarioComPermissao, usuarioSemPermissao;

		@BeforeClass
		public static void beforeClass() {

			DatabaseCleaner.cleanAll();

			usuarioComPermissao = buildUsuarioLogin(AcaoSistema.CADASTRAR_USUARIO);

			perfilValido = new PerfilBuilder().save();

			usuarioValido = new UsuarioBuilder()
					.comPerfil(perfilValido)
					.comEmail("enrico.navarro@gt4w.com.br")
					.build();

			perfilInvalido = new PerfilBuilder().build();

			usuarioInvalido = new UsuarioBuilder()
					.comPerfil(perfilInvalido)
					.comLoginRandomico()
					.build();

			usuarioParaTesteDeEmailComErro = new UsuarioBuilder()
					.comPerfil(perfilValido)
					.comLoginRandomico()
					.comEmail("teste.erro@gt4w.com.br")
					.build();

			usuarioParaTesteDeEmailComExcecao = new UsuarioBuilder()
					.comPerfil(perfilValido)
					.comLoginRandomico()
					.comEmail("teste.excecao@gt4w.com.br")
					.build();

			usuarioSemPermissao = buildUsuarioLogin(AcaoSistema.VISUALIZAR_USUARIO);

			usuarioPermissao =  new UsuarioBuilder()
					.comPerfil(perfilValido)
					.comLoginRandomico()
					.build();

			commitTransaction();

			url = Router.reverse("Usuarios.create").url;
			assertEquals("/usuario", url);

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

			MailSystem mailSystem = Mockito.mock(MailSystem.class);
			Future<Boolean> future = Mockito.mock(Future.class);
			Mockito.when(future.get()).thenReturn(true);
			Mockito.when(future.isDone()).thenReturn(true);
			Mockito.when(mailSystem.sendMessage(Mockito.any(Email.class))).thenReturn(future);
			Mail.useMailSystem(mailSystem);

			Response response = POSTJson(url, usuarioValido);

			assertIsOk(response);

			Message message = responseToObject(response, Message.class);

			Usuario usuario = Usuario.find("login", usuarioValido.login).first();

			assertNotNull(usuario);

			usuario._delete();

			assertEquals(Messages.get("usuarios.cadastro.sucesso"), message.getText());

		}

		@Test
		public void naoDeveCadastrar() {

			Response response = POSTJson(url, usuarioInvalido);

			assertStatus(422, response);

			Usuario usuario = Usuario.find("login", usuarioInvalido.login).first();

			assertNull(usuario);

		}

		@Test
		public void naoDeveCadastrarSemPermissao() {

			logout();

			login(usuarioSemPermissao.login, UsuarioBuilder.SENHA_PADRAO);

			Response response = POSTJson(url, usuarioPermissao);

			assertStatus(Http.StatusCode.FORBIDDEN, response);

			Usuario usuarioSalvo = Usuario.find("login", usuarioPermissao.login).first();

			assertNull(usuarioSalvo);

		}

		@Test
		public void naoDeveCadastrarSemUsuarioLogado() {

			logout();

			Response response = POSTJson(url, usuarioPermissao);

			assertStatus(Http.StatusCode.UNAUTHORIZED, response);

			Usuario usuarioSalvo = Usuario.find("login", usuarioPermissao.login).first();

			assertNull(usuarioSalvo);

		}

		@Test
		public void deveCadastrarQuandoEnvioDeEmailFalharESalvarReenvio() throws InterruptedException, ExecutionException {

			MailSystem mailSystem = Mockito.mock(MailSystem.class);
			Future<Boolean> future = Mockito.mock(Future.class);
			Mockito.when(future.isDone()).thenReturn(true);
			Mockito.when(future.get()).thenReturn(false);
			Mockito.when(mailSystem.sendMessage(Mockito.any(Email.class))).thenReturn(future);
			Mail.useMailSystem(mailSystem);

			Response response = POSTJson(url, usuarioParaTesteDeEmailComErro);

			assertStatus(Http.StatusCode.OK, response);

			Usuario usuario = Usuario.find("login", usuarioParaTesteDeEmailComErro.login).first();

			assertNotNull(usuario);

			ReenvioEmail reenvioEmail = ReenvioEmail.find("FROM ReenvioEmail re JOIN FETCH re.usuario usuario WHERE usuario.id = :id")
					.setParameter("id", usuario.id)
					.first();

			assertNotNull(reenvioEmail);

			reenvioEmail.delete();
			usuario.delete();

		}

		@Test
		public void naoDeveCadastrarQuandoEnvioDeEmailFalharPorExcecao() throws InterruptedException, ExecutionException {

			MailSystem mailSystem = Mockito.mock(MailSystem.class);
			Future<Boolean> future = Mockito.mock(Future.class);
			Mockito.when(future.isDone()).thenReturn(true);
			Mockito.when(future.get()).thenReturn(false);
			Mockito.when(mailSystem.sendMessage(Mockito.any(Email.class))).thenThrow(Exception.class);
			Mail.useMailSystem(mailSystem);

			Response response = POSTJson(url, usuarioParaTesteDeEmailComExcecao);

			assertStatus(Http.StatusCode.INTERNAL_ERROR, response);

			Usuario usuario = Usuario.find("login", usuarioParaTesteDeEmailComExcecao.login).first();

			assertNull(usuario);

		}

	}

	public static class ValidacaoToken extends BaseFunctionalTest {

		private static Perfil perfilValido;
		private static Usuario usuarioComPermissao;

		@BeforeClass
		public static void beforeClass() throws ExecutionException, InterruptedException {

			DatabaseCleaner.cleanAll();

			usuarioComPermissao = buildUsuarioLogin(AcaoSistema.CADASTRAR_USUARIO);

			perfilValido = new PerfilBuilder().save();

			MailSystem mailSystem = Mockito.mock(MailSystem.class);
			Future<Boolean> future = Mockito.mock(Future.class);
			Mockito.when(future.get()).thenReturn(true);
			Mockito.when(future.isDone()).thenReturn(true);
			Mockito.when(mailSystem.sendMessage(Mockito.any(Email.class))).thenReturn(future);
			Mail.useMailSystem(mailSystem);

			commitTransaction();

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
		public void deveValidar() {

			Usuario usuarioValido = new UsuarioBuilder()
					.comPerfil(perfilValido)
					.comLoginRandomico()
					.comEmail("teste.Validacao@gt4w.com.br")
					.semSenha()
					.build();

			usuarioValido.linkAcesso = new LinkAcessoBuilder()
					.comToken(SHA512Generator.generateValue(usuarioValido.login + Long.toString(new Date().getTime()) + Math.random()))
					.comUsuario(usuarioValido)
					.build();

			usuarioValido.save();

			commitTransaction();

			Map<String, Object> args = new HashMap<>();
			args.put("token", usuarioValido.linkAcesso.token);

			String url = Router.reverse("Usuarios.validateToken", args).url;

			assertEquals("/usuario/validarToken/" + usuarioValido.linkAcesso.token, url);

			Response response = GET(url);

			assertIsOk(response);

			Usuario usuario = responseToObject(response, Usuario.class);

			assertEquals(usuarioValido.id, usuario.id);

		}

	}

	public static class CriacaoSenha extends BaseFunctionalTest {

		private static Perfil perfilValido;
		private static Usuario usuarioValido;
		private static String token, tokenInvalido, senha;

		@BeforeClass
		public static void beforeClass() throws ExecutionException, InterruptedException {

			DatabaseCleaner.cleanAll();

			perfilValido = new PerfilBuilder().save();

			MailSystem mailSystem = Mockito.mock(MailSystem.class);
			Future<Boolean> future = Mockito.mock(Future.class);
			Mockito.when(future.get()).thenReturn(true);
			Mockito.when(future.isDone()).thenReturn(true);
			Mockito.when(mailSystem.sendMessage(Mockito.any(Email.class))).thenReturn(future);
			Mail.useMailSystem(mailSystem);

			usuarioValido = new UsuarioBuilder()
					.comPerfil(perfilValido)
					.comLoginRandomico()
					.comEmail("teste.criacaoSenha@gt4w.com.br")
					.save();

			commitTransaction();

			usuarioValido.refresh();

			token = "?id=" + usuarioValido.id.toString() + "?login=" + usuarioValido.login + "?reset=false";
			token = Crypto.encryptAES(token);

			tokenInvalido = "?id=999999" + "?login=" + usuarioValido.login + "?reset=false";
			tokenInvalido = Crypto.encryptAES(tokenInvalido);

			senha = "123456789";

		}

		@Test
		public void deveCriarSenha() {

			Map<String, Object> args = new HashMap<>();
			args.put("id", usuarioValido.id.toString());
			args.put("senha", senha);

			String url = Router.reverse("Usuarios.createPassword", args).url;

			assertEquals("/usuario/definirSenhaPrimeiroAcesso?senha=" + senha + "&id=" +
					usuarioValido.id.toString(), url);

			Response response = PUTJson(url, "");

			assertIsOk(response);

			Message message = responseToObject(response, Message.class);

			Usuario usuario = Usuario.findById(usuarioValido.id);

			usuario.refresh();

			assertNotNull(usuario.senha);

			assertEquals(Messages.get("usuarios.primeiroAcesso.sucesso"), message.getText());

		}

		@Test
		public void naoDeveCriarSemUsuarioCadastrado() {

			Map<String, Object> args = new HashMap<>();
			args.put("id", "99999");
			args.put("senha", senha);

			String url = Router.reverse("Usuarios.createPassword", args).url;

			assertEquals("/usuario/definirSenhaPrimeiroAcesso?senha=" + senha +
					"&id=99999", url);

			Response response = PUTJson(url, "");

			assertIsNotFound(response);

		}

	}

	public static class RedefinicaoSenha extends BaseFunctionalTest {

		private static Perfil perfilValido;
		private static Usuario usuarioValido;
		private static String token, tokenInvalido, senha;

		@BeforeClass
		public static void beforeClass() throws ExecutionException, InterruptedException {

			DatabaseCleaner.cleanAll();

			perfilValido = new PerfilBuilder().save();

			MailSystem mailSystem = Mockito.mock(MailSystem.class);
			Future<Boolean> future = Mockito.mock(Future.class);
			Mockito.when(future.get()).thenReturn(true);
			Mockito.when(future.isDone()).thenReturn(true);
			Mockito.when(mailSystem.sendMessage(Mockito.any(Email.class))).thenReturn(future);
			Mail.useMailSystem(mailSystem);

			usuarioValido = new UsuarioBuilder()
					.comPerfil(perfilValido)
					.comLoginRandomico()
					.comSenha("123456")
					.comEmail("teste.criacaoSenha@gt4w.com.br")
					.save();

			commitTransaction();

			usuarioValido.refresh();

			token = "?id=" + usuarioValido.id.toString() + "?login=" + usuarioValido.login + "?reset=true";
			token = Crypto.encryptAES(token);

			tokenInvalido = "?id=999999" + "?login=" + usuarioValido.login + "?reset=true";
			tokenInvalido = Crypto.encryptAES(tokenInvalido);

			senha = "123456789";

		}

		@Test
		public void deveRedefinirSenha() {

			Map<String, Object> args = new HashMap<>();
			args.put("id", usuarioValido.id.toString());
			args.put("senha", senha);

			String url = Router.reverse("Usuarios.resetPassword", args).url;

			assertEquals("/usuario/redefinirSenha?senha=" + senha + "&id=" + usuarioValido.id.toString(), url);

			Response response = PUTJson(url, "");

			assertIsOk(response);

			Message message = responseToObject(response, Message.class);

			Usuario usuario = Usuario.findById(usuarioValido.id);

			usuario.refresh();

			assertEquals(SHA512Generator.generateValue(senha), usuario.senha);

			assertEquals(Messages.get("usuarios.redefinir.sucesso"), message.getText());

		}

		@Test
		public void naoDeveRedefinirSemUsuarioCadastrado() {

			Map<String, Object> args = new HashMap<>();
			args.put("id", "99999");
			args.put("senha", senha);

			String url = Router.reverse("Usuarios.resetPassword", args).url;

			assertEquals("/usuario/redefinirSenha?senha=" + senha + "&id=99999", url);

			Response response = PUTJson(url, "");

			assertIsNotFound(response);

		}

	}

	public static class Ativacao extends BaseFunctionalTest {

		private static Perfil perfilValido;
		private static String url;
		private static Usuario usuarioDesativado, usuarioComPermissao;

		@BeforeClass
		public static void beforeClass() {

			DatabaseCleaner.cleanAll();

			usuarioComPermissao = buildUsuarioLogin(AcaoSistema.ATIVAR_DESATIVAR_USUARIO);

			perfilValido = new PerfilBuilder().save();

			commitTransaction();

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
		public void deveAtivar() {

			usuarioDesativado = new UsuarioBuilder()
					.comPerfil(perfilValido)
					.comStatusAtivo(false)
					.comLoginRandomico()
					.comSenha("123456")
					.comEmail("teste.Validacao@gt4w.com.br")
					.save();

			commitTransaction();

			Map<String, Object> args = new HashMap<>();
			args.put("id", usuarioDesativado.id.toString());

			url = Router.reverse("Usuarios.activate", args).url;

			Response response = PUT(url, "application/x-www-form-urlencoded", args.toString());

			assertIsOk(response);

			Message message = responseToObject(response, Message.class);

			Usuario usuario = Usuario.find("login", usuarioDesativado.login).first();

			usuarioDesativado.refresh();

			assertNotNull(usuario);
			assertTrue(usuario.ativo);

			usuario._delete();

			assertEquals(Messages.get("usuarios.ativo.sucesso"), message.getText());

		}

	}

}