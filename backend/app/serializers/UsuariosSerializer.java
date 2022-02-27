package serializers;

import java.util.Date;

import play.Play;
import play.Play.Mode;
import flexjson.JSONSerializer;

public class UsuariosSerializer {

	public static JSONSerializer findUsuariosByLogin;
	public static JSONSerializer findUsuariosByToken;
	public static JSONSerializer getModulosPermitidos;
	public static JSONSerializer findUsuarioBySessionKey;
	public static JSONSerializer findUsuariosByPerfil;
	public static JSONSerializer getSetores;
	public static JSONSerializer findUsuariosByModulo;
	public static JSONSerializer findUsuariosPessoaSema;

	static {

		boolean prettyPrint = Play.mode == Mode.DEV;

		findUsuariosByLogin = new JSONSerializer()
				.include(
						"id",
						"ativo",
						"email",
						"pessoaCadastro",
						"perfis.id",
						"perfis.nome",
						"perfis.codigo",
						"perfis.permissoes.id",
						"perfis.permissoes.nome",
						"perfis.permissoes.codigo",
						"setores.id",
						"setores.nome",
						"setores.sigla",
						"agendaDesativacao.id",
						"agendaDesativacao.motivo.id",
						"agendaDesativacao.motivo.descricao",
						"agendaDesativacao.dataInicio",
						"agendaDesativacao.dataFim"
				)
				.exclude("*")
				.transform(DateSerializer.getTransformer(), Date.class)
				.prettyPrint(prettyPrint);

		findUsuariosByToken = new JSONSerializer()
				.include(
						"id",
						"temSenha"
				)
				.exclude("*")
				.prettyPrint(prettyPrint);

		getModulosPermitidos = new JSONSerializer()
				.include(
						"modulosPermitidos.id",
						"modulosPermitidos.nome",
						"modulosPermitidos.logotipo",
						"modulosPermitidos.url",
						"modulosPermitidos.descricao",
						"modulosPermitidos.sigla",
						"modulosPermitidos.ativo"
				)
				.exclude("*")
				.prettyPrint(prettyPrint);

		findUsuarioBySessionKey = new JSONSerializer()
				.include(
						"id",
						"email",
						"pessoaCadastro",
						"pessoa.id",
						"perfis.nome",
						"agendaDesativacao.id",
						"agendaDesativacao.motivo.id",
						"agendaDesativacao.motivo.descricao",
						"agendaDesativacao.dataInicio",
						"agendaDesativacao.dataFim"
				)
				.exclude("*")
				.transform(DateSerializer.getTransformer(), Date.class)
				.prettyPrint(prettyPrint);

		findUsuariosByPerfil = new JSONSerializer()
				.include(
						"id",
						"nome",
						"login",
						"ativo",
						"email",
						"pessoaCadastro",
						"perfis.id",
						"perfis.nome",
						"perfis.codigo",
						"setores.id",
						"setores.nome",
						"setores.sigla",
						"pessoa.nome"
				)
				.exclude("*")
				.transform(DateSerializer.getTransformer(), Date.class)
				.prettyPrint(prettyPrint);

		getSetores = new JSONSerializer()
				.include(
						"id",
						"nome",
						"sigla"
				)
				.exclude("*")
				.prettyPrint(prettyPrint);

		findUsuariosByModulo = new JSONSerializer()
				.include(
						"id",
						"nome",
						"login",
						"ativo",
						"email",
						"pessoaCadastro",
						"perfis.id",
						"perfis.nome",
						"perfis.codigo",
						"setores.id",
						"setores.nome",
						"setores.sigla",
						"pessoa.nome"
				)
				.exclude("*")
				.transform(DateSerializer.getTransformer(), Date.class)
				.prettyPrint(prettyPrint);

		findUsuariosPessoaSema = new JSONSerializer()
				.include(
						"id",
						"nomeUsuario",
						"login",
						"email",
						"cpf",
						"sexo",
						"dataNascimento",
						"dataCadastro",
						"dataAtualizacao",
						"celular"
				)
				.exclude("*")
				.transform(DateSerializer.getTransformer(), Date.class)
				.prettyPrint(prettyPrint);

	}

}
