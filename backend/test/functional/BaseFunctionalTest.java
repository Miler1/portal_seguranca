package functional;

import builders.ModuloBuilder;
import builders.PerfilBuilder;
import builders.UsuarioBuilder;
import com.google.gson.*;
import deserializers.DateDeserializer;
import flexjson.JSONSerializer;
import models.*;
import models.permissoes.AcaoSistema;
import org.junit.AfterClass;
import play.cache.Cache;
import play.db.jpa.JPA;
import play.mvc.Http;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.test.FunctionalTest;
import secure.SHA512Generator;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;

public abstract class BaseFunctionalTest extends FunctionalTest {

	protected static Gson gson;
	protected static Modulo modulo;

	static {

		GsonBuilder builder = new GsonBuilder();

		// Register here the adapters
		builder.registerTypeAdapter(Date.class, new DateDeserializer());
		builder.setDateFormat("dd/MM/yyyy HH:mm:ss");

		gson = builder.create();

	}

	protected static Http.Response getAccessToken() {

		return POST(getAccessTokenRequest(), "/external/token", "application/x-www-form-urlencoded", "");

	}

	protected static Http.Request getAccessTokenRequest() {

		modulo = new ModuloBuilder().save();

		@SuppressWarnings("deprecation")
		Http.Request requestPadrao = new Http.Request();
		Http.Header header = new Http.Header("authorization", "Basic " + new String(Base64.getEncoder().encode((modulo.oAuthClient.clientId + ":" + modulo.oAuthClient.clientSecret).getBytes())));

		modulo.oAuthClient.clientSecret = SHA512Generator.generateValue(modulo.oAuthClient.clientSecret);
		modulo.oAuthClient.clientId = SHA512Generator.generateValue(modulo.oAuthClient.clientId);
		modulo.oAuthClient.save();

		commitTransaction();

		requestPadrao.remoteAddress = "127.0.0.1";
		requestPadrao.params.put("grant_type", "client_credentials");
		requestPadrao.headers.put("authorization", header);

		return requestPadrao;

	}

	protected static Http.Request getDefaultRequestWithToken() {

		Response response = getAccessToken();

		modulo.refresh();

		@SuppressWarnings("deprecation")
		Http.Request requestPadrao = new Http.Request();
		Http.Header header = new Http.Header("authorization", "Bearer " + new String(responseToObject(response, Token.class).access_token.getBytes()));
		requestPadrao.remoteAddress = "127.0.0.1";
		requestPadrao.headers.put("authorization", header);

		return requestPadrao;

	}

	public static Usuario buildUsuarioLogin(Integer idAcaoSistema) {

		AcaoSistema acaoSistema = AcaoSistema.findById(idAcaoSistema);
		Permissao permissao = Permissao.find("codigo = ?", acaoSistema.permissao).first();
		Perfil perfil = new PerfilBuilder().comPermissao(permissao).save();

		return new UsuarioBuilder().comPerfil(perfil).comLoginRandomico().comSenha(UsuarioBuilder.SENHA_PADRAO).save();

	}

	protected static void beginTransaction() {

		if (!JPA.isInsideTransaction()) {

			JPA.em().getTransaction().begin();

		}

	}

	@AfterClass
	public static void close() {

		if (JPA.isInsideTransaction()) {

			JPA.em().getTransaction().commit();

		}

	}

	public static void commitTransaction() {

		if (JPA.isInsideTransaction()) {

			JPA.em().getTransaction().commit();
			JPA.em().getTransaction().begin();

		}

	}

	protected static <T> void assertIsNotEmptyArrayJson(Response response, Class clazz) {

		assertIsNotEmptyArrayJson("O array não deveria estar vazio.", response, clazz);

	}

	protected static <T> void assertIsNotEmptyJson(String message, Response response, Class clazz) {

		assertIsOk(response);

		String content = getContent(response);

		assertNotNull(message, gson.fromJson(content, clazz));

	}

	protected static <T> void assertIsNotEmptyArrayJson(String message, Response response, Class clazz) {

		assertIsOk(response);
		assertContentType("application/json", response);

		String content = getContent(response);

		assertTrue(message, Array.getLength(getJson(content, clazz)) > 0);

	}

	protected void assertNotEmpty(Collection collection) {

		assertNotNull(collection);
		assertFalse(collection.isEmpty());

	}

	protected static Object getJson(String content, Class clazz) {
		return gson.fromJson(content, Array.newInstance(clazz, 1).getClass());
	}

	public static <T> T  responseToObject(Response response, Class<T> clazz) {

		assertIsOk(response);
		assertContentType("application/json", response);
		String content = getContent(response);

		return gson.fromJson(content, clazz);
	}

	protected static <T> List<T> getResponseAsListOf(Response response, Class<T> clazz) {

		assertIsOk(response);
		assertContentType("application/json", response);

		String content = getContent(response);
		Object contentArray = gson.fromJson(content, Array.newInstance(clazz, 1).getClass());

		int length = Array.getLength(contentArray);
		List<T> typedContentList = new ArrayList<T>();

		for (int i = 0; i < length; i++) {

			typedContentList.add((T) Array.get(contentArray, i));

		}

		return typedContentList;

	}

	protected static <T> Pagination<T> responseToPaginationOf(Response response, Class<T> clazz) {

		assertIsOk(response);
		assertContentType("application/json", response);

		String content = getContent(response);

		Type type = ParameterizedTypeImpl.make(Pagination.class, new Type[] { clazz }, null);

		return gson.fromJson(content, type);

	}

	protected static <T> Pagination<T> getResponseAsPaginationOf(Response response, Class<T> clazz) {

		assertIsOk(response);
		assertContentType("application/json", response);

		String contentJson = getContent(response);

		JsonElement jsonElem = new JsonParser().parse(contentJson);
		JsonObject json = (JsonObject) jsonElem;

		Pagination<T> pagination = gson.fromJson(json, Pagination.class);
		pagination.setPageItems(getTypedListFromJsonElement(json.get("pageItems"), clazz));

		return pagination;

	}

	private static <T> List<T> getTypedListFromJsonElement(JsonElement items, Class<T> clazz) {

		Object contentArray = gson.fromJson(items, Array.newInstance(clazz, 1).getClass());

		if (contentArray == null)
			return null;

		int length = Array.getLength(contentArray);
		List<T> typedContentList = new ArrayList<T>();

		for (int i = 0; i < length; i++) {

			typedContentList.add((T) Array.get(contentArray, i));

		}

		return typedContentList;

	}


	public static Response login(String login, String senha) {

		Cache.clear();

		Request request = getDefaultRequestWithToken();
		request.params.put("username",login);
		request.params.put("password",senha);

		return POST(request,"/login", "application/json", "");

	}

	public static void logout() {

		GET("/logout");

	}

	public static Response POSTJson(String url, String json) {

		return POST(url, "application/json", json);

	}

	public static Response PUTJson(String url, String json) {

		return PUT(url, "application/json", json);

	}

	public static Response POSTJson(String url, Object model, JSONSerializer js) {

		String json = js.serialize(model);

		return POST(url, "application/json", json);

	}

	public static <T> Response POSTJson(String url, T data) {

		String jsonData = gson.toJson(data);

		return POST(url, "application/json", jsonData);

	}

	public static <T> Response POSTJson(String url, T data, Class<T> clazz) {

		String jsonData = gson.toJson(data, clazz);

		return POST(url, "application/json", jsonData);

	}

	public static <T> Response PUTJson(String url, T data) {

		String jsonData = gson.toJson(data);

		return PUT(url, "application/json", jsonData);

	}

	public static <T> T fromJson(Response response, Class<T> clazz) {

		return fromJson(getContent(response), clazz);

	}

	public static <T> T fromJson(String json, Class<T> clazz) {

		Gson gson = new GsonBuilder()
						.registerTypeAdapter(Date.class, new DateDeserializer())
						.create();

		return gson.fromJson(json, clazz);

	}

}
