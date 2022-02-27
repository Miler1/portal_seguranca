package unit.secure.authorization;

import builders.AcaoSistemaBuilder;
import models.Usuario;
import models.permissoes.AcaoSistema;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.test.UnitTest;
import secure.IAuthenticatedUser;
import secure.authorization.Action;
import secure.authorization.Authorization;
import secure.authorization.Permissible;
import utils.DatabaseCleaner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthorizationTest extends UnitTest {

	private static IAuthenticatedUser usuarioLogado;

	@BeforeClass
	public static void setup() {

		DatabaseCleaner.cleanAcoesSistemasFake();

	}

	@Before
	public void before() {

		usuarioLogado = mock(Usuario.class);

	}

	private AcaoSistema createAcaoSistema(String regras) {

		AcaoSistema acaoSistema = new AcaoSistema();
		acaoSistema.id = 1;
		acaoSistema.descricao = "Teste";
		acaoSistema.permissao = "ROLE_TESTE";
		acaoSistema.pacoteRegras = "unit.secure.authorization.rules";

		return acaoSistema;

	}

	@Test
	public void devePrencherTodasAcoesDisponiveisNoPermissivel() {

		AcaoSistema acaoSistema1 = new AcaoSistemaBuilder().comPermissao().save();
		AcaoSistema acaoSistema2 = new AcaoSistemaBuilder().comPermissao().save();
		AcaoSistema acaoSistema3 = new AcaoSistemaBuilder().comPermissao().save();
		AcaoSistema acaoSistema4 = new AcaoSistemaBuilder().comPermissao().save();

		List<Integer> actionsIds = new ArrayList<>();
		actionsIds.add(acaoSistema1.id);
		actionsIds.add(acaoSistema2.id);
		actionsIds.add(acaoSistema3.id);
		actionsIds.add(acaoSistema4.id);

		PermissibleFake permissible = new PermissibleFake();
		permissible.setAvailableActions(actionsIds);

		when(usuarioLogado.hasRole(any(String.class))).thenReturn(true);

		Authorization.getInstance().fillPermittedActions(usuarioLogado, permissible);

		assertPermissivelContemAcoes(permissible, actionsIds);

	}

	@Test
	public void naoDevePreencherAcoesDisponiveisNoPermissivel() {

		AcaoSistema acaoSistema1 = new AcaoSistemaBuilder().comPermissao().save();
		AcaoSistema acaoSistema2 = new AcaoSistemaBuilder().comPermissao().save();

		List<Integer> actionsIds = new ArrayList<>();
		actionsIds.add(acaoSistema1.id);
		actionsIds.add(acaoSistema2.id);

		PermissibleFake permissible = new PermissibleFake();
		permissible.setAvailableActions(actionsIds);

		when(usuarioLogado.hasRole(any(String.class))).thenReturn(false);

		Authorization.getInstance().fillPermittedActions(usuarioLogado, permissible);

		assertPermissivelContemAcoes(permissible, null);

	}

	@Test
	public void devePreencherApenasPrimeiraAcaoNoPermissivel() {

		AcaoSistema acaoSistema1 = new AcaoSistemaBuilder().comPermissao().save();
		AcaoSistema acaoSistema2 = new AcaoSistemaBuilder().comPermissao().save();

		List<Integer> actionsIds = new ArrayList<>();
		actionsIds.add(acaoSistema1.id);
		actionsIds.add(acaoSistema2.id);

		PermissibleFake permissible = new PermissibleFake();
		permissible.setAvailableActions(actionsIds);

		List<Integer> actionsIdsExpected = new ArrayList<>();
		actionsIdsExpected.add(acaoSistema1.id);

		when(usuarioLogado.hasRole(acaoSistema1.getRole())).thenReturn(true);

		Authorization.getInstance().fillPermittedActions(usuarioLogado, permissible);

		assertPermissivelContemAcoes(permissible, actionsIdsExpected);

	}

	@Test
	public void deveRetornarApenasAcoesUsuarioTemPermissao() {

		Action acaoComPermissao = new AcaoSistemaBuilder().comPermissao().build();
		Action acaoSemPermissao = new AcaoSistemaBuilder().comPermissao().build();

		when(usuarioLogado.hasRole(acaoComPermissao.getRole())).thenReturn(true);
		when(usuarioLogado.hasRole(acaoSemPermissao.getRole())).thenReturn(false);

		List<Action> acoes = new ArrayList<>();
		acoes.add(acaoComPermissao);
		acoes.add(acaoSemPermissao);

		List<Integer> idsAcoesPermitidas = Authorization.getInstance().checkPermittedActions(acoes, usuarioLogado);

		assertTrue(idsAcoesPermitidas.contains(acaoComPermissao.getId()));
		assertFalse(idsAcoesPermitidas.contains(acaoSemPermissao.getId()));

	}

	private void assertPermissivelContemAcoes(Permissible permissivel, List<Integer> idsAcoesEsperadas) {

		List<Integer> idsAcoesPermissivel = permissivel.getPermittedActionsIds();

		if (idsAcoesEsperadas == null || idsAcoesEsperadas.isEmpty()) {

			assertTrue(idsAcoesPermissivel == null || idsAcoesPermissivel.isEmpty());
			return;

		}

		assertTrue(idsAcoesPermissivel != null && !idsAcoesPermissivel.isEmpty());
		assertEquals(idsAcoesEsperadas.size(), idsAcoesPermissivel.size());

		for (Integer idAcaoEsperada : idsAcoesEsperadas) {

			assertTrue(idsAcoesPermissivel.contains(idAcaoEsperada));

		}

	}

	public class PermissibleFake implements Permissible {

		List<Action> availableActions;
		List<Integer> permittedActionsIds;

		public void setAvailableActions(List<Integer> actionsIds) {

			if(this.availableActions == null) {

				this.availableActions = new ArrayList<>();

			}

			for(Integer actionId : actionsIds) {

				this.availableActions.add((Action) AcaoSistema.findById(actionId));

			}

		}

		@Override
		public List<Action> getAvailableActions() {

			return availableActions;

		}

		@Override
		public void setPermittedActionsIds(List<Integer> actionsIds) {

			this.permittedActionsIds = actionsIds;

		}

		@Override
		public List<Integer> getPermittedActionsIds() {

			return this.permittedActionsIds;

		}

	}

}
