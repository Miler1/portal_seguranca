package serializers;

import flexjson.JSONSerializer;
import play.Play;

import java.util.Date;

public class ModulosSerializer {

	public static JSONSerializer findByFilter, perfisUsuarioLogado, validateModuloFile, oAuthClientCredentials, findById, findWithPerfis, findAll, setoresModulo;

	static {

		boolean prettyPrint = Play.mode == Play.Mode.DEV;

		findByFilter = new JSONSerializer()
				.include(
						"pageItems.id",
						"pageItems.sigla",
						"pageItems.nome",
						"pageItems.ativo",
						"totalItems"
				)
				.exclude("*")
				.transform(DateSerializer.getTransformer(), Date.class)
				.prettyPrint(prettyPrint);

		validateModuloFile = new JSONSerializer()
				.include(
						"nome",
						"sigla",
						"url",
						"ips",
						"descricao",
						"cadastrarPerfis",
						"permissoes.codigo",
						"permissoes.nome",
						"permissoes.perfis.codigo",
						"permissoes.perfis.nome",
						"permissoes.perfilPublico",
						"perfis.codigo",
						"perfis.nome"
				)
				.exclude("*")
				.transform(DateSerializer.getTransformer(), Date.class)
				.prettyPrint(prettyPrint);

		oAuthClientCredentials = new JSONSerializer()
				.include(
						"unHashedOAuthClient.clientId",
						"unHashedOAuthClient.clientSecret"
				)
				.exclude("*")
				.transform(DateSerializer.getTransformer(), Date.class)
				.prettyPrint(prettyPrint);

		perfisUsuarioLogado = new JSONSerializer()
				.include(
						"id",
						"avatar",
						"nome",
						"permissoes.id",
						"permissoes.nome",
						"permissoes.codigo",
						"setores.id",
						"setores.nome",
						"setores.sigla",
						"setores.tipo.id",
						"setores.tipo.nome"
				)
				.exclude("*")
				.transform(DateSerializer.getTransformer(), Date.class)
				.prettyPrint(prettyPrint);

		setoresModulo = new JSONSerializer()
				.include(
						"id",
						"nome",
						"sigla",
						"tipo.id",
						"tipo.nome"
				)
				.exclude("*")
				.transform(DateSerializer.getTransformer(), Date.class)
				.prettyPrint(prettyPrint);

		findById = new JSONSerializer()
				.include(
						"id",
						"nome",
						"sigla",
						"url",
						"ips",
						"descricao",
						"cadastrarPerfis",
						"permissoes.codigo",
						"permissoes.nome",
						"permissoes.perfis.nome",
						"perfis.nome",
						"ativo"
				)
				.exclude("*")
				.transform(DateSerializer.getTransformer(), Date.class)
				.prettyPrint(prettyPrint);

		findWithPerfis =  new JSONSerializer()
				.include(
						"id",
						"nome",
						"sigla",
						"perfis.codigo",
						"perfis.nome",
						"perfis.id"
				)
				.exclude("*")
				.transform(DateSerializer.getTransformer(), Date.class)
				.prettyPrint(prettyPrint);

		findAll = new JSONSerializer()
				.include(
						"id",
						"sigla",
						"nome"
				)
				.exclude("*")
				.prettyPrint(prettyPrint);
	}

}
