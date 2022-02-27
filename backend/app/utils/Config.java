package utils;

import java.io.File;

import play.Play;

public class Config {

	/* Configuracões de arquivos temporários */

	public static final String ARQUIVOS_PATH = Play.configuration.getProperty("arquivos.path");
	public static final String ARQUIVOS_PATH_LOGOTIPOS = Play.configuration.getProperty("arquivos.path.logotipos");
	public static final String APPLICATION_TEMP_FOLDER = Play.applicationPath + File.separator + Play.configuration.getProperty("application.tempFolder");
	public static final long TAMANHO_MAXIMO_ARQUIVO = getLongConfig("sistema.tamanhoMaximoArquivoUpload");

	public static final String PERFIL_PUBLICO = "Público";
	public static final String CODIGO_PERFIL_PUBLICO = "publico";
	public static final String CODIGO_PERFIL_GESTOR_EU = "gestorEu";
	public static final String CODIGO_PERFIL_PRODAP = "PRODAP";
	public static final String SIGLA_PORTAL = "GAA";
	public static final String SIGLA_CADASTRO = "MCU";
	public static final String SIGLA_ENTRADA_UNICA = "MEU";
	public static final String NOME_ENTRADA_UNICA = "Entrada Única";
	public static final String URL_CADASTRO_UNIFICADO = Play.configuration.getProperty("cadastroUnificado.url");
	public static final int ID_PORTAL = 1;
	public static final int ID_CADASTRO = 2;

	/*	Url de comunicação com API do portal da sema	*/
	public static final String API_PORTAL_SEMA_URL = Play.configuration.getProperty("api.portalSema.url");

	/*
	 * Métodos utilitários
	 */

	private static Long getLongConfig(String configKey) {

		String config = Play.configuration.getProperty(configKey);

		if(config != null && !config.isEmpty()) {

			return Long.parseLong(config);

		}

		return null;

	}

	private static int getIntConfig(String configKey) {

		String config = Play.configuration.getProperty(configKey);

		if(config != null && !config.isEmpty()) {

			return Integer.parseInt(config);

		}

		return 0;

	}

}
