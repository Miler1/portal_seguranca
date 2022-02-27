package unit;

import builders.ModuloBuilder;
import models.FiltroModulo;
import models.Modulo;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.DatabaseCleaner;

import java.util.List;

public class ModuloTest {

	public static class Listagem extends BaseUnitTest {

		@BeforeClass
		public static void beforeClass() {

			DatabaseCleaner.cleanAll();

		}

		@Test
		public void deveListarOrdenadoPorNomeASC() {

			Modulo modulo = new ModuloBuilder()
					.comNome("A")
					.save();

			Modulo modulo2 = new ModuloBuilder()
					.comNome("B")
					.save();

			FiltroModulo filtro = new FiltroModulo();
			filtro.ordenacao = FiltroModulo.Ordenacao.NOME_ASC;
			filtro.nome = null;
			filtro.sigla = null;
			filtro.ativo = null;
			filtro.numeroPagina = 1;
			filtro.tamanhoPagina = 10;

			List<Modulo> modulos = Modulo.findByFilter(filtro);

			assertEquals(modulos.get(0).nome, "A");
			assertEquals(modulos.get(1).nome, "B");
			assertEquals(modulos.size(), 3);

			modulo._delete();
			modulo2.delete();

		}

		@Test
		public void deveListarOrdenadoPorNomeDESC() {

			Modulo modulo = new ModuloBuilder()
					.comNome("A")
					.save();

			Modulo modulo2 = new ModuloBuilder()
					.comNome("B")
					.save();

			FiltroModulo filtro = new FiltroModulo();
			filtro.ordenacao = FiltroModulo.Ordenacao.NOME_DESC;
			filtro.nome = null;
			filtro.sigla = null;
			filtro.ativo = null;
			filtro.numeroPagina = 1;
			filtro.tamanhoPagina = 10;

			List<Modulo> modulos = Modulo.findByFilter(filtro);

			assertEquals(modulos.get(1).nome, "B");
			assertEquals(modulos.get(2).nome, "A");
			assertEquals(modulos.size(), 3);

			modulo._delete();
			modulo2.delete();

		}

		@Test
		public void deveFiltrarPorNome() {

			Modulo modulo = new ModuloBuilder()
					.comNome("Nome Modulo")
					.save();

			FiltroModulo filtro = new FiltroModulo();
			filtro.ordenacao = FiltroModulo.Ordenacao.NOME_ASC;
			filtro.nome = "Nome Modulo";
			filtro.sigla = null;
			filtro.ativo = null;
			filtro.numeroPagina = 1;
			filtro.tamanhoPagina = 10;

			List<Modulo> modulos = Modulo.findByFilter(filtro);

			assertEquals(modulos.get(0).nome, filtro.nome);
			assertEquals(modulos.size(), 1);

			modulo._delete();

		}

		@Test
		public void deveFiltrarPorNomeParcial() {

			Modulo modulo = new ModuloBuilder()
					.comNome("Nome Modulo")
					.save();

			FiltroModulo filtro = new FiltroModulo();
			filtro.ordenacao = FiltroModulo.Ordenacao.NOME_ASC;
			filtro.nome = "Nome";
			filtro.sigla = null;
			filtro.ativo = null;
			filtro.numeroPagina = 1;
			filtro.tamanhoPagina = 10;

			List<Modulo> modulos = Modulo.findByFilter(filtro);

			assertNotEquals(modulos.get(0).nome, filtro.nome);
			assertEquals(modulos.size(), 1);

			modulo._delete();

		}

		@Test
		public void deveFiltrarPorSigla() {

			Modulo modulo = new ModuloBuilder()
					.comSigla("ABC")
					.save();

			FiltroModulo filtro = new FiltroModulo();
			filtro.ordenacao = FiltroModulo.Ordenacao.NOME_ASC;
			filtro.nome = null;
			filtro.sigla = "ABC";
			filtro.ativo = null;
			filtro.numeroPagina = 1;
			filtro.tamanhoPagina = 10;

			List<Modulo> modulos = Modulo.findByFilter(filtro);

			assertEquals(modulos.get(0).sigla, filtro.sigla);
			assertEquals(modulos.size(), 1);

			modulo._delete();

		}

		@Test
		public void deveFiltrarPorStatusAtivo() {

			Modulo modulo = new ModuloBuilder()
					.comStatusAtivo()
					.save();

			FiltroModulo filtro = new FiltroModulo();
			filtro.ordenacao = FiltroModulo.Ordenacao.NOME_ASC;
			filtro.nome = null;
			filtro.sigla = null;
			filtro.ativo = true;
			filtro.numeroPagina = 1;
			filtro.tamanhoPagina = 10;

			List<Modulo> modulos = Modulo.findByFilter(filtro);

			assertEquals(modulos.get(0).ativo, filtro.ativo);
			assertEquals(modulos.size(), 2);

			modulo._delete();

		}

		@Test
		public void deveFiltrarPorStatusInativo() {

			Modulo modulo = new ModuloBuilder()
					.comStatusInativo()
					.save();

			FiltroModulo filtro = new FiltroModulo();
			filtro.ordenacao = FiltroModulo.Ordenacao.NOME_ASC;
			filtro.nome = null;
			filtro.sigla = null;
			filtro.ativo = false;
			filtro.numeroPagina = 1;
			filtro.tamanhoPagina = 10;

			List<Modulo> modulos = Modulo.findByFilter(filtro);

			assertEquals(modulos.get(0).ativo, filtro.ativo);
			assertEquals(modulos.size(), 1);

			modulo._delete();

		}

		@Test
		public void deveFiltrarPorNomeIgnorandoAcentosECaixaAlta() {

			Modulo modulo = new ModuloBuilder()
					.comNome("Módũlô")
					.save();

			FiltroModulo filtro = new FiltroModulo();
			filtro.ordenacao = FiltroModulo.Ordenacao.NOME_ASC;
			filtro.nome = "MODULO";
			filtro.sigla = null;
			filtro.ativo = null;
			filtro.numeroPagina = 1;
			filtro.tamanhoPagina = 10;

			List<Modulo> modulos = Modulo.findByFilter(filtro);

			assertNotEquals(modulos.get(0).nome, filtro.nome);
			assertEquals(modulos.size(), 1);

			modulo._delete();

		}

		@Test
		public void deveFiltrarPorNomeESigla() {

			Modulo modulo = new ModuloBuilder()
					.comNome("Nome Modulo")
					.comSigla("ABC")
					.save();

			FiltroModulo filtro = new FiltroModulo();
			filtro.ordenacao = FiltroModulo.Ordenacao.NOME_ASC;
			filtro.nome = "Nome Modulo";
			filtro.sigla = "ABC";
			filtro.ativo = null;
			filtro.numeroPagina = 1;
			filtro.tamanhoPagina = 10;

			List<Modulo> modulos = Modulo.findByFilter(filtro);

			assertEquals(modulos.get(0).nome, filtro.nome);
			assertEquals(modulos.get(0).sigla, filtro.sigla);
			assertEquals(modulos.size(), 1);

			modulo._delete();

		}

		@Test
		public void deveFiltrarPorNomeEStatus() {

			Modulo modulo = new ModuloBuilder()
					.comNome("Nome Modulo")
					.comStatusAtivo()
					.save();

			FiltroModulo filtro = new FiltroModulo();
			filtro.ordenacao = FiltroModulo.Ordenacao.NOME_ASC;
			filtro.nome = "Nome Modulo";
			filtro.sigla = null;
			filtro.ativo = true;
			filtro.numeroPagina = 1;
			filtro.tamanhoPagina = 10;

			List<Modulo> modulos = Modulo.findByFilter(filtro);

			assertEquals(modulos.get(0).nome, filtro.nome);
			assertEquals(modulos.get(0).ativo, filtro.ativo);
			assertEquals(modulos.size(), 1);

			modulo._delete();

		}

		@Test
		public void deveFiltrarPorNomeIgnorandoAcentosECaixaAltaComStatusESigla() {

			Modulo modulo = new ModuloBuilder()
					.comNome("Módũlô")
					.comStatusAtivo()
					.comSigla("ABC")
					.save();

			FiltroModulo filtro = new FiltroModulo();
			filtro.ordenacao = FiltroModulo.Ordenacao.NOME_ASC;
			filtro.nome = "mODULO";
			filtro.sigla = "ABC";
			filtro.ativo = true;
			filtro.numeroPagina = 1;
			filtro.tamanhoPagina = 10;

			List<Modulo> modulos = Modulo.findByFilter(filtro);

			assertNotEquals(modulos.get(0).nome, filtro.nome);
			assertEquals(modulos.get(0).ativo, filtro.ativo);
			assertEquals(modulos.get(0).sigla, filtro.sigla);
			assertEquals(modulos.size(), 1);

			modulo._delete();

		}

	}

}
