package portalSema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import deserializers.StringDeserializer;
import play.Logger;
import play.Play;
import play.libs.WS;

import exceptions.WebServiceException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static utils.Config.API_PORTAL_SEMA_URL;

public class PortalSemaWS {

    /**
     * Requisição POST - (Checar cpf do cidadão para verificar situação do cadastro no portal da sema).
     */
    public static final String CHECAR_CIDADAO_CPF = API_PORTAL_SEMA_URL + "/checar_cidadao_cpf/";

    /**
     * Requisição POST - (Realiza a autenticação do usuário no portal da sema).
     */
    public static final String LOGIN = API_PORTAL_SEMA_URL + "/login/";

    /**
     * Requisição GET - (Obtem dados do Perfil do usuário autenticado).
     */
    public static final String PROFILE = API_PORTAL_SEMA_URL + "/admin/profile/";

    /**
     * Requisição POST - (Checa se o usuário ainda está com a autenticação ativa).
     */
    public static final String CHECAR_AUTENTICACAO = API_PORTAL_SEMA_URL + "/checar/";

    /**
     * Requisição GET - (Realiza o logout do usuário no portal da sema).
     */
    public static final String LOGOUT = API_PORTAL_SEMA_URL + "/logout_api/";

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";

    public static final String API_PORTAL_API_KEY = Play.configuration.getProperty("api.portalSema.ApiKey");
    public static final String API_PORTAL_SEMA_CLIENT_ID = Play.configuration.getProperty("api.portalSema.Client.id");

    private GsonBuilder gsonBuilder = new GsonBuilder();

    public PortalSemaWS() {
        //nothing
    }

    private WS.HttpResponse requestPost(Map<String, String> params, WS.WSRequest request) {
        request.setHeader(CONTENT_TYPE,APPLICATION_JSON);
        request.setHeader("Accept","application/json");

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(String.class, new StringDeserializer())
                .create();

        if(!params.isEmpty()) {
            String jsonData = gson.toJson(params);
            Logger.info("JSON: " + jsonData);
            request.body(jsonData);
        }

        WS.HttpResponse response = request.post();

        if (!response.success())
            throw new WebServiceException(response);

        return response;
    }

    public CheckCidadaoVO checarCidadaoCpf(String cpf) {

        Map<String, String> params = new HashMap<>();
        params.put("cpf", cpf);

        WS.WSRequest request = WS.url(CHECAR_CIDADAO_CPF);
        request.setHeader("Authorization","Api-Key " + API_PORTAL_API_KEY);

        WS.HttpResponse response = requestPost(params, request);

        Type type = new TypeToken<CheckCidadaoVO>(){}.getType();

        return gsonBuilder.create().fromJson(response.getJson(), type);

    }

    public PortalSemaResponseTokenVO loginAPI(String username, String password) {

        Map<String, String> paramsAuth = new HashMap<>();
        paramsAuth.put("client_id", API_PORTAL_SEMA_CLIENT_ID);
        paramsAuth.put("username", username);
        paramsAuth.put("password", password);

        WS.WSRequest request = WS.url(LOGIN);

        WS.HttpResponse response = requestPost(paramsAuth, request);

        Type type = new TypeToken<PortalSemaResponseTokenVO>(){}.getType();

        return gsonBuilder.create().fromJson(response.getJson(), type);

    }

    public PessoaPortalSemaVO recuperaDadosUsuario(String accessToken) {

        WS.WSRequest request = WS.url(PROFILE);
        request.setHeader("Authorization","Bearer " + accessToken);
        request.setHeader(CONTENT_TYPE,APPLICATION_JSON);

        WS.HttpResponse response = request.get();

        if (!response.success())
            throw new WebServiceException(response);

        Type type = new TypeToken<PessoaPortalSemaVO>(){}.getType();

        return gsonBuilder.create().fromJson(response.getJson(), type);

    }

    public CheckAuthenticateVO checarAutenticacao(String accessToken) {

        Map<String, String> params = new HashMap<>();
        params.put("access_token", accessToken);

        WS.WSRequest request = WS.url(CHECAR_AUTENTICACAO);

        WS.HttpResponse response = requestPost(params, request);

        Type type = new TypeToken<CheckAuthenticateVO>(){}.getType();

        return gsonBuilder.create().fromJson(response.getJson(), type);

    }

    public Boolean logoutAPI(String accessToken) {

        WS.WSRequest request = WS.url(LOGOUT);
        request.setHeader("Authorization","Bearer " + accessToken);
        request.setHeader(CONTENT_TYPE,APPLICATION_JSON);

        WS.HttpResponse response = request.get();

        if (!response.success())
            throw new WebServiceException(response);

        return true;

    }
}
