package services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import models.Perfil;
import models.Setor;
import models.Usuario;
import play.libs.WS;

import java.util.*;

public class ExternalPessoaService extends BaseExternalService {

	protected static String HAS_PESSOA_WITH_LOGIN_PATH = "usuario/pessoaEstaBloqueado/";
	protected static String FIND_PESSOA_BY_LOGIN = "usuario/pessoa/";
	protected static String PESSOA_PORTAL_SEMA_UPDATE = "usuario/pessoa/updatePessoaSema";
	protected static String HAS_PESSOA_FISICA_CADASTRADA = "pessoaFisica/vincular/";

	public static Boolean isPessoaBloqued(String login) {

		WS.WSRequest wsRequest = WS
				.url(cadastroUnificadoUrl + "/public/" + HAS_PESSOA_WITH_LOGIN_PATH + login);

		WS.HttpResponse httpResponse = wsRequest.get();

		verifyResponse(httpResponse);

		return Boolean.valueOf(httpResponse.getString());

	}

	public static Usuario findPessoaByLoginForHeader(String login) {

		WS.WSRequest wsRequest = WS
				.url(cadastroUnificadoUrl + "/public/" + FIND_PESSOA_BY_LOGIN + login);

		WS.HttpResponse httpResponse = wsRequest.get();

		verifyResponse(httpResponse);

		Usuario usuario = new Usuario();

		if (!httpResponse.getJson().isJsonNull() && !httpResponse.getJson().getAsJsonObject().get("pessoa").isJsonNull()) {
			JsonObject pessoa = httpResponse.getJson().getAsJsonObject().get("pessoa").getAsJsonObject();
			usuario.pessoaId = Integer.parseInt(pessoa.get("id").getAsString());

			usuario.getNomeByObject(pessoa);

			usuario.getRazaoSocialByObject(pessoa);
		}

		return usuario;

	}

	public static Usuario updatePessoaPortalSema(Usuario usuarioPortalSema) {

		WS.WSRequest wsRequest = WS
				.url(cadastroUnificadoUrl + "/public/" + PESSOA_PORTAL_SEMA_UPDATE)
				.body(gson.toJson(usuarioPortalSema));

		wsRequest.headers.put("Content-Type", "application/json");

		WS.HttpResponse httpResponse = wsRequest.put();

		verifyResponse(httpResponse);

		if (!httpResponse.getJson().isJsonNull() && !httpResponse.getJson().getAsJsonObject().get("pessoa").isJsonNull()) {
			JsonObject pessoa = httpResponse.getJson().getAsJsonObject().get("pessoa").getAsJsonObject();
			JsonArray perfis = httpResponse.getJson().getAsJsonObject().get("perfis").getAsJsonArray();
			JsonArray setores = httpResponse.getJson().getAsJsonObject().get("setores").getAsJsonArray();
			usuarioPortalSema.pessoaId = Integer.parseInt(pessoa.get("id").getAsString());

			Set<Perfil> perfisSet = new Gson().fromJson(perfis, new TypeToken<HashSet<Perfil>>(){}.getType());
			Set<Setor> setoresSet = new Gson().fromJson(setores, new TypeToken<HashSet<Setor>>(){}.getType());

			usuarioPortalSema.perfis = perfisSet;
			usuarioPortalSema.setores = setoresSet;

		}

		return usuarioPortalSema;

	}

	public static Usuario checkPessoaFisicaCadastrada(String login) {

		WS.WSRequest wsRequest = WS
				.url(cadastroUnificadoUrl + "/public/" + HAS_PESSOA_FISICA_CADASTRADA + login);

		WS.HttpResponse httpResponse = wsRequest.get();

		verifyResponse(httpResponse);

		Usuario usuario = new Usuario();

		if (!httpResponse.getJson().isJsonNull() && !httpResponse.getJson().getAsJsonObject().get("id").isJsonNull()) {
			usuario.pessoaId = httpResponse.getJson().getAsJsonObject().get("id").getAsInt();
		}

		return usuario;

	}

}
