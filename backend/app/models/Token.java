package models;

import play.Play;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class Token {

	public String token_type;
	public String access_token;
	public String refresh_token;
	public String expires_in;

	public Token() {

		super();

	}

	public Token(String clientId, Boolean refreshToken) {

		super();

		this.access_token = generateToken(clientId);
		this.refresh_token = (Boolean.getBoolean(Play.configuration.getProperty("oAuth.token.refreshToken")) && refreshToken) ? generateToken(clientId) : null;
		this.expires_in = Play.configuration.getProperty("oAuth.token.expirationTime");
		this.token_type = Play.configuration.getProperty("oAuth.token.type");

	}

	private static String generateToken(String clientId) {

		return String.valueOf(new Date().getTime()) + UUID.randomUUID().toString().replaceAll("-", "") + clientId;

	}

}
