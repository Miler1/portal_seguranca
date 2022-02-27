package functional.serializers;

import flexjson.JSONSerializer;
import play.Play;
import serializers.DateSerializer;

import java.util.Date;

public class ModulosSerializer {

	public static JSONSerializer findByFilter;

	static {

		boolean prettyPrint = Play.mode == Play.Mode.DEV;

		findByFilter = new JSONSerializer()
				.include(
						"ordenacao",
						"tamanhoPagina",
						"numeroPagina",
						"sigla",
						"nome",
						"ativo"
				)
				.exclude("*")
				.transform(DateSerializer.getTransformer(), Date.class)
				.prettyPrint(prettyPrint);

	}

}
