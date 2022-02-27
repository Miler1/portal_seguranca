package controllers;

import exceptions.OAuthRequestException;
import exceptions.PortalSegurancaException;
import exceptions.ValidationException;
import models.*;
import models.permissoes.AcaoSistema;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Http;
import play.mvc.Http.Request;
import portalSema.CheckAuthenticateVO;
import portalSema.CheckCidadaoVO;
import portalSema.PortalSemaResponseTokenVO;
import secure.AuthenticationServiceFactory;
import secure.CheckingServiceFactory;
import secure.IAuthenticatedUser;
import secure.SHA512Generator;
import secure.authorization.Authorization;
import secure.authorization.Permissible;
import secure.oauth.OAuthAuthorizationService;
import secure.oauth.OAuthAuthorizationServiceFactory;
import serializers.SecureSerializer;
import serializers.TokenSerializer;
import services.ExternalPessoaService;
import services.UsuarioServiceFactory;
import utils.Config;

import java.util.HashSet;
import java.util.Set;

public class Secure extends BaseController {

	private static AuthenticationServiceFactory authenticationServiceFactory = new AuthenticationServiceFactory();
	private static OAuthAuthorizationServiceFactory authorizationServiceFactory = new OAuthAuthorizationServiceFactory();
	private static CheckingServiceFactory checkingServiceFactory = new CheckingServiceFactory();
	private static UsuarioServiceFactory usuarioServiceFactory = new UsuarioServiceFactory();

	@Before(unless = {"authenticate", "login",
			"Usuarios.createPassword",
			"Usuarios.isUserAuthenticated",
			"Usuarios.validateToken",
			"Usuarios.mailResetPassword",
			"Usuarios.resetPassword",
			"getAccessToken",
			"isAccessTokenValid"
	})
	public static void isAuthenticated() {

		if(isPublicResource()){

			return;

		} else if(isExternalResource()) {

			OAuthAuthorizationService.validateTokenAndIP(request);

		} else {

//			TODO: Comentando por enquanto, até implemetar as outras coisas

//			Usuario usuarioLogado = (Usuario) getAuthenticatedUser();
//
//			if(usuarioLogado == null) {
//
//				Logger.debug("Permissão negada! Sem usuario logado.");
//
//				clearUserSession();
//
//				unauthorized();
//
//			}
//
//			Cache.set(session.getId(), usuarioLogado, Play.configuration.getProperty("application.session.maxAge"));

		}

	}

	public static void authenticate() {

		if(isExternalResource()) {

			OAuthAuthorizationService.validateTokenAndIP(request);

		}

		String cpf = request.params.get("username");

		cpf = cpf.replaceAll("[./-]", "");

		CheckCidadaoVO cidadaoVO =  checkingServiceFactory.getInstance().checkCidadao(cpf);

		if((cidadaoVO.cidadao != null && cidadaoVO.cidadao) && (cidadaoVO.usuario != null && cidadaoVO.usuario)){

			PortalSemaResponseTokenVO portalSemaResponseTokenVO = authenticationServiceFactory.getInstance().authenticatePortalSema(request, session);

			if(portalSemaResponseTokenVO != null){

				if(portalSemaResponseTokenVO.access_token == null) {

					throw new ValidationException().userMessage("authenticate.credenciaisInvalidas");

				}

				Usuario usuario = usuarioServiceFactory.getInstance().BuscaUsuarioEU(cpf);

				usuario = verificaUsuarioCriado(usuario, portalSemaResponseTokenVO.access_token);

				usuario.portalSema = true;

				String sessionKey = session.getId();

				Logger.debug("ID da Sessão: %s", new Object[]{sessionKey});

				Cache.set("SESSION_KEY", sessionKey);

				Cache.set(sessionKey + "_TOKEN", portalSemaResponseTokenVO, Play.configuration.getProperty("application.session.maxAge"));

				usuario.readjustFieldsToResponse(sessionKey);

				usuario.nome = usuario.nomeUsuario != null ? usuario.nomeUsuario : usuario.razaoSocialUsuario;

				Cache.set(sessionKey, usuario, Play.configuration.getProperty("application.session.maxAge"));

				verifyUsuarioPerfis(usuario);

				renderJSON(usuario, isExternalResource() ? SecureSerializer.externalAuthenticate : SecureSerializer.authenticate);

			} else {

				Usuario usuario = new Usuario();

				usuario.portalSema = false;

				renderJSON(usuario, isExternalResource() ? SecureSerializer.externalAuthenticate : SecureSerializer.authenticate);

			}

		} else {

			Usuario usuario = new Usuario();

			usuario.portalSema = false;

			renderJSON(usuario, isExternalResource() ? SecureSerializer.externalAuthenticate : SecureSerializer.authenticate);

		}

//		Usuario usuario = (Usuario) authenticationServiceFactory.getInstance().authenticate(request, session);
//
//		validateUsuarioSessao(usuario);
//
//		String sessionKey = session.getId();
//
//		Logger.debug("ID da Sessão: %s", new Object[]{sessionKey});
//
//		Cache.set(sessionKey, usuario, Play.configuration.getProperty("application.session.maxAge"));
//
//		usuario.readjustFieldsToResponse(sessionKey);
//
//		verifyUsuarioPerfis(usuario);
//
//		renderJSON(usuario, isExternalResource() ? SecureSerializer.externalAuthenticate : SecureSerializer.authenticate);

	}

	private static Usuario verificaUsuarioCriado(Usuario usuario, String acessToken) {

		if(usuario == null) {

			usuario = usuarioServiceFactory.getInstance().criarUsuarioByPessoaSema(acessToken);

			return usuario;

		} else {

			// implementar edição de dados caso tenha editado no portal da sema
			if (usuario.pessoaCadastro) {

				usuario = usuarioServiceFactory.getInstance().atualizarUsuarioByPessoaSema(acessToken, usuario);

			} else if (!usuario.pessoaCadastro) {

				Perfil perfilProdap = Perfil.getPerfilByCodigo(Config.CODIGO_PERFIL_PRODAP);

				usuario.perfis = new HashSet<>();

				usuario.perfis.add(perfilProdap);

				usuario.perfilSelecionado = perfilProdap;

			}

			return usuario;

		}

	}

	private static void verifyUsuarioPerfis(Usuario usuario) {

		if(!isOwnResource()) {

			usuario.perfis = Perfil.findPerfisByModuloRequest(usuario);

		}

	}

	private static void validateUsuarioSessao(Usuario usuario) {

		if(usuario == null || usuario.getId() == null) {

			throw new ValidationException().userMessage("authenticate.credenciaisInvalidas");

		}

		if(!usuario.ativo) {

			throw new ValidationException().userMessage("authenticate.inativo");

		}

		if(isExternalResource() && !hasModuloAccess(usuario)) {

			throw new ValidationException().userMessage("authenticate.semAcesso.modulo");

		}

	}

	private static Boolean hasModuloAccess(Usuario usuario) {

		String accessToken = OAuthAuthorizationService.getToken(request);
		OAuthClient oAuthClient = OAuthClient.find("accessToken", SHA512Generator.generateValue(accessToken)).first();
		Set<Modulo> moduloSet = Modulo.findModulosByPermissoes(usuario.findAllPermissoes());

		for(Modulo modulo : moduloSet) {

			if(modulo.id.equals(oAuthClient.modulo.id)) {

				return true;

			}

		}

		return false;

	}

	public static IAuthenticatedUser getAuthenticatedUser() {

		Logger.debug("ID da Sessão: %s", new Object[]{session.getId()});

		return Cache.get(session.getId(), Usuario.class);

	}

	public static void login() {

		redirect(Play.configuration.getProperty("authentication.url.login"));

	}

	public static void logout() {

		clearUserSession();

		String sessionKey = Cache.get("SESSION_KEY", String.class);

		PortalSemaResponseTokenVO portalSemaResponseTokenVO = Cache.get(sessionKey + "_TOKEN", PortalSemaResponseTokenVO.class);

		if(portalSemaResponseTokenVO != null) {
			authenticationServiceFactory.getInstance().logoutPortalSema(portalSemaResponseTokenVO.access_token);
		}

		redirect(Play.configuration.getProperty("authentication.url.login"));

	}

	private static void clearUserSession() {

		Cache.delete(session.getId());
		session.clear();

	}

	protected static void executeAuthorization(Integer... idsAcaoSistema) {

		checkAuthenticate();

		executeAuthorization(null, idsAcaoSistema);

	}

	protected static void executeAuthorization(Permissible permissible, Integer... idsAcaoSistema) {

		for(Integer idAcaoSistema : idsAcaoSistema) {

			AcaoSistema acao = AcaoSistema.findById(idAcaoSistema);

			if (Authorization.getInstance().checkPermission(acao, Secure.getAuthenticatedUser(), permissible)) {

				return;

			}

		}

		forbidden();

	}

	protected static void checkAuthenticate() {

		String sessionKey = Cache.get("SESSION_KEY", String.class);

		PortalSemaResponseTokenVO portalSemaResponseTokenVO = Cache.get(sessionKey + "_TOKEN", PortalSemaResponseTokenVO.class);

		CheckAuthenticateVO checkAuthenticateVO = checkingServiceFactory.getInstance().checkAuthenticate(portalSemaResponseTokenVO.access_token);

		if(checkAuthenticateVO != null && !checkAuthenticateVO.logado) {

			logout();

		}

	}

	public static void isAccessTokenValid(String token) {

		OAuthAuthorizationService.validateToken(token);

	}

	public static void getAccessToken() {

		String grantType = request.params.get("grant_type");

		validateGrantType(grantType);

		Token token = authorizationServiceFactory.getInstance(grantType).getAccessToken(request);

		if(Boolean.getBoolean(Play.configuration.getProperty("oAuth.token.refreshToken"))) {

			renderJSON(token);

		}

		renderJSON(token, TokenSerializer.withoutRefreshToken);

	}

	private static void validateGrantType(String grantType) {

		if(!Play.configuration.containsKey("oAuth.grantType." + grantType)
				|| !Boolean.valueOf(Play.configuration.getProperty("oAuth.grantType." + grantType))) {

			throw new OAuthRequestException().userMessage("oAuth.grantType.invalido");

		}

	}

	public static void isLoggedWithCookie(Request requestAuth) {

		Http.Cookie cookie = requestAuth.cookies.get(Play.configuration.getProperty("portalSeguranca.session.cookie"));

		if(cookie == null) {

			unauthorized();

		}

		String cookieSessionVal = cookie.value;
		cookieSessionVal = cookieSessionVal.substring(cookieSessionVal.indexOf("-") + 1);
		String[] session = cookieSessionVal.split("&");
		String sessionKey = session[1].substring(cookieSessionVal.indexOf("=") + 1);
		Usuario usuario = Cache.get(sessionKey, Usuario.class);

		if (usuario == null || usuario.getId() == null || (usuario.perfilSelecionado == null && usuario.pessoaCadastro)) {

			unauthorized();

		}

		usuario.sessionKeyEntradaUnica = sessionKey;

		Logger.debug("ID da Sessão: %s", new Object[]{sessionKey});

		renderJSON(usuario, SecureSerializer.externalAuthenticate);

	}

	public static void isLogged(String sessionKey) {

		Usuario usuario = Cache.get(sessionKey, Usuario.class);

		Logger.debug("ID da Sessão verificada: %s", new Object[]{sessionKey});

		if(usuario == null || usuario.getId() == null) {

			renderJSON(false);

		}

		renderJSON(true);

	}

	public static void redirectToModulosWithSessionKey(Integer idPerfil, Integer idSetor, Integer idModulo) {

		Usuario usuario = (Usuario) getAuthenticatedUser();

		usuario.perfilSelecionado = Perfil.findByIdWithPermissoes(idPerfil);

		if (idSetor != null) {

			usuario.setorSelecionado = Setor.findById(idSetor);
		}

		String sessionKeyEntradaUnica = session.getId();

		Cache.replace(sessionKeyEntradaUnica, usuario);

		Modulo modulo = Modulo.findById(idModulo);

		if(idModulo == Config.ID_PORTAL) {

			Modulo cadastroUnificado = Modulo.findById(Config.ID_CADASTRO);
			modulo.url = cadastroUnificado.url;

		}

		frontEndRedirect(modulo.url + "/" + sessionKeyEntradaUnica);

	}

	public static Boolean externalVerifyAuthenticatedUser(String login, String sessionKey ) {

		Logger.debug("ID da Sessão: %s", new Object[]{session.getId()});

		Usuario usuario = Cache.get(sessionKey, Usuario.class);

		if(usuario.login.equals(login)) {

			return true;

		}

		return false;

	}

	public static void findIdModuloByToken(String token) {

		String accessToken = OAuthAuthorizationService.getToken(token);
		OAuthClient oAuthClient = OAuthClient.find("accessToken", SHA512Generator.generateValue(accessToken)).first();

		if(oAuthClient == null || oAuthClient.modulo == null) {

			throw new PortalSegurancaException().userMessage("oAuth.client.naoEncontrado");

		}

		renderJSON(oAuthClient.modulo.id);

	}

	public static void isPasswordAuthenticated(String login, String password) {

		PortalSemaResponseTokenVO tokenVO =  checkingServiceFactory.getInstance().checkUserPasswordValidation(login,password);

		if(tokenVO.access_token == null) {
			throw new ValidationException().userMessage("authenticate.senhaInvalida");
		}

	}

	/**
	 * Retorna se o módulo + serviço + endereco estão liberados para acesso
	 * @param userSession - sessão associada ao usuário
	 * @param service - serviço a ser verificado
	 * @param address - endereço a ser verificado
	 */
	public static void isAllowedAccess(String userSession, String service, String address) {

		String accessToken;
		OAuthClient oAuthClient;

		accessToken = OAuthAuthorizationService.getToken(request);
		oAuthClient = OAuthClient.find("accessToken", SHA512Generator.generateValue(accessToken)).first();

		if(oAuthClient == null || oAuthClient.modulo == null) {

			throw new PortalSegurancaException().userMessage("oAuth.client.naoEncontrado");

		}

		Modulo ownerModule = oAuthClient.modulo;

		IAuthenticatedUser userInCache = Cache.get(userSession, Usuario.class);

		boolean allowed = false;

		Logger.info("isAllowedAccess() -> ownerModule: [ %s - %s ] - service: [ %s ] - address: [ %s ]", ownerModule.id.toString(), ownerModule.sigla, service, address);

		// Se o token (userSession) for a chave da sessão do usuário logado
		if(userInCache != null) {

			Logger.info("isAllowedAccess() -> Usuário encontrado com a sessão enviada.");

			Usuario userLogged = Usuario.findById(((Usuario) userInCache).id);

			if (userLogged == null) {

				throw new PortalSegurancaException().userMessage("oAuth.client.naoEncontrado");
			}

			userLogged.modulosPermitidos = Modulo.findModulosByPermissoes(userLogged.findAllPermissoes());

			allowed = AcessoModulo.acessoLiberadoParaOModulo(ownerModule, userLogged.modulosPermitidos, service, address);

		}
		// Caso o token (userSession) for a chave de um módulo, caso o solicitante NÃO for um usuário no sistema
		else {

			accessToken = OAuthAuthorizationService.getToken(userSession);
			oAuthClient = OAuthClient.find("accessToken", SHA512Generator.generateValue(accessToken)).first();

			if(oAuthClient != null && oAuthClient.modulo != null) {

				Logger.info("Módulo encontrado com o token: %s", userSession);

				Set<Modulo> modules = new HashSet<Modulo>();
				modules.add(oAuthClient.modulo);

				allowed = AcessoModulo.acessoLiberadoParaOModulo(ownerModule, modules, service, address);
			}
			else {

				Logger.error("Módulo não encontrado com o token: %s", userSession);
				allowed = false;
			}
		}

		Logger.info("isAllowedAccess() -> allowed: [ %s ]", allowed);

		renderJSON(allowed);
	}

}
