package serializers;

import flexjson.JSONSerializer;
import play.Play;
import play.Play.Mode;

public class SecureSerializer {

	public static JSONSerializer externalAuthenticateWithoutSessionKey;
	public static JSONSerializer externalAuthenticate;
	public static JSONSerializer authenticate;
	public static JSONSerializer authenticatedUser;
	public static JSONSerializer token;

	static {

		boolean prettyPrint = Play.mode == Mode.DEV;

		authenticate = new JSONSerializer()
				.include(
						"id",
						"login",
						"email",
						"nome",
						"pessoaId",
						"pessoaCadastro",
						"portalSema",
						"perfilSelecionado.id",
						"perfilSelecionado.nome",
						"perfilSelecionado.codigo",
						"perfilSelecionado.permissoes.id",
						"perfilSelecionado.permissoes.codigo",
						"setorSelecionado.id",
						"setorSelecionado.nome",
						"setorSelecionado.sigla",
						"setores.id",
						"setores.nome",
						"setores.sigla"
				)
				.exclude("*")
				.prettyPrint(prettyPrint);

		token = new JSONSerializer()
				.include(
						"id",
						"login",
						"email",
						"pessoaCadastro",
						"portalSema",
						"perfis.id",
						"perfis.nome",
						"setores.id",
						"setores.nome",
						"setores.sigla",
						"perfilSelecionado.id",
						"perfilSelecionado.nome",
						"perfilSelecionado.codigo",
						"perfilSelecionado.permissoes.id",
						"perfilSelecionado.permissoes.nome",
						"perfilSelecionado.permissoes.codigo",
						"setorSelecionado.id",
						"setorSelecionado.nome",
						"setorSelecionado.sigla"
				)
				.exclude("*")
				.prettyPrint(prettyPrint);

		authenticatedUser = new JSONSerializer()
				.include(
						"id",
						"login",
						"email",
						"pessoaCadastro",
						"portalSema",
						"perfis.id",
						"perfis.nome",
						"perfis.codigo",
						"setores.id",
						"setores.nome",
						"setores.sigla",
						"perfilSelecionado.id",
						"perfilSelecionado.nome",
						"perfilSelecionado.codigo",
						"perfilSelecionado.permissoes.id",
						"perfilSelecionado.permissoes.codigo",
						"setorSelecionado.id",
						"setorSelecionado.nome",
						"setorSelecionado.sigla"
				)
				.exclude("*")
				.prettyPrint(prettyPrint);

		externalAuthenticate = new JSONSerializer()
				.include(
						"id",
						"login",
						"email",
						"nome",
						"pessoaCadastro",
						"portalSema",
						"pessoaId",
						"perfis.nome",
						"perfis.codigo",
						"setores.id",
						"setores.nome",
						"setores.sigla",
						"perfis.permissoes.codigo",
						"perfilSelecionado.id",
						"perfilSelecionado.nome",
						"perfilSelecionado.codigo",
						"perfilSelecionado.permissoes.id",
						"perfilSelecionado.permissoes.codigo",
						"sessionKeyEntradaUnica"
				)
				.exclude("*")
				.prettyPrint(prettyPrint);

		externalAuthenticateWithoutSessionKey = new JSONSerializer()
				.include(
						"id",
						"login",
						"email",
						"nome",
						"pessoaCadastro",
						"portalSema",
						"perfis.nome",
						"perfis.codigo",
						"perfis.permissoes.codigo",
						"setores.id",
						"setores.nome",
						"setores.sigla",
						"perfilSelecionado.nome",
						"perfilSelecionado.codigo",
						"perfilSelecionado.permissoes.nome",
						"perfilSelecionado.permissoes.codigo",
						"setorSelecionado.id",
						"setorSelecionado.nome",
						"setorSelecionado.sigla"
				)
				.exclude("*")
				.prettyPrint(prettyPrint);

	}

}
