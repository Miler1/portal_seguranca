package functional;

import builders.ModuloBuilder;
import builders.UsuarioBuilder;
import models.FiltroModulo;
import models.Modulo;
import models.Pagination;
import models.Usuario;
import models.permissoes.AcaoSistema;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Router;
import utils.DatabaseCleaner;

public class ModulosTest {

	public static class Listagem extends BaseFunctionalTest {

		private static Usuario usuarioComPermissao;

		@BeforeClass
		public static void beforeClass() {

			DatabaseCleaner.cleanAll();

			usuarioComPermissao = buildUsuarioLogin(AcaoSistema.PESQUISAR_MODULOS);

			commitTransaction();

		}

		@Before
		public void before() {

			login(usuarioComPermissao.login, UsuarioBuilder.SENHA_PADRAO);

		}

		@AfterClass
		public static void afterClass() {

			DatabaseCleaner.cleanAll();

		}

		@Test
		public void deveListar() {

			for(int i = 0; i < 30; i++) {

				new ModuloBuilder().save();

			}

			commitTransaction();

			FiltroModulo filtro = new FiltroModulo();
			filtro.ativo = null;
			filtro.sigla = null;
			filtro.nome = null;
			filtro.ordenacao = FiltroModulo.Ordenacao.NOME_ASC;
			filtro.tamanhoPagina = 10;
			filtro.numeroPagina = 1;

			String url = Router.reverse("Modulos.findByFilter").url;

			assertEquals("/modulos", url);

			Http.Response response = POSTJson(url, filtro);

			assertIsOk(response);

			Pagination paginacao = getResponseAsPaginationOf(response, Modulo.class);

			assertNotNull(paginacao.getTotalItems());
			assertNotNull(paginacao.getPageItems());

			assertEquals(10, paginacao.getPageItems().size());

		}

	}

}
