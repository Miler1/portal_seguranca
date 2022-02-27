package serializers;

import flexjson.JSONSerializer;
import play.Play;
import play.Play.Mode;

import java.util.Date;

public class PerfilSerializer {

	public static JSONSerializer findAll;
	public static JSONSerializer findByFilter;
	public static JSONSerializer find;
	public static JSONSerializer findAllWithPermissoes;

	static {

		boolean prettyPrint = Play.mode == Mode.DEV;

		findAll = new JSONSerializer()
				.include(
						"id",
						"codigo",
						"nome"
				)
				.exclude("*")
				.transform(DateSerializer.getTransformerWithTimetable(), Date.class)
				.prettyPrint(prettyPrint);

		findByFilter = new JSONSerializer()
				.include(
						"pageItems.id",
						"pageItems.nome",
						"pageItems.codigo",
						"pageItems.ativo",
						"pageItems.moduloPertencente.sigla",
						"pageItems.cadastroEntradaUnica",
						"totalItems"
				)
				.exclude("*")
				.transform(DateSerializer.getTransformerWithTimetable(), Date.class)
				.prettyPrint(prettyPrint);

		find = new JSONSerializer()
				.include(
						"id",
						"nome",
						"codigo",
						"ativo",
						"avatar",
						"modulosTemPermissoes.id",
						"modulosTemPermissoes.nome",
						"modulosTemPermissoes.permissoes.id",
						"modulosTemPermissoes.permissoes.nome",
						"modulosTemPermissoes.permissoes.isFromPerfilVisualizar",
						"modulosTemPermissoes.permissoes.codigo"
				)
				.exclude("*")
				.transform(DateSerializer.getTransformerWithTimetable(), Date.class)
				.prettyPrint(prettyPrint);

		findAllWithPermissoes = new JSONSerializer()
			.include(
				"id",
				"codigo",
				"nome",
				"permissoes.id",
				"permissoes.nome",
				"permissoes.codigo",
				"permissoes.modulo.id",
				"permissoes.modulo.nome",
				"permissoes.modulo.sigla"
			)
			.exclude("*")
			.transform(DateSerializer.getTransformerWithTimetable(), Date.class)
			.prettyPrint(prettyPrint);
	}

}
