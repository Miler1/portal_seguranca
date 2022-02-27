package controllers;

import exceptions.ValidationException;
import models.Message;
import models.Modulo;
import models.Perfil;
import models.Usuario;
import models.permissoes.AcaoSistema;
import play.Play;
import play.cache.Cache;
import play.i18n.Messages;
import play.mvc.Http;
import play.mvc.With;
import secure.authorization.Action;
import secure.authorization.Authorization;
import serializers.SecureSerializer;
import serializers.UsuariosSerializer;
import services.ExternalPessoaService;
import utils.StringUtil;

import java.util.Date;
import java.util.List;

@With(Secure.class)
public class Usuarios extends BaseController {

	public static void create(Usuario usuario) {

		if(!isExternalResource()) {

			Secure.executeAuthorization(AcaoSistema.CADASTRAR_USUARIO);

		}

		notFoundIfNull(usuario);

		if(usuario.id != null) {

			Usuario usuarioSave = Usuario.findById(usuario.id);

			usuarioSave.pessoaCadastro = usuario.pessoaCadastro;
			usuarioSave.dataAtualizacao = new Date();

			usuarioSave.save();

		} else {

			usuario.save();

		}


		renderJSON(new Message(Messages.get("usuarios.cadastro.sucesso", usuario)));

	}

	public static void update(Usuario usuario) {

		if(!isExternalResource()) {

			Secure.executeAuthorization(AcaoSistema.EDITAR_USUARIO);

		}

		Usuario usuarioAtualizar = Usuario.findByLogin(usuario.login);

		notFoundIfNull(usuarioAtualizar, Messages.get("usuarios.busca.naoEncontrado"));

		usuarioAtualizar.update(usuario);

		renderJSON(new Message(Messages.get("usuarios.atualizacao.sucesso", usuario.login)));

	}

	public static void isUserAuthenticated(Integer id) {

		Usuario usuarioAutenticado = (Usuario) Secure.getAuthenticatedUser();

		if(usuarioAutenticado == null || usuarioAutenticado.senha == null || usuarioAutenticado.perfilSelecionado == null) {

			renderJSON(false);

		}

		Usuario usuario = Usuario.findById(id);

		if(!usuarioAutenticado.id.equals(usuario.id)) {

			renderJSON(false);

		}

		renderJSON(true);

	}

	public static void getAcoesPermitidas() {

		List<Action> acoes = AcaoSistema.find("").fetch();

		List<Integer> idsAcoesPermitidas = Authorization.getInstance().checkPermittedActions(acoes, Secure.getAuthenticatedUser());

		renderJSON(idsAcoesPermitidas);

	}

	public static void externalFindByLogin(String login) {

		renderJSON(Usuario.findByLogin(login), UsuariosSerializer.findUsuariosByLogin);

	}

//	public static void createPassword(Integer id, String senha) throws Exception {
//
//		notFoundIfNull(id);
//		notFoundIfNull(senha);
//
//		Usuario usuario = Usuario.findById(id);
//
//		notFoundIfNull(usuario);
//
//		usuario.createPassword(senha);
//
//		renderJSON(new Message(Messages.get("usuarios.primeiroAcesso.sucesso")));
//
//	}

	public static void validateToken(String token) throws Exception {

		notFoundIfNull(token);

		Usuario usuario = Usuario.validateToken(token);

		if (usuario == null) {

			throw new ValidationException("usuarios.primeiroAcesso.naoEncontrado");

		}

		renderJSON(usuario, UsuariosSerializer.findUsuariosByToken);

	}

	// public static void mailResetPassword(String login) throws Exception {

	// 	Usuario usuario = Usuario.findByLogin(login);

	// 	if(usuario == null) {

	// 		throw new ValidationException().userMessage(Messages.get("usuarios.busca.naoEncontrado"));

	// 	}

	// 	if(usuario.senha == null || !usuario.ativo) {

	// 		throw new ValidationException().userMessage("usuarios.redefinirAtivoReq");

	// 	}

	// 	Message message = new Message("usuarios.emailRedefinirSenha", StringUtil.coverMail(usuario.email));

	// 	usuario.sendResetPasswordMail();

	// 	if(response.status.equals(Http.StatusCode.ACCEPTED)) {

	// 		message.setText("usuarios.emailRedefinirSenha.envioEmail.erro", StringUtil.coverMail(usuario.email));

	// 	}

	// 	renderJSON(message);

	// }

	// public static void resetPassword(Integer id, String senha) throws Exception {

	// 	notFoundIfNull(id);
	// 	notFoundIfNull(senha);

	// 	Usuario usuario = Usuario.findById(id);

	// 	notFoundIfNull(usuario);

	// 	usuario.resetPassword(senha);

	// 	renderJSON(new Message(Messages.get("usuarios.redefinir.sucesso")));

	// }

	public static void activate(Integer id) {

		if(!isExternalResource()) {

			Secure.executeAuthorization(AcaoSistema.ATIVAR_DESATIVAR_USUARIO);

		}

		notFoundIfNull(id);

		Usuario usuarioAtivar = Usuario.findById(id);

		notFoundIfNull(usuarioAtivar, Messages.get("usuarios.busca.naoEncontrado"));

		usuarioAtivar.activate();

		renderJSON(new Message("usuarios.ativo.sucesso"));

	}

	public static void externalRemoveByLogin(String login) {

		notFoundIfNull(login);

		Usuario usuarioRemover = Usuario.find("login", login).first();

		notFoundIfNull(usuarioRemover, Messages.get("usuarios.busca.naoEncontrado"));

		usuarioRemover.remove();

		renderJSON(new Message("usuarios.removido.sucesso"));

	}

	public static void hasUsuarioWithLogin(String login) {

		Usuario usuario = Usuario.findByLogin(login);

		if(usuario == null) {

			renderJSON(false);

		}

		renderJSON(true);

	}

	public static void isPessoaBloqued(String login) {

		if(ExternalPessoaService.isPessoaBloqued(login)) {

			renderJSON(true);

		}

		renderJSON(false);

	}

	public static void getModulosPermitidos(String login) {

		Usuario usuario = Usuario.find("login", login).first();

		if(usuario != null && usuario.perfis != null) {

			usuario.modulosPermitidos = Modulo.findModulosByPermissoes(usuario.findAllPermissoes());

		}

		renderJSON(usuario, UsuariosSerializer.getModulosPermitidos);

	}

	public static void MCUFindBySessionKey(String sessionKeyEntradaUnica) {

		Usuario usuario = Cache.get(sessionKeyEntradaUnica, Usuario.class);

		if(usuario != null) {

			Usuario usuarioPessoa = ExternalPessoaService.findPessoaByLoginForHeader(usuario.login);

			usuario.nome = usuarioPessoa.nomeUsuario != null ? usuarioPessoa.nomeUsuario : usuarioPessoa.razaoSocialUsuario;

			renderJSON(usuario, SecureSerializer.authenticate);

		}

	}

	public static void externalFindBySessionKey(String sessionKeyEntradaUnica) {

		Usuario usuario = Cache.get(sessionKeyEntradaUnica, Usuario.class);

		if(usuario != null) {

			Usuario usuarioPessoa = ExternalPessoaService.findPessoaByLoginForHeader(usuario.login);

			usuario.nome = usuarioPessoa.nomeUsuario != null ? usuarioPessoa.nomeUsuario : usuarioPessoa.razaoSocialUsuario;

			if(!isOwnResource()) {

				usuario.perfis = Perfil.findPerfisByModuloRequest(Usuario.findById(usuario.id));

			}

			renderJSON(usuario, SecureSerializer.externalAuthenticateWithoutSessionKey);

		}

	}

	public static void findPessoaByLogin(String login) {

		Usuario usuario = ExternalPessoaService.findPessoaByLoginForHeader(login);

		renderJSON(usuario);

	}

//	public static void updatePassword(Integer id, String senhaAtual, String senhaNova) throws Exception {
//
//		notFoundIfNull(id);
//		notFoundIfNull(senhaAtual);
//		notFoundIfNull(senhaNova);
//
//		Usuario usuario = Usuario.findById(id);
//
//		notFoundIfNull(usuario);
//
//		usuario.updatePassword(senhaAtual, senhaNova);
//
//		renderJSON(new Message(Messages.get("usuarios.senha.alterada.sucesso")));
//
//	}

	public static void verificarCpf(String cpf){

		renderJSON(Usuario.verificarCpf(cpf));
	}

	public static void getSetoresUsuario(String login) {

		Usuario usuario = Usuario.find("login", login).first();

		renderJSON(usuario.setores, UsuariosSerializer.getSetores);

	}

	public static void getUsuariosByModulo(String siglaModulo) {

		renderJSON(Usuario.getUsuariosByModulo(siglaModulo), UsuariosSerializer.findUsuariosByModulo);

	}

	//verifica se o usuario existe no sistema e se os dados complementares foram preenchidos
	public static void verificarCadastro(String login){

		renderJSON(Usuario.verificarCadastro(login));

	}

	//verifica se a pessoa física existe no sistema caso exista somente edita os dados complementares
	public static void verificarPessoa(String login){

		renderJSON(Usuario.verificarPessoa(login));

	}

}
