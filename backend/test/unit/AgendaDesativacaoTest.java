package unit;

import builders.AgendaDesativacaoBuilder;
import builders.UsuarioBuilder;
import exceptions.ValidationException;
import functional.BaseFunctionalTest;
import models.AgendaDesativacao;
import models.Message;
import models.Motivo;
import models.Usuario;
import models.permissoes.AcaoSistema;
import org.apache.commons.lang.time.DateUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import play.i18n.Messages;
import play.mvc.Http;
import play.mvc.Router;
import utils.DatabaseCleaner;

import java.util.Date;

public class AgendaDesativacaoTest {

	protected static Usuario usuarioLogado;
	protected static String url;
	protected static Integer UNPROCESSABLE_ENTITY = 422;

	public static void beforeClassPadrao() {

		DatabaseCleaner.cleanAll();

		usuarioLogado = BaseFunctionalTest.buildUsuarioLogin(AcaoSistema.ATIVAR_DESATIVAR_USUARIO);

		BaseFunctionalTest.commitTransaction();

		BaseFunctionalTest.login(usuarioLogado.login, "123456");

	}

	public static class Cadastro extends BaseUnitTest {

		private static AgendaDesativacao agendaDesativacao;

		@BeforeClass
		public static void beforeClass() {

			beforeClassPadrao();

			url = Router.reverse("AgendasDesativacao.create").url;

			assertEquals("/agendaDesativacao", url);

		}

		@AfterClass
		public static void afterClass() {

			DatabaseCleaner.cleanAll();

		}

		@Test
		public void deveCadastrar() {

			agendaDesativacao = new AgendaDesativacaoBuilder().build();

			BaseFunctionalTest.commitTransaction();

			Http.Response response = BaseFunctionalTest.POSTJson(url, agendaDesativacao);

			BaseFunctionalTest.assertIsOk(response);

			Message message = BaseFunctionalTest.responseToObject(response, Message.class);

			assertEquals(Messages.get("agendas.cadastro.sucesso"), message.getText());

			assertNotNull(AgendaDesativacao.find("usuario.id", usuarioLogado.id));

			agendaDesativacao._delete();

		}

		@Test
		public void deveCadastrarDesativandoUsuarioQuandoTempoIndeterminado() {

			agendaDesativacao = new AgendaDesativacaoBuilder()
					.statusTempoIndeterminado(true)
					.build();

			BaseFunctionalTest.commitTransaction();

			Http.Response response = BaseFunctionalTest.POSTJson(url, agendaDesativacao);

			BaseFunctionalTest.assertIsOk(response);

			Message message = BaseFunctionalTest.responseToObject(response, Message.class);

			assertEquals(Messages.get("agendas.cadastro.sucesso"), message.getText());

			agendaDesativacao.usuario.refresh();

			assertFalse(agendaDesativacao.usuario.ativo);

			agendaDesativacao._delete();

		}

		@Test
		public void deveCadastrarComDataInicioEFim() {

			agendaDesativacao = new AgendaDesativacaoBuilder()
					.comDataInicio(new Date())
					.comDataFim(DateUtils.addDays(new Date(), 1))
					.statusTempoIndeterminado(false)
					.build();

			BaseFunctionalTest.commitTransaction();

			Http.Response response = BaseFunctionalTest.POSTJson(url, agendaDesativacao);

			BaseFunctionalTest.assertIsOk(response);

			Message message = BaseFunctionalTest.responseToObject(response, Message.class);

			assertEquals(Messages.get("agendas.cadastro.sucesso"), message.getText());

			assertNotNull(AgendaDesativacao.find("id_usuario", usuarioLogado.id));

			agendaDesativacao._delete();

		}

		@Test
		public void naoDeveCadastrarSemUsuarioAlvo() {

			agendaDesativacao = new AgendaDesativacaoBuilder()
					.semUsuarioAlvo()
					.build();

			Http.Response response = BaseFunctionalTest.POSTJson(url, agendaDesativacao);

			assertValidationExceptionAndMessage("agendas.usuario.obrigatorio");

			if(response.status.equals(UNPROCESSABLE_ENTITY)) {

				throw new ValidationException().userMessage(response.out.toString());

			}

		}

		@Test
		public void naoDeveCadastrarSemDataInicioSeTempoIndeterminadoFalse() {

			agendaDesativacao = new AgendaDesativacaoBuilder()
					.semDataInicio()
					.statusTempoIndeterminado(false)
					.build();

			Http.Response response = BaseFunctionalTest.POSTJson(url, agendaDesativacao);

			assertValidationExceptionAndMessage("agendas.dataInicio.obrigatorio");

			if (response.status.equals(UNPROCESSABLE_ENTITY)) {

				throw new ValidationException().userMessage(response.out.toString());

			}

		}

		@Test
		public void naoDeveCadastrarSemDataFimSeTempoIndeterminadoFalse() {

			agendaDesativacao = new AgendaDesativacaoBuilder()
					.semDataFim()
					.statusTempoIndeterminado(false)
					.build();

			Http.Response response = BaseFunctionalTest.POSTJson(url, agendaDesativacao);

			assertValidationExceptionAndMessage("agendas.dataFim.obrigatorio");

			if(response.status.equals(UNPROCESSABLE_ENTITY)) {

				throw new ValidationException().userMessage(response.out.toString());

			}

		}

		@Test
		public void naoDeveCadastrarSemMotivo() {

			agendaDesativacao = new AgendaDesativacaoBuilder()
					.semMotivo()
					.build();

			Http.Response response = BaseFunctionalTest.POSTJson(url, agendaDesativacao);

			assertValidationExceptionAndMessage("agendas.motivo.obrigatorio");

			if(response.status.equals(UNPROCESSABLE_ENTITY)) {

				throw new ValidationException().userMessage(response.out.toString());

			}

		}

		@Test
		public void naoDeveCadastrarSemDescricaoSeMotivoOutro() {

			agendaDesativacao = new AgendaDesativacaoBuilder()
					.comMotivo((Motivo) Motivo.findById(Motivo.OUTRO))
					.build();

			Http.Response response = BaseFunctionalTest.POSTJson(url, agendaDesativacao);

			assertValidationExceptionAndMessage("agendas.descricao.obrigatorio");

			if(response.status.equals(UNPROCESSABLE_ENTITY)) {

				throw new ValidationException().userMessage(response.out.toString());

			}

		}

		@Test
		public void naoDeveCadastrarSeUsuarioAlvoDesativado() {

			agendaDesativacao = new AgendaDesativacaoBuilder()
					.comUsuario(new UsuarioBuilder().comLoginRandomico().comSenha("123456").comStatusAtivo(false).save())
					.build();

			BaseFunctionalTest.commitTransaction();

			Http.Response response = BaseFunctionalTest.POSTJson(url, agendaDesativacao);

			assertValidationExceptionAndMessage("agendas.usuario.desativado");

			if(response.status.equals(UNPROCESSABLE_ENTITY)) {

				throw new ValidationException().userMessage(response.out.toString());

			}

		}

		@Test
		public void naoDeveCadastrarSeDataInicioAposDataFimViceVersa() {

			Date dataInicio = new Date();
			Date dataFim = DateUtils.addDays(dataInicio, - 5);

			agendaDesativacao = new AgendaDesativacaoBuilder()
					.comDataInicio(dataInicio)
					.comDataFim(dataFim)
					.statusTempoIndeterminado(false)
					.build();

			BaseFunctionalTest.commitTransaction();

			Http.Response response = BaseFunctionalTest.POSTJson(url, agendaDesativacao);

			assertValidationExceptionAndMessage("agendas.datas.invalidas");

			if(response.status.equals(UNPROCESSABLE_ENTITY)) {

				throw new ValidationException().userMessage(response.out.toString());

			}

		}

		@Test
		public void naoDeveCadastrarSeUsuarioLogado() {

			Usuario usuario = new UsuarioBuilder()
					.comLoginRandomico()
					.comSenha("123456")
					.comStatusAtivo(true)
					.save();

			agendaDesativacao = new AgendaDesativacaoBuilder()
					.comUsuario(usuario)
					.comUsuarioSolicitante(usuario)
					.build();

			BaseFunctionalTest.commitTransaction();

			Http.Response response = BaseFunctionalTest.POSTJson(url, agendaDesativacao);

			assertValidationExceptionAndMessage("agendas.usuario.logado");

			if(response.status.equals(UNPROCESSABLE_ENTITY)) {

				throw new ValidationException().userMessage(response.out.toString());

			}

		}

	}

}
