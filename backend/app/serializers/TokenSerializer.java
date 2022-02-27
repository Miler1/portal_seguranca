package serializers;

import flexjson.JSONSerializer;
import play.Play;

public class TokenSerializer {

	public static JSONSerializer withoutRefreshToken;

	static {

		boolean prettyPrint = Play.mode == Play.Mode.DEV;

		withoutRefreshToken = new JSONSerializer()
				.include(
						"token_type",
						"access_token",
						"expires_in"
				)
				.exclude("*")
				.prettyPrint(prettyPrint);

	}

}