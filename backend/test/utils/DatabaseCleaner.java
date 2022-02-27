package utils;

import builders.AcaoSistemaBuilder;
import models.*;
import models.permissoes.AcaoSistema;
import play.db.jpa.JPA;
import play.test.Fixtures;

public class DatabaseCleaner {

	public static void cleanAll() {

		Fixtures.delete(ReenvioEmail.class, LinkAcesso.class, AgendaDesativacao.class);
		cleanUsuarios();
		cleanPerfis();
		cleanAcoesSistemasFake();
		cleanPermissoes();
		cleanModulos();
		Fixtures.delete(OAuthClient.class);

	}

	public static void clean(Class... classes) {

		Fixtures.delete(classes);

	}

	private static void cleanPermissoes() {

		JPA.em().createQuery(new StringBuilder().append("DELETE FROM ")
				.append(Permissao.class.getSimpleName())
				.append(" WHERE id_modulo != 1")
				.append("CASCADE")
				.toString())
				.executeUpdate();

	}

	private static void cleanModulos() {

		JPA.em().createQuery(new StringBuilder().append("DELETE FROM ")
				.append(Modulo.class.getSimpleName())
				.append(" WHERE id != 1")
				.append("CASCADE")
				.toString())
				.executeUpdate();

	}

	public static void cleanAcoesSistemasFake() {

		JPA.em().createQuery(new StringBuilder().append("DELETE FROM ")
				.append(AcaoSistema.class.getSimpleName())
				.append(" WHERE id >= :firstIdFake")
				.toString())
			.setParameter("firstIdFake", AcaoSistemaBuilder.FIRST_ID_FAKE)
			.executeUpdate();

		AcaoSistemaBuilder.id = AcaoSistemaBuilder.FIRST_ID_FAKE;

	}

	private static void cleanPerfis() {

		JPA.em().createQuery(new StringBuilder().append("DELETE FROM ")
				.append(Perfil.class.getSimpleName())
				.append(" WHERE id != 1")
				.toString())
				.executeUpdate();

	}

	private static void cleanUsuarios() {

		JPA.em().createQuery(new StringBuilder().append("DELETE FROM ")
				.append(Usuario.class.getSimpleName())
				.append(" WHERE id != 1")
				.append("CASCADE")
				.toString())
				.executeUpdate();

	}

}
