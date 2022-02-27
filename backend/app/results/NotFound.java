package results;

import play.mvc.Http;
import play.mvc.results.Result;

public class NotFound extends Result {

	public void apply(Http.Request request, Http.Response response) {

		response.status = Http.StatusCode.NOT_FOUND;
		response.setHeader("authorization", "Entrada Única");

	}

}
