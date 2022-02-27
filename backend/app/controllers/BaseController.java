package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deserializers.StringDeserializer;
import exceptions.OAuthRequestException;
import exceptions.PortalSegurancaException;
import exceptions.ValidationException;
import flexjson.JSONSerializer;
import org.hibernate.Session;
import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Controller;
import play.mvc.Http;
import results.Redirect;
import results.Unauthorized;
import serializers.DateSerializer;

import java.util.Collection;
import java.util.Date;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;


public class BaseController extends Controller {

	protected static Gson gson;
	protected static Integer HTTP_STATUS_UNPROCESSABLE_ENTITY = 422;

	static {

		GsonBuilder builder = new GsonBuilder()
				.serializeSpecialFloatingPointValues()
				.registerTypeAdapter(String.class, new StringDeserializer())
				.registerTypeAdapter(Date.class, new DateSerializer());

		gson = builder.create();

	}

	protected static boolean isPublicResource() {

		return request.path.contains(Play.configuration.getProperty("authentication.url.public"));

	}

	protected static boolean isExternalResource() {

		return request.path.contains(Play.configuration.getProperty("authentication.url.external"));

	}

	protected static boolean isOwnResource() {

		return !isPublicResource() && !isExternalResource();

	}

	protected static void renderJSON(Object model) {

		renderJSON(gson.toJson(model));

	}

	protected static void renderJSON(Object model, JSONSerializer js) {

		String json = js.serialize(model);

		renderJSON(json);

	}

	protected static void renderJSON(Collection<Object> models, JSONSerializer js) {

		String json = js.serialize(models);

		renderJSON(json);

	}

	protected static void renderImage(File file) throws IOException {

		file.createNewFile();
		String extension = FilenameUtils.getExtension(file.getPath());

		if(file.exists() || file.getAbsoluteFile().exists()){

			if(extension.compareTo("jpeg") == 0)
				response.setHeader("Content-Type", "image/jpg");
			if(extension.compareTo("gif") == 0)
				response.setHeader("Content-Type", "image/gif");
			if(extension.compareTo("bmp") == 0)
				response.setHeader("Content-Type", "image/bmp");
			if(extension.compareTo("png") == 0)
				response.setHeader("Content-Type", "image/png");
			if(extension.compareTo("jpg") == 0)
				response.setHeader("Content-Type", "image/jpg");

			renderBinary(file);

		}

	}



	@Catch(value = Exception.class, priority = 2)
	protected static void returnIfExceptionRaised(Throwable throwable) {

		Logger.error(throwable, throwable.getMessage());

		JPA.setRollbackOnly();

		if (throwable instanceof ValidationException){

			error(HTTP_STATUS_UNPROCESSABLE_ENTITY, ((ValidationException) throwable).getUserMessage());

		} else if (throwable instanceof OAuthRequestException) {

			error(Http.StatusCode.BAD_REQUEST, ((OAuthRequestException) throwable).getUserMessage());

		} else if (throwable instanceof PortalSegurancaException) {

			error(((PortalSegurancaException) throwable).getUserMessage());

		} else {

			error(Messages.get("erro.padrao"));

		}

	}

	protected static void unauthorized() {

		throw new Unauthorized();

	}

	protected static void frontEndRedirect(String url) {

		throw new Redirect(url);

	}

	@Before
	protected static void setFilters() {

//		((Session) JPA.em().getDelegate()).enableFilter("entityRemovida").setParameter("removido", false);

	}

}
