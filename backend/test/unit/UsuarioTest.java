package unit;

import builders.AgendaDesativacaoBuilder;
import builders.LinkAcessoBuilder;
import builders.PerfilBuilder;
import builders.UsuarioBuilder;
import exceptions.ValidationException;
import models.Perfil;
import models.ReenvioEmail;
import models.Usuario;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.mail.Email;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import play.Play;
import play.libs.Mail;
import play.libs.mail.MailSystem;
import secure.SHA512Generator;
import utils.DatabaseCleaner;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class UsuarioTest {

	public static class Cadastro extends BaseUnitTest {

		private static Perfil perfilValido;
		private static Usuario usuarioValido;

		@BeforeClass
		public static void beforeClass() {

			DatabaseCleaner.cleanAll();

			perfilValido = new PerfilBuilder().save();

		}

		@AfterClass
		public static void afterClass() {

			perfilValido._delete();

		}

		@Test
		public void deveCadastrar() throws InterruptedException, ExecutionException {

			MailSystem mailSystem = Mockito.mock(MailSystem.class);

			Future<Boolean> future = Mockito.mock(Future.class);
			Mockito.when(future.get()).thenReturn(true);
			Mockito.when(future.isDone()).thenReturn(true);

			Mockito.when(mailSystem.sendMessage(Mockito.any(Email.class))).thenReturn(future);
			Mail.useMailSystem(mailSystem);

			usuarioValido = new UsuarioBuilder().comPerfil(perfilValido).build();

			usuarioValido.save();

			Usuario usuario = Usuario.findById(usuarioValido.id);

			assertNotNull(usuario);
			assertNotEmpty(usuario.perfis);

			usuario._delete();

		}

		@Test
		public void naoDeveCadastrarSemLogin() {

			assertValidationExceptionAndMessage("usuarios.validacao.login.req");

			Usuario usuario = new UsuarioBuilder()
					.semLogin()
					.build();

			usuario.save();

		}

		@Test
		public void naoDeveCadastrarSemEmail() {

			assertValidationExceptionAndMessage("usuarios.validacao.email.req");

			Usuario usuario = new UsuarioBuilder()
					.semEmail()
					.build();

			usuario.save();

		}

		@Test
		public void naoDeveCadastrarSemPerfil() {

			assertValidationExceptionAndMessage("usuarios.validacao.perfil.req");

			Usuario usuario = new UsuarioBuilder()
					.semPerfil()
					.build();

			usuario.save();

		}

		@Test
		public void naoDeveCadastrarComPerfilInvalido() {

			assertValidationExceptionAndMessage("usuarios.validacao.perfil.invalido");

			Perfil perfil = new PerfilBuilder()
					.comId(999999999)
					.build();

			Usuario usuario = new UsuarioBuilder()
					.comPerfil(perfil)
					.build();

			usuario.save();

		}

		@Test
		public void naoDeveCadastrarComSenhaInvalida() {

			assertValidationExceptionAndMessage("usuarios.validacao.usuario.tamanhoSenha");

			Usuario usuario = new UsuarioBuilder()
					.comSenhaInvalida()
					.build();

			usuario.save();

		}

		@Test
		public void naoDeveCadastrarComLoginNaoUnico() throws ExecutionException, InterruptedException {

			MailSystem mailSystem = Mockito.mock(MailSystem.class);

			Future<Boolean> future = Mockito.mock(Future.class);
			Mockito.when(future.get()).thenReturn(true);
			Mockito.when(future.isDone()).thenReturn(true);

			Mockito.when(mailSystem.sendMessage(Mockito.any(Email.class))).thenReturn(future);
			Mail.useMailSystem(mailSystem);

			try {

				usuarioValido = new UsuarioBuilder().comPerfil(perfilValido).build().save();

				Usuario usuario = new UsuarioBuilder().comPerfil(perfilValido).comEmail("diferente@diferente.com").build();

				assertValidationExceptionAndMessage("usuarios.validacao.login.unico");

				usuario.save();

			} finally {

				usuarioValido._delete();

			}

		}

		@Test
		public void naoDeveCadastrarComEmailNaoUnico() throws ExecutionException, InterruptedException {

			MailSystem mailSystem = Mockito.mock(MailSystem.class);

			Future<Boolean> future = Mockito.mock(Future.class);
			Mockito.when(future.get()).thenReturn(true);
			Mockito.when(future.isDone()).thenReturn(true);

			Mockito.when(mailSystem.sendMessage(Mockito.any(Email.class))).thenReturn(future);
			Mail.useMailSystem(mailSystem);

			try {

				usuarioValido = new UsuarioBuilder().comPerfil(perfilValido).build().save();

				Usuario usuario = new UsuarioBuilder().comPerfil(perfilValido).comLoginRandomico().build();

				assertValidationExceptionAndMessage("usuarios.validacao.email.unico");

				usuario.save();

			} finally {

				usuarioValido._delete();

			}

		}

		@Test
		public void deveCadastrarComErroServidorEmailEDeveSalvarReenvio() throws ExecutionException, InterruptedException {

			MailSystem mailSystem = Mockito.mock(MailSystem.class);
			Future<Boolean> future = Mockito.mock(Future.class);
			Mockito.when(future.get()).thenReturn(false);
			Mockito.when(future.isDone()).thenReturn(true);
			Mockito.when(mailSystem.sendMessage(Mockito.any(Email.class))).thenReturn(future);
			Mail.useMailSystem(mailSystem);

			Usuario usuario = new UsuarioBuilder().comPerfil(perfilValido).comLoginRandomico().build();

			ReenvioEmail reenvioEmail;

			try {

				usuario.save();

				reenvioEmail = ReenvioEmail.find("FROM ReenvioEmail re JOIN FETCH re.usuario usuario WHERE usuario.id = :id")
						.setParameter("id", usuario.id)
						.first();

				assertNotNull(reenvioEmail);

			} finally {

				reenvioEmail = ReenvioEmail.find("FROM ReenvioEmail re JOIN FETCH re.usuario usuario WHERE usuario.id = :id")
						.setParameter("id", usuario.id)
						.first();

				reenvioEmail.delete();
				usuario.delete();

			}

		}

	}

	public static class Edicao extends BaseUnitTest {

		private static Usuario usuario, usuarioSemPerfil;
		private static Perfil perfilValido;

		@BeforeClass
		public static void beforeClass() {

			DatabaseCleaner.cleanAll();

			perfilValido = new PerfilBuilder().save();
			usuario = new UsuarioBuilder().save();

		}

		@Test(expected = ValidationException.class)
		public void naoDeveAtualizarSemPerfil() {

			Usuario usuarioAtualizar = Usuario.findById(usuario.id);

			usuarioSemPerfil = usuario;
			usuarioSemPerfil.perfis.clear();

			assertNotNull(usuarioAtualizar);

			usuarioAtualizar.update(usuarioSemPerfil);

		}

	}

	public static class ValidacaoToken extends BaseUnitTest {

		private static Perfil perfilValido;

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

		}

		@AfterClass
		public static void afterClass() {

			DatabaseCleaner.cleanAll();

		}

		@Test
		public void deveValidarToken() throws Exception {

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

			Usuario usuario = Usuario.validateToken(usuarioValido.linkAcesso.token);

			assertEquals(usuarioValido.id, usuario.id);

			usuarioValido._delete();

		}

		@Test
		public void naoDeveValidarSeTokenJaUsadoParaRedefinirSenha() throws Exception {

			Usuario usuarioValido = new UsuarioBuilder()
					.comPerfil(perfilValido)
					.comLoginRandomico()
					.comEmail("teste.Validacao2@gt4w.com.br")
					.comSenha("123456")
					.build();

			usuarioValido.linkAcesso = new LinkAcessoBuilder()
					.comToken(SHA512Generator.generateValue(usuarioValido.login + Long.toString(new Date().getTime()) + Math.random()))
					.comUsuario(usuarioValido)
					.comRedefinirSenha()
					.build();

			usuarioValido._save();

			String token = usuarioValido.linkAcesso.token;

			// usuarioValido.resetPassword("123456");

			assertValidationExceptionAndMessage("usuarios.token.tokenInvalido");

			try {

				Usuario.validateToken(token);

			} finally {

				usuarioValido._delete();

			}

		}

		@Test
		public void naoDeveValidarSeExpirou() throws Exception {

			Usuario usuarioValido = new UsuarioBuilder()
					.comPerfil(perfilValido)
					.comLoginRandomico()
					.comEmail("teste.Validacao@gt4w.com.br")
					.comSenha("123456")
					.build();

			usuarioValido.linkAcesso = new LinkAcessoBuilder()
					.comToken(SHA512Generator.generateValue(usuarioValido.login + Long.toString(new Date().getTime()) + Math.random()))
					.comUsuario(usuarioValido)
					.comDataSolicitacao(DateUtils.addHours(new Date(), - Integer.parseInt(Play.configuration.getProperty("token.expirationTime"))))
					.comRedefinirSenha()
					.build();

			usuarioValido._save();

			assertValidationExceptionAndMessage("usuarios.token.expirado");

			try {

				Usuario.validateToken(usuarioValido.linkAcesso.token);

			} finally {

				usuarioValido._delete();

			}

		}

	}

	public static class CriacaoSenha extends BaseUnitTest {

		private static Perfil perfilValido;

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

		}

		@AfterClass
		public static void afterClass() {

			perfilValido._delete();

		}

		@Test
		public void deveCriarSenha() throws Exception {

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

			usuarioValido._save();

			// usuarioValido.createPassword("123456789");

			usuarioValido.refresh();

			assertEquals(usuarioValido.senha, SHA512Generator.generateValue("123456789"));

			usuarioValido._delete();

		}

	}

	public static class RedefinicaoSenha extends BaseUnitTest {

		private static Perfil perfilValido;

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

		}

		@AfterClass
		public static void afterClass() {

			perfilValido._delete();

		}

		@Test
		public void deveRedefinirSenha() throws Exception {

			Usuario usuarioValido = new UsuarioBuilder()
					.comPerfil(perfilValido)
					.comLoginRandomico()
					.comEmail("teste.Validacao@gt4w.com.br")
					.comSenha("123456")
					.build();

			usuarioValido.linkAcesso = new LinkAcessoBuilder()
					.comToken(SHA512Generator.generateValue(usuarioValido.login + Long.toString(new Date().getTime()) + Math.random()))
					.comUsuario(usuarioValido)
					.comRedefinirSenha()
					.build();

			// usuarioValido.resetPassword("123456789");

			usuarioValido.refresh();

			assertEquals(usuarioValido.senha, SHA512Generator.generateValue("123456789"));

			usuarioValido._delete();

		}

		@Test
		public void naoDeveRedefinirSenhaSeSenhaAnteriorNull() throws Exception {

			Usuario usuarioValido = new UsuarioBuilder()
					.comPerfil(perfilValido)
					.comLoginRandomico()
					.comEmail("teste.Validacao@gt4w.com.br")
					.semSenha()
					.build();

			usuarioValido.linkAcesso = new LinkAcessoBuilder()
					.comToken(SHA512Generator.generateValue(usuarioValido.login + Long.toString(new Date().getTime()) + Math.random()))
					.comUsuario(usuarioValido)
					.comRedefinirSenha()
					.build();

			usuarioValido._save();

			assertValidationExceptionAndMessage("usuarios.redefinirAtivoReq");

			try {

				// usuarioValido.resetPassword("123456789");

			} finally {

				usuarioValido._delete();

			}

		}

		@Test
		public void naoDeveRedefinirSenhaSeUsuarioNaoEstiverAtivo() throws Exception {

			Usuario usuarioValido = new UsuarioBuilder()
					.comPerfil(perfilValido)
					.comLoginRandomico()
					.comEmail("teste.Validacao@gt4w.com.br")
					.comSenha("123456")
					.comStatusAtivo(false)
					.build();

			usuarioValido.linkAcesso = new LinkAcessoBuilder()
					.comToken(SHA512Generator.generateValue(usuarioValido.login + Long.toString(new Date().getTime()) + Math.random()))
					.comUsuario(usuarioValido)
					.comRedefinirSenha()
					.build();

			usuarioValido._save();

			assertValidationExceptionAndMessage("usuarios.redefinirAtivoReq");

			try {

				// usuarioValido.resetPassword("123456789");

			} finally {

				usuarioValido._delete();

			}

		}

		@Test
		public void deveAlterarSenha() throws Exception {

			Usuario usuarioValido = new UsuarioBuilder()
					.comPerfil(perfilValido)
					.comLoginRandomico()
					.comEmail("teste.Validacao@gt4w.com.br")
					.comSenha("123456")
					.build();

			// usuarioValido.updatePassword("123456", "123456789");

			assertEquals(usuarioValido.senha, SHA512Generator.generateValue("123456789"));

			usuarioValido._delete();

		}

	}

	public static class Ativar extends BaseUnitTest {

		private static Perfil perfilValido;
		private static Usuario usuarioDesativado, usuarioSolicitante;

		@BeforeClass
		public static void beforeClass() {

			DatabaseCleaner.cleanAll();

			perfilValido = new PerfilBuilder().save();

		}

		@AfterClass
		public static void afterClass() {

			DatabaseCleaner.cleanAll();

		}

		@Test
		public void deveAtivarERemoverAgendamento() {

			usuarioSolicitante = new UsuarioBuilder()
					.comPerfil(perfilValido)
					.comStatusAtivo(true)
					.comLoginRandomico()
					.comEmail("teste.teste@gt4w.com.br")
					.save();

			usuarioDesativado = new UsuarioBuilder()
					.comPerfil(perfilValido)
					.comStatusAtivo(false)
					.comSenha("123456")
					.comLoginRandomico()
					.comEmail("teste.Validacao@gt4w.com.br")
					.save();

			new AgendaDesativacaoBuilder()
					.comUsuario(usuarioDesativado)
					.comUsuarioSolicitante(usuarioSolicitante)
					.save();

			usuarioDesativado.refresh();

			usuarioDesativado.activate();

			usuarioDesativado.refresh();

			assertNull(usuarioDesativado.agendaDesativacao);
			assertTrue(usuarioDesativado.ativo);

			usuarioSolicitante._delete();
			usuarioDesativado._delete();

		}

		@Test
		public void naoDeveAtivarSeJaAtivado() {

			Usuario usuarioAtivado = new UsuarioBuilder()
					.comPerfil(perfilValido)
					.comStatusAtivo(true)
					.comSenha("123456")
					.comLoginRandomico()
					.comEmail("teste.Validacao@gt4w.com.br")
					.save();



			assertValidationExceptionAndMessage("usuarios.ativo.jaAtivo");

			try {

				usuarioAtivado.activate();

			} finally {

				usuarioAtivado._delete();

			}

		}

		@Test
		public void naoDeveAtivarSemPrimeiroAcesso() {

			Usuario usuarioAtivado = new UsuarioBuilder()
					.comPerfil(perfilValido)
					.comStatusAtivo(false)
					.comLoginRandomico()
					.semSenha()
					.comEmail("teste.Validacao@gt4w.com.br")
					.save();

			assertValidationExceptionAndMessage("usuarios.ativo.primeiroAcesso.obrigatorio");

			try {

				usuarioAtivado.activate();

			} finally {

				usuarioAtivado._delete();

			}

		}

	}

}
